import java.sql.*;

public class VerifyExerciseTable {
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:fitness.db");
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("PRAGMA table_info(exercise)")) {
            System.out.println("exercise columns:");
            while (rs.next()) System.out.println(" - " + rs.getString("name"));
        }
        System.out.println("DB path: " + new java.io.File("fitness.db").getAbsolutePath());

    }
}
