package secnelis.bglparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class Parser {

	public Dict parse(File bglFile, String srcEncoding, String dstEncoding)
			throws IOException {
		// unzip
		File dir = new File("tmp");
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String unzippedFilename = "tmp/" + bglFile.getName() + ".unzipped";
		File unzippedFile = new File(unzippedFilename);
		if (!unzippedFile.exists()) {
			unzipBglFile(bglFile, unzippedFile);
		}
		// parse
		return parseUnzippedFile(unzippedFile, srcEncoding, dstEncoding);
	}

	private Dict parseUnzippedFile(File unzippedFile, String srcEncoding,
			String dstEncoding) throws IOException {
		// parse meta data and retrieve entry blocks
		FileInputStream unzippedStream = new FileInputStream(unzippedFile);
		List<BglBlock> entryBlocks = new ArrayList<BglBlock>();
		DictMetaInfo metaInfo = new DictMetaInfo(unzippedStream, entryBlocks);
		unzippedStream.close();

		Dict dict = new Dict(metaInfo.name, metaInfo.author);

		// parse entries
		for (BglBlock block : entryBlocks) {
			// block.dump("debug/lastword.bin");
			DictEntry entry = new DictEntry(block, srcEncoding, dstEncoding);
			dict.addEntry(entry);
		}

		return dict;
	}

	private void unzipBglFile(File bglFile, File dstFile) throws IOException {
		FileInputStream in = new FileInputStream(bglFile);
		byte[] buf = new byte[6];
		int pos = in.read(buf);
		// First four bytes: BGL signature 0x12340001 or 0x12340002
		// (big-endian)
		if (pos < 6
				|| (buf[0] == 0x12 && buf[1] == 0x34 && buf[2] == 0x00 && (buf[4] == 0x01 || buf[4] == 0x02))) {
			in.close();
			throw new IOException("Invalid file: no BGL signature: " + bglFile);
		}
		int gzipHeaderPos = (buf[4] & 0xFF) << 8 | (buf[5] & 0xFF);
		if (gzipHeaderPos < 6) {
			in.close();
			throw new IOException("No gzip ptr");
		}
		in.skip(gzipHeaderPos - 6);

		GZIPInputStream gzip = new GZIPInputStream(in);
		PrintStream out = new PrintStream(dstFile);
		byte[] gzipbuf = new byte[512];
		int gziplen;
		while ((gziplen = gzip.read(gzipbuf)) != -1) {
			out.write(gzipbuf, 0, gziplen);
		}
		out.close();
		gzip.close();

		in.close();
	}

}
