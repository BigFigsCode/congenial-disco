import java.sql.*;

public class FKCheck {
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:fitness.db");
             Statement s = c.createStatement()) {

            s.execute("PRAGMA foreign_keys = ON"); // ensure on for this session too

            // This should FAIL if workout.profile_id refers to a non-existent profile
            try {
                s.execute("INSERT INTO workout (profile_id, name) VALUES (9999,'Test');");
                System.out.println("❌ Expected FK failure but insert succeeded");
            } catch (SQLException ex) {
                System.out.println("✅ FK working: " + ex.getMessage());
            }
        }
    }
}
