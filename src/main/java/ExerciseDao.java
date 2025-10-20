import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExerciseDao {
    private static final String URL = "jdbc:sqlite:fitness.db";

    private static final String SQL_INSERT =
            "INSERT INTO exercise(profile_id, name, muscle, equipment, difficulty, type) " +
                    "VALUES (?,?,?,?,?,?)";

    private static final String SQL_FIND_BY_ID =
            "SELECT id, profile_id, name, muscle, equipment, difficulty, type " +
                    "FROM exercise WHERE id=?";

    private static final String SQL_GET_ALL =
            "SELECT id, profile_id, name, muscle, equipment, difficulty, type " +
                    "FROM exercise ORDER BY name";

    private static final String SQL_FIND_BY_MUSCLE =
            "SELECT id, profile_id, name, muscle, equipment, difficulty, type " +
                    "FROM exercise WHERE muscle=? ORDER BY name";

    // CREATE
    public int insert(Exercise e) {
        try (Connection c = DriverManager.getConnection(URL)) {
            enableFK(c);
            try (PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, e.getProfileId());
                ps.setString(2, e.getName());
                ps.setString(3, e.getMuscle());
                ps.setString(4, e.getEquipment());
                ps.setString(5, e.getDifficulty());
                ps.setString(6, e.getType());
                int rows = ps.executeUpdate();
                if (rows != 1) return 0;

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        int id = keys.getInt(1);
                        e.setId(id);
                        return id;
                    }
                }
                // Fallback if needed:
                try (Statement s = c.createStatement();
                     ResultSet rs = s.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        e.setId(id);
                        return id;
                    }
                }
                return 0;
            }
        } catch (SQLException ex) {
            System.out.println("❌ insert exercise failed: " + ex.getMessage());
            ex.printStackTrace();
            return 0;
        }
    }

    // READ by id
    public Optional<Exercise> findById(int id) {
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_ID)) {
            enableFK(c);
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException ex) {
            System.out.println("❌ findById failed: " + ex.getMessage());
            return Optional.empty();
        }
    }

    public List<Exercise> findByProfile(int profileId) {
        String sql = "SELECT id, profile_id, name, muscle, equipment, difficulty, type FROM exercise WHERE profile_id=? ORDER BY name";
        List<Exercise> out = new java.util.ArrayList<>();
        try (var c = java.sql.DriverManager.getConnection("jdbc:sqlite:fitness.db");
             var ps = c.prepareStatement(sql)) {
            ps.setInt(1, profileId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    Exercise e = new Exercise(
                            rs.getInt("profile_id"),
                            rs.getString("name"),
                            rs.getString("muscle"),
                            rs.getString("equipment"),
                            rs.getString("difficulty"),
                            rs.getString("type")
                    );
                    e.setId(rs.getInt("id"));
                    out.add(e);
                }
            }
        } catch (Exception ex) {
            System.out.println("❌ findByProfile failed: " + ex.getMessage());
        }
        return out;
    }


    // READ all
    public List<Exercise> getAll() {
        List<Exercise> out = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(URL);
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(SQL_GET_ALL)) {
            enableFK(c);
            while (rs.next()) out.add(mapRow(rs));
        } catch (SQLException ex) {
            System.out.println("❌ getAll failed: " + ex.getMessage());
        }
        return out;
    }

    // UPDATE
    public boolean update(Exercise e) {
        String sql = """
            UPDATE exercise
               SET profile_id=?, name=?, muscle=?, equipment=?, difficulty=?, type=?
             WHERE id=?
            """;
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            enableFK(c);
            ps.setInt(1, e.getProfileId());
            ps.setString(2, e.getName());
            ps.setString(3, e.getMuscle());
            ps.setString(4, e.getEquipment());
            ps.setString(5, e.getDifficulty());
            ps.setString(6, e.getType());
            ps.setInt(7, e.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            System.out.println("❌ update exercise failed: " + ex.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean delete(int id) {
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement("DELETE FROM exercise WHERE id=?")) {
            enableFK(c);
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            System.out.println("❌ delete exercise failed: " + ex.getMessage());
            return false;
        }
    }

    // Filters
    public List<Exercise> findByMuscle(String muscle) {
        List<Exercise> out = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_MUSCLE)) {
            enableFK(c);
            ps.setString(1, muscle);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            System.out.println("❌ findBy(muscle) failed: " + ex.getMessage());
        }
        return out;
    }

    static void enableFK(Connection c) {
        try (Statement s = c.createStatement()) { s.execute("PRAGMA foreign_keys = ON"); }
        catch (SQLException ignored) {}
    }

    private static Exercise mapRow(ResultSet rs) throws SQLException {
        Exercise e = new Exercise(
                rs.getInt("profile_id"),
                rs.getString("name"),
                rs.getString("muscle"),
                rs.getString("equipment"),
                rs.getString("difficulty"),
                rs.getString("type")
        );
        e.setId(rs.getInt("id"));
        return e;
    }
}
