package secnelis.bglparser;

import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		File bglFile = new File("dicts/Babylon_English_Chinese.bgl");
		Parser parser = new Parser();
		Dict dict = parser.parse(bglFile, "GBK", "GBK");
		System.out.println(dict);
	}

}
