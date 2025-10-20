import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.Statement;  // at the top with your imports


public class WorkoutDao {
    private static final String URL = "jdbc:sqlite:fitness.db";


    private static final String SQL_INSERT = """
        INSERT INTO workout(profile_id, name, day_of_week,
                            rest_between_sets_sec, rest_between_exercises_sec, active)
        VALUES(?,?,?,?,?,?)
        """;

    private static final String SQL_FIND_BY_ID = """
        SELECT id, profile_id, name, day_of_week,
               rest_between_sets_sec, rest_between_exercises_sec, active,
               created_at, updated_at
          FROM workout
         WHERE id=?
        """;

    private static final String SQL_GET_ALL = """
        SELECT id, profile_id, name, day_of_week,
               rest_between_sets_sec, rest_between_exercises_sec, active,
               created_at, updated_at
          FROM workout
         ORDER BY id
        """;

    private static final String SQL_GET_BY_PROFILE = """
        SELECT id, profile_id, name, day_of_week,
               rest_between_sets_sec, rest_between_exercises_sec, active,
               created_at, updated_at
          FROM workout
         WHERE profile_id=?
         ORDER BY COALESCE(day_of_week, 999), name
        """;

    private static final String SQL_GET_BY_PROFILE_AND_DAY = """
        SELECT id, profile_id, name, day_of_week,
               rest_between_sets_sec, rest_between_exercises_sec, active,
               created_at, updated_at
          FROM workout
         WHERE profile_id=? AND day_of_week=?
         ORDER BY name
        """;

    private static final String SQL_LIST_WITH_PROFILE_BY_PROFILE = """
    SELECT w.id, w.profile_id, p.name AS profile_name,
           w.name, w.day_of_week, w.rest_between_sets_sec,
           w.rest_between_exercises_sec, w.active,
           w.created_at, w.updated_at
      FROM workout w
      JOIN profile p ON p.id = w.profile_id
     WHERE w.profile_id = ?
     ORDER BY COALESCE(w.day_of_week, 999), w.name
""";



    public int insert(Workout w) {
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            enableFK(c);
            ps.setInt(1, w.getProfileId());
            ps.setString(2, w.getName());
            if (w.getDayOfWeek() == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, w.getDayOfWeek());
            ps.setInt(4, w.getRestBetweenSetsSec());
            ps.setInt(5, w.getRestBetweenExercisesSec());
            ps.setInt(6, w.isActive() ? 1 : 0);

            int rows = ps.executeUpdate();
            if (rows != 1) return 0;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    w.setId(id);
                    return id;
                }
            }
            return 0;
        } catch (SQLException ex) {
            System.out.println("❌ insert workout failed: " + ex.getMessage());
            return 0;
        }
    }

    public Optional<Workout> findById(int id) {
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

    public List<Workout> getAll() {
        List<Workout> out = new ArrayList<>();
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

    public List<Workout> getByProfile(int profileId) {
        List<Workout> out = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(SQL_GET_BY_PROFILE)) {
            enableFK(c);
            ps.setInt(1, profileId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            System.out.println("❌ getByProfile failed: " + ex.getMessage());
        }
        return out;
    }

    public List<WorkoutWithProfile> listWithProfileByProfile(int profileId) {
        List<WorkoutWithProfile> out = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(SQL_LIST_WITH_PROFILE_BY_PROFILE)) {
            ExerciseDao.enableFK(c);
            ps.setInt(1, profileId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapWithProfile(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ listWithProfileByProfile failed: " + e.getMessage());
        }
        return out;
    }

    private static WorkoutWithProfile mapWithProfile(ResultSet rs) throws SQLException {
        Integer day = (rs.getObject("day_of_week") == null ? null : rs.getInt("day_of_week"));
        return new WorkoutWithProfile(
                rs.getInt("id"),
                rs.getInt("profile_id"),
                rs.getString("profile_name"),
                rs.getString("name"),
                day,
                rs.getInt("rest_between_sets_sec"),
                rs.getInt("rest_between_exercises_sec"),
                rs.getInt("active") == 1,
                rs.getString("created_at"),
                rs.getString("updated_at")
        );

    }

    public List<Workout> getByProfileAndDay(int profileId, int dayOfWeek) {
        List<Workout> out = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(SQL_GET_BY_PROFILE_AND_DAY)) {
            enableFK(c);
            ps.setInt(1, profileId);
            ps.setInt(2, dayOfWeek);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        } catch (SQLException ex) {
            System.out.println("❌ getByProfileAndDay failed: " + ex.getMessage());
        }
        return out;
    }

    public boolean update(Workout w) {
        String sql = """
            UPDATE workout
               SET profile_id=?, name=?, day_of_week=?,
                   rest_between_sets_sec=?, rest_between_exercises_sec=?,
                   active=?, updated_at=datetime('now')
             WHERE id=?
            """;
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            enableFK(c);
            ps.setInt(1, w.getProfileId());
            ps.setString(2, w.getName());
            if (w.getDayOfWeek() == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, w.getDayOfWeek());
            ps.setInt(4, w.getRestBetweenSetsSec());
            ps.setInt(5, w.getRestBetweenExercisesSec());
            ps.setInt(6, w.isActive() ? 1 : 0);
            ps.setInt(7, w.getId());
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            System.out.println("❌ update workout failed: " + ex.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement("DELETE FROM workout WHERE id=?")) {
            enableFK(c);
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            System.out.println("❌ delete workout failed: " + ex.getMessage());
            return false;
        }
    }

    private static void enableFK(Connection c) {
        try (Statement s = c.createStatement()) { s.execute("PRAGMA foreign_keys = ON"); }
        catch (SQLException ignored) {}
    }

    private static Workout mapRow(ResultSet rs) throws SQLException {
        return new Workout(
                rs.getInt("id"),
                rs.getInt("profile_id"),
                rs.getString("name"),
                (rs.getObject("day_of_week") == null ? null : rs.getInt("day_of_week")),
                rs.getInt("rest_between_sets_sec"),
                rs.getInt("rest_between_exercises_sec"),
                rs.getInt("active") == 1,
                rs.getString("created_at"),
                rs.getString("updated_at")
        );
    }
}
