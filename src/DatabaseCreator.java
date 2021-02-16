import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DatabaseCreator {

	private String url;

	public DatabaseCreator() {
		this.url = createNewDatabase("BooksDatabase.db");
		createArticlesTable();
	}

	private Connection connect(String url) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return conn;
	}

	public void insertIfNotExists(String ean, String name, String normalizedName, String price) {
		String sql = "INSERT INTO articles(ean, name, normalizedName, price) VALUES (?, ?, ?, ?) WHERE NOT EXISTS(SELECT ean FROM articles where )";
		try(Connection conn = this.connect(url); PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setString(1, ean);
			pstmt.setString(2, name);
			pstmt.setString(3, normalizedName);
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	private void insert(String ean, String name, String normalizedName, String price) {
		String sql = "INSERT OR IGNORE INTO articles(ean , name, normalizedName, price) VALUES (?, ?, ?, ?)";
		try (Connection conn = this.connect(url); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, ean);
			pstmt.setString(2, name);
			pstmt.setString(3, normalizedName);
			pstmt.setString(4, price);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String createNewDatabase(String fileName) {
		String url = "jdbc:sqlite:" + fileName;
		try (Connection conn = this.connect(url)) {
			if (conn != null) {
				DatabaseMetaData meta = conn.getMetaData();
				System.out.println("The driver name is " + meta.getDriverName());
				System.out.println("A new database has been created.");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return url;
	}

	public void createArticlesTable() {
		// System.out.println("Vytvarim table articles");
		String sql = "CREATE TABLE IF NOT EXISTS articles (\n" + " ean text NOT NULL PRIMARY KEY, \n"
				+ " name text NOT NULL, \n" + " normalizedName text NOT NULL, \n" + " price text NOT NULL\n" + ");";

		try (Connection conn = this.connect(url); Statement stmt = conn.createStatement()) {
			stmt.execute(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertBatch(List<Item> items) {
//		 System.out.println("Vkladam data do articles");
		String sql = "INSERT OR IGNORE INTO articles(ean, name, normalizedName, price) VALUES (?, ?, ?, ?)";

		try (Connection conn = this.connect(url); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			conn.setAutoCommit(false);
			for (Item item : items) {
				pstmt.setString(1, item.getEan());
				pstmt.setString(2, item.getName());
				pstmt.setString(3, item.getNormalizedName());
				pstmt.setString(4, item.getPrice());
				pstmt.addBatch();
			}

			int[] result = pstmt.executeBatch();
			System.out.println(result.length);
			conn.commit();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createFtsTableAndFillIt() {
		// System.out.println("Vytvarim fts a vkladam data");
		String ftsSql = "CREATE VIRTUAL TABLE fts_books_names USING FTS4(ean, name, normalizedName);";
		String insertData = "INSERT INTO fts_books_names SELECT ean, name, normalizedName FROM articles;";

		try (Connection conn = this.connect(url); Statement stmt = conn.createStatement()) {
			stmt.execute(ftsSql);
			stmt.execute(insertData);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
