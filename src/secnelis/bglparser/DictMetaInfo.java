package secnelis.bglparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DictMetaInfo {

	public String name;
	public String author;

	public String defaultCharset;

	public String srcLng;
	public String srcEnc;
	public String srcEncName;

	public String dstLng;
	public String dstEnc;
	public String dstEncName;

	public DictMetaInfo(InputStream s, List<BglBlock> entryBlocks) throws IOException {
		String headword = "";
		int type = -1;
		while (true) {
			headword = "";
			BglBlock block = BglBlock.read(s);
			if (block == null) {
				break;
			}
			if (block.type == 0 && block.data[0] == 8) {
				type = block.data[1];
				if (type > 64)
					type -= 65;
				this.defaultCharset = BglConstants.Bgl_charset[type];
			} else if (block.type == 1 || block.type == 10) {
				entryBlocks.add(block);
			} else if (block.type == 3) {
				int pos = 2;
				switch (block.data[1]) {
				case 1:
					for (int a = 0; a < block.length - 2; a++)
						headword += (char) block.data[pos++];
					this.name = headword;
					break;
				case 2:
					for (int a = 0; a < block.length - 2; a++)
						headword += (char) block.data[pos++];
					this.author = headword;
					break;
				case 7:
					this.srcLng = BglConstants.Bgl_language[block.data[5]];
					break;
				case 8:
					this.dstLng = BglConstants.Bgl_language[block.data[5]];
					break;
				case 26:
					type = block.data[2];
					if (type > 64)
						type -= 65;
					this.srcEnc = BglConstants.Bgl_charset[type];
					this.srcEncName = BglConstants.Bgl_charsetname[type];
					break;
				case 27:
					type = block.data[2];
					if (type > 64)
						type -= 65;
					this.dstEnc = BglConstants.Bgl_charset[type];
					this.dstEncName = BglConstants.Bgl_charsetname[type];
					break;
				}
			} else
				continue;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BglMetaData [\r\n\tname = ");
		builder.append(name);
		builder.append("\r\n\tauthor = ");
		builder.append(author);
		builder.append("\r\n\tdefaultCharset = ");
		builder.append(defaultCharset);
		builder.append("\r\n\tsrcLng = ");
		builder.append(srcLng);
		builder.append("\r\n\tsrcEnc = ");
		builder.append(srcEnc);
		builder.append("\r\n\tsrcEncName = ");
		builder.append(srcEncName);
		builder.append("\r\n\tdstLng = ");
		builder.append(dstLng);
		builder.append("\r\n\tdstEnc = ");
		builder.append(dstEnc);
		builder.append("\r\n\tdstEncName = ");
		builder.append(dstEncName);
		builder.append("\r\n]");
		return builder.toString();
	}

}
