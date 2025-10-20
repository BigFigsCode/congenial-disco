import java.sql.*;

public class DatabaseTest {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:fitness.db"; // Will create fitness.db in your project folder

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("✅ Connected to the database!");

                Statement stmt = conn.createStatement();
                stmt.execute("CREATE TABLE IF NOT EXISTS profile (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT," +
                        "level TEXT," +
                        "goal TEXT," +
                        "days_per_week INTEGER)");

                System.out.println("✅ Table 'profile' created successfully.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }
}
