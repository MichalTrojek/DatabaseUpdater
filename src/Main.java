import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.sqlite.SQLiteConfig.Encoding;

public class Main {

	public static void main(String args[]) {
		 convertOldWay();
		// updateDatabase();
		// createCsvFile();
		//convertFromTwoFilesToDatabase(readUpdateFile());
	}

	public static void convertFromTwoFilesToDatabase(List<Item> updateItems) {

		File input = new File("data.csv");
		File output = new File("output.csv");
		List<Item> items = new ArrayList<>();

		Set<Item> finalItems = new HashSet<>();

		for (Item item : updateItems) {
			finalItems.add(item);
		}

		try {
			CSVParser parser = CSVParser.parse(input, Charset.forName("Windows-1250"),
					CSVFormat.RFC4180.withDelimiter(';').withQuote('"').withRecordSeparator("\r\n")
							.withIgnoreEmptyLines(false).withIgnoreSurroundingSpaces().withFirstRecordAsHeader()
							.withHeader("ean", "name", "price"));

			parser.forEach(csvRecord -> {
				if (csvRecord.size() == 3) {
					String ean = csvRecord.get("ean");
					String name = csvRecord.get("name");
					String price = csvRecord.get("price");
					String normalizedName = Normalizer.normalize(name, Normalizer.Form.NFD)
							.replaceAll("[^\\p{ASCII}]", "").toLowerCase();
					items.add(new Item(ean, name, price, normalizedName));
				} else {

				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Items before " + finalItems.size());
		for (Item item : items) {
			finalItems.add(item);
		}
		System.out.println("Items after " + finalItems.size());
		System.out.println("ITEMS " + items.size());
		items.clear();
		for (Item item : finalItems) {
			items.add(item);
		}

		System.out.println(items.size());

		//
		DatabaseCreator db = new DatabaseCreator();
		System.out.println(items.size() + "Itens");
		db.insertBatch(items);

		db.createFtsTableAndFillIt();
		System.out.println("HOtovo");

	}

	public static void createCsvFile() {
		List<Item> items = readUpdateFile();
		String[] HEADERS = { "ean", "name", "normalizedname", "price" };
		try (CSVPrinter printer = new CSVPrinter(
				new OutputStreamWriter(new FileOutputStream("importToAzure.csv"), StandardCharsets.UTF_8),
				CSVFormat.POSTGRESQL_CSV.withHeader(HEADERS))) {
			for (Item item : items) {
				if (item.getEan().isEmpty() || item.getName().isEmpty()) {
					continue;
				}
				printer.printRecord(item.getEan(), item.getName(), item.getNormalizedName(), item.getPrice());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void updateDatabase() {
		List<Item> items = readUpdateFile();

		DatabaseCreator db = new DatabaseCreator();
		db.insertBatch(items);

	}

	private static List<Item> readUpdateFile() {
		List<Item> items = new ArrayList<>();
		File input = new File("update.csv");
		try {
			CSVParser parser = CSVParser.parse(input, Charset.forName("Windows-1250"),
					CSVFormat.RFC4180.withDelimiter(';').withQuote('"').withRecordSeparator("\r\n")
							.withIgnoreEmptyLines(false).withIgnoreSurroundingSpaces().withFirstRecordAsHeader()
							.withHeader("KOD2", "STAV", "NAZEV", "REGAL", "CENA_SDA", "KOD", "STAV_KONS", "POSL_NAKUP",
									"STAV001", "STAV_CELK"));

			parser.forEach(csvRecord -> {

				// System.out.println(csvRecord.get01));
				String ean = csvRecord.get("KOD2");
				String name = csvRecord.get("NAZEV");
				String price = csvRecord.get("CENA_SDA");
				String normalizedName = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
						.toLowerCase();

				// System.out.println(ean + " " + name + " " + price + " " + normalizedName);
				items.add(new Item(ean, name, price, normalizedName));

			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		return items;
	}

	public static void convertOldWay() {

		File input = new File("data.csv");
		File output = new File("output.csv");
		List<Item> items = new ArrayList<>();

		try {
			CSVParser parser = CSVParser.parse(input, Charset.forName("Windows-1250"),
					CSVFormat.RFC4180.withDelimiter(';').withQuote('"').withRecordSeparator("\r\n")
							.withIgnoreEmptyLines(false).withIgnoreSurroundingSpaces().withFirstRecordAsHeader()
							.withHeader("ean", "name", "price"));

			parser.forEach(csvRecord -> {
				if (csvRecord.size() == 3) {
					String ean = csvRecord.get("ean");
					String name = csvRecord.get("name");
					String price = csvRecord.get("price");
					String normalizedName = Normalizer.normalize(name, Normalizer.Form.NFD)
							.replaceAll("[^\\p{ASCII}]", "").toLowerCase();
					items.add(new Item(ean, name, price, normalizedName));
				} else {

				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		DatabaseCreator db = new DatabaseCreator();
		System.out.println(items.size() + "Itens");
		db.insertBatch(items);

		db.createFtsTableAndFillIt();
		System.out.println("HOtovo");

	}
}
