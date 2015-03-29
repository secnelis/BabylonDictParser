package secnelis.bglparser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class DictEntry {

	public String headWord;

	public String definition;
	public int wordClass;
	public String displayWord;
	public String phonetics;

	public List<String> alternates = new ArrayList<String>();

	private BglBlock block;
	private String srcEncoding;
	private String dstEncoding;

	public DictEntry(BglBlock block, String srcEncoding, String dstEncoding)
			throws IOException {
		this.block = block;
		this.srcEncoding = srcEncoding;
		this.dstEncoding = dstEncoding;
		parse();
	}

	private void parse() throws IOException {
		int len = 0;
		int pos = 0;
		// Head
		len = block.data[pos++] & 0xFF;
		headWord = readString(block.data, pos, len, srcEncoding);
		pos += len;

		// Definition
		len = (block.data[pos++] & 0xFF) << 8 | (block.data[pos++] & 0xFF);
		byte[] defdata = new byte[len];
		System.arraycopy(block.data, pos, defdata, 0, len);
		parseDefinitionPart(defdata);
		pos += len;

		// Alternates
		while (pos < block.length) {
			len = block.data[pos++] & 0xFF;
			String alternate = readString(block.data, pos, len, srcEncoding);
			pos += len;
			alternates.add(alternate);
		}
	}

	private void parseDefinitionPart(byte[] defdata) throws IOException {
		int deflen = defdata.length;
		for (int i = 0; i < defdata.length; i++) {
			if (defdata[i] == 0x14) {
				deflen = i;
				break;
			}
		}
		definition = readString(defdata, 0, deflen, dstEncoding);
		if (deflen < defdata.length) {
			int metastart = deflen + 1;
			int metalen = defdata.length - metastart;
			byte[] metadata = new byte[metalen];
			System.arraycopy(defdata, metastart, metadata, 0, metalen);
			parseDefinitionMetaInfo(metadata);
		}
	}

	private void parseDefinitionMetaInfo(byte[] data) throws IOException {
		int len = data.length;
		int pos = 0;
		int specifier, blocklen;
		while (true) {
			if (pos >= len) {
				break;
			}
			int what = readNum(data, pos++, 1);
			switch (what) {
			case 0x02: // 1 byte WORD_CLASS
				wordClass = readNum(data, pos++, 1);
				break;
			case 0x18: // BLOCK(1) as DISPLAY_WORD
				blocklen = readNum(data, pos++, 1);
				displayWord = readString(data, pos, blocklen, srcEncoding);
				pos += blocklen;
				break;
			case 0x50: // 1 byte specifier, followed by BLOCK_A(1)
				specifier = readNum(data, pos++, 1);
				blocklen = readNum(data, pos++, 1);
				parseBlockAB(specifier, data, pos, blocklen);
				pos += blocklen;
				break;
			case 0x60: // 1 byte specifier, followed by BLOCK_A(2)
				specifier = readNum(data, pos++, 1);
				blocklen = readNum(data, pos, 2);
				pos += 2;
				parseBlockAB(specifier, data, pos, blocklen);
				pos += blocklen;
				break;
			case 0x06: // 1 byte UNKNOWN (possibly concerning alternatives)
				pos++;
				break;
			case 0x13: // 1 byte UNKNOWN
				pos++;
				break;
			case 0xC7: // no data UNKNOWN
				break;
			default:
				int highnibble = (what & 0xF0) >> 4;
				if (highnibble == 4) {
					int n = what & 0xF;
					specifier = readNum(data, pos++, 1);
					blocklen = n + 1;
					parseBlockAB(specifier, data, pos, blocklen);
					pos += blocklen;
				}
				// \x4N: BLOCK_B(N+1)
				break;
			}
		}
	}

	private void parseBlockAB(int specifier, byte[] data, int offset, int len) {
		if (specifier == 0x1B) { // PHONETICS
			phonetics = readString(data, offset, len, srcEncoding);
		} else if (specifier == 0x18) { // WORD_VARIATION (to be studied)
		}
	}

	private int readNum(byte[] data, int offset, int bytes) throws IOException {
		int val = 0;
		for (int i = 0; i < bytes; i++) {
			val = (val << 8) | (data[offset + i] & 0xFF);
		}
		return val;
	}

	private static String readString(byte[] data, int pos, int len,
			String charset) {
		try {
			return new String(data, pos, len, charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Entry [headWord=");
		builder.append(headWord);
		builder.append(", displayWord=");
		builder.append(displayWord);
		builder.append(", phonetics=");
		builder.append(phonetics);
		builder.append(", definition=");
		builder.append(definition);
		builder.append(", wordClass=");
		builder.append(wordClass);
		builder.append(", alternates=");
		builder.append(alternates);
		builder.append("]");
		return builder.toString();
	}

}
