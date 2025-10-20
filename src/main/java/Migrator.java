import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Objects;
import java.util.stream.Collectors;

public class Migrator {
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:fitness.db")) {
            conn.setAutoCommit(false);

            try (Statement st = conn.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON;");

                String sql = new BufferedReader(new InputStreamReader(
                        Objects.requireNonNull(Migrator.class.getResourceAsStream("/schema.sql")),
                        StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));

                // Remove single-line comments and split on ';'
                for (String raw : sql.replaceAll("(?m)^\\s*--.*$", "").split(";")) {
                    String s = raw.trim();
                    if (s.isEmpty()) continue;
                    try {
                        st.execute(s);
                    } catch (SQLException e) {
                        // Ignore harmless reruns
                        String msg = e.getMessage().toLowerCase();
                        if (msg.contains("already exists")) {
                            // ok to ignore on rerun
                        } else {
                            throw e;
                        }
                    }
                }
            }

            conn.commit();
            System.out.println("✅ Schema applied successfully.");
        }
    }
}
