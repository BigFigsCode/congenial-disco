

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfileDao {

    private static final String URL = "jdbc:sqlite:fitness.db";

    // INSERT a new profile
    // CREATE: insert and return generated id
    public int insert(Profile profile) {
        String sql = "INSERT INTO profile(name, level, goal, days_per_week) VALUES(?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, profile.getName());
            ps.setString(2, profile.getLevel());
            ps.setString(3, profile.getGoal());
            ps.setInt(4, profile.getDaysPerWeek());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);     // SQLite supports this
                    profile.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ insert failed: " + e.getMessage());
        }
        return 0;
    }


    // Quick get-or-create by name (optional convenience)
    public int getOrCreateByName(String name, String level, String goal, int daysPerWeek) {
        String find = "SELECT id FROM profile WHERE name=?";
        try (var c = java.sql.DriverManager.getConnection("jdbc:sqlite:fitness.db");
             var ps = c.prepareStatement(find)) {
            ps.setString(1, name);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception ignored) {}

        String ins = "INSERT INTO profile(name, level, goal, days_per_week) VALUES (?,?,?,?)";
        try (var c = java.sql.DriverManager.getConnection("jdbc:sqlite:fitness.db");
             var ps = c.prepareStatement(ins, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, level);
            ps.setString(3, goal);
            ps.setInt(4, daysPerWeek);
            ps.executeUpdate();
            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }


    // SELECT all profiles
    public List<Profile> getAllProfiles() {
        List<Profile> profiles = new ArrayList<>();
        String sql = "SELECT * FROM profile";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Profile p = new Profile(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("level"),
                        rs.getString("goal"),
                        rs.getInt("days_per_week")
                );
                profiles.add(p);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error reading profiles: " + e.getMessage());
        }
        return profiles;
    }

    // READ: find by id
    public Optional<Profile> findById(int id) {
        String sql = "SELECT id, name, level, goal, days_per_week FROM profile WHERE id=?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ findById failed: " + e.getMessage());
        }
        return Optional.empty();
    }

    // READ: all
    public List<Profile> getAll() {
        List<Profile> out = new ArrayList<>();
        String sql = "SELECT id, name, level, goal, days_per_week FROM profile ORDER BY id";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) out.add(mapRow(rs));
        } catch (SQLException e) {
            System.out.println("❌ getAll failed: " + e.getMessage());
        }
        return out;
    }

    // UPDATE: returns true if exactly one row updated
    public boolean update(Profile p) {
        String sql = """
            UPDATE profile
               SET name=?, level=?, goal=?, days_per_week=?
             WHERE id=?
            """;
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getLevel());
            ps.setString(3, p.getGoal());
            ps.setInt(4, p.getDaysPerWeek());
            ps.setInt(5, p.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("❌ update failed: " + e.getMessage());
            return false;
        }
    }

    // DELETE: returns true if exactly one row deleted
    public boolean delete(int id) {
        String sql = "DELETE FROM profile WHERE id=?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("❌ delete failed: " + e.getMessage());
            return false;
        }
    }

    // Helper to map a ResultSet row to Profile
    private static Profile mapRow(ResultSet rs) throws SQLException {
        return new Profile(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("level"),
                rs.getString("goal"),
                rs.getInt("days_per_week")
        );
        // If you later add created_at/updated_at, extend this mapper.
    }
}
