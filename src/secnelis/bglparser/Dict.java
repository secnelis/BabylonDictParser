package secnelis.bglparser;

import java.util.ArrayList;
import java.util.List;

public class Dict {

	private String name;

	private String author;

	private final List<DictEntry> entries = new ArrayList<DictEntry>();

	public Dict(String name, String author) {
		this.name = name;
		this.author = author;
	}

	public String title() {
		return name;
	}

	public String author() {
		return author;
	}

	public List<DictEntry> entries() {
		return entries;
	}

	public void addEntry(DictEntry entry) {
		entries.add(entry);
	}

	@Override
	public String toString() {
		return name + "\r\n" + author + "\r\n" + entries.size() + " entries";
	}
}
