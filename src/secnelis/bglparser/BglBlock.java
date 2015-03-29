package secnelis.bglparser;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class BglBlock {

	public static BglBlock read(InputStream s) throws IOException {
		int len = readNum(s, 1);
		int type = len & 0xF;
		if (type == 4) {
			return null;
		} // end-of-file marker
		len >>= 4;
		len = len < 4 ? readNum(s, len + 1) : len - 4;
		byte[] data = new byte[0];
		if (len > 0) {
			data = new byte[len];
			s.read(data, 0, len);
		}
		return new BglBlock(type, len, data);
	}

	static int readNum(InputStream s, int bytes) throws IOException {
		byte[] buf = new byte[4];
		if (bytes < 1 || bytes > 4) {
			throw new IllegalArgumentException("Must be between 1 and 4 bytes");
		}
		s.read(buf, 0, bytes);
		int val = 0;
		for (int i = 0; i < bytes; i++) {
			val = (val << 8) | (buf[i] & 0xFF);
		}
		return val;
	}

	public final int type;
	public final int length;
	public final byte[] data;

	private BglBlock(int type, int length, byte[] data) {
		this.type = type;
		this.length = length;
		this.data = data;
	}

	public void dump(String file) throws IOException {
		PrintStream out = new PrintStream(file);
		out.write(data);
		out.close();
	}

	@Override
	public String toString() {
		return "BglBlock [type=" + type + ", length=" + length + ", data="
				+ Arrays.toString(data) + "]";
	}

}
