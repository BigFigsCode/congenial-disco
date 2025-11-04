
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;




public class SessionDao {
    private static final String URL = "jdbc:sqlite:fitness.db";

    private static void enableFK(Connection c) {
        try (Statement s = c.createStatement()) { s.execute("PRAGMA foreign_keys = ON"); }
        catch (SQLException ignored) {}
    }

    private static final String SQL_INSERT = """
        INSERT INTO session(profile_id, workout_id, date_iso, total_minutes, rpe, notes)
        VALUES(?,?,?,?,?,?)
        """;

    private static final String SQL_FIND_BY_ID = """
        SELECT id, profile_id, workout_id, date_iso, total_minutes, rpe, notes
          FROM session
         WHERE id=?
        """;

    private static final String SQL_GET_BY_PROFILE = """
        SELECT id, profile_id, workout_id, date_iso, total_minutes, rpe, notes
          FROM session
         WHERE profile_id=?
         ORDER BY date_iso DESC
        """;

    private static final String SQL_GET_BY_PROFILE_RANGE = """
        SELECT id, profile_id, workout_id, date_iso, total_minutes, rpe, notes
          FROM session
         WHERE profile_id=? AND date_iso BETWEEN ? AND ?
         ORDER BY date_iso
        """;

    private static final String SQL_GET_BY_WORKOUT = """
        SELECT id, profile_id, workout_id, date_iso, total_minutes, rpe, notes
          FROM session
         WHERE workout_id=?
         ORDER BY date_iso DESC
        """;

    public int insert(Session s) {
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            enableFK(c);
            ps.setInt(1, s.getProfileId());
            if (s.getWorkoutId() == null) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, s.getWorkoutId());
            ps.setString(3, s.getDateIso());
            ps.setInt(4, s.getTotalMinutes());
            if (s.getRpe() == null) ps.setNull(5, Types.INTEGER);
            else ps.setInt(5, s.getRpe());
            ps.setString(6, s.getNotes()); // notes can be null — ok to pass null

            int rows = ps.executeUpdate();
            if (rows != 1) return 0;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    s.setId(id);
                    return id;
                }
            }
            return 0;
        } catch (SQLException e) {
            System.out.println("❌ insert session failed: " + e.getMessage());
            return 0;
        }
    }

    public Optional<Session> findById(int id) {
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_ID)) {
            enableFK(c);
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            System.out.println("❌ findById failed: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Session> getByProfile(int profileId) {
        List<Session> out = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(SQL_GET_BY_PROFILE)) {
            enableFK(c);
            ps.setInt(1, profileId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ getByProfile failed: " + e.getMessage());
        }
        return out;
    }

    public List<Session> getByProfileAndRange(int profileId, String fromIso, String toIso) {
        List<Session> out = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(SQL_GET_BY_PROFILE_RANGE)) {
            enableFK(c);
            ps.setInt(1, profileId);
            ps.setString(2, fromIso);
            ps.setString(3, toIso);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ getByProfileAndRange failed: " + e.getMessage());
        }
        return out;
    }

    public List<Session> getByWorkout(int workoutId) {
        List<Session> out = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(SQL_GET_BY_WORKOUT)) {
            enableFK(c);
            ps.setInt(1, workoutId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ getByWorkout failed: " + e.getMessage());
        }
        return out;
    }

    public boolean update(Session s) {
        String sql = """
            UPDATE session
               SET profile_id=?, workout_id=?, date_iso=?,
                   total_minutes=?, rpe=?, notes=?
             WHERE id=?
            """;
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            enableFK(c);
            ps.setInt(1, s.getProfileId());
            if (s.getWorkoutId() == null) ps.setNull(2, Types.INTEGER);
            else ps.setInt(2, s.getWorkoutId());
            ps.setString(3, s.getDateIso());
            ps.setInt(4, s.getTotalMinutes());
            if (s.getRpe() == null) ps.setNull(5, Types.INTEGER);
            else ps.setInt(5, s.getRpe());
            ps.setString(6, s.getNotes());
            ps.setInt(7, s.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("❌ update session failed: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement("DELETE FROM session WHERE id=?")) {
            enableFK(c);
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("❌ delete session failed: " + e.getMessage());
            return false;
        }
    }

    private static Session mapRow(ResultSet rs) throws SQLException {
        Integer workoutId = (rs.getObject("workout_id") == null ? null : rs.getInt("workout_id"));
        Integer rpe = (rs.getObject("rpe") == null ? null : rs.getInt("rpe"));
        String notes = rs.getString("notes"); // may be null
        return new Session(
                rs.getInt("id"),
                rs.getInt("profile_id"),
                workoutId,
                rs.getString("date_iso"),
                rs.getInt("total_minutes"),
                rpe,
                notes
        );
    }
}
