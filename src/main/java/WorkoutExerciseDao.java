
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WorkoutExerciseDao {
    private static final String URL = "jdbc:sqlite:fitness.db";

    private static void enableFK(Connection c) {
        try (Statement s = c.createStatement()) { s.execute("PRAGMA foreign_keys = ON"); }
        catch (SQLException ignored) {}
    }

    // INSERT a single row
    public boolean insert(WorkoutExercise we) {
        final String sql = """
            INSERT INTO workout_exercise(workout_id, exercise_id, order_idx, target_sets, target_reps_or_secs)
            VALUES(?,?,?,?,?)
            """;
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            enableFK(c);
            ps.setInt(1, we.getWorkoutId());
            ps.setInt(2, we.getExerciseId());
            ps.setInt(3, we.getOrderIdx());
            ps.setInt(4, we.getTargetSets());
            ps.setInt(5, we.getTargetRepsOrSecs());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("❌ insert workout_exercise failed: " + e.getMessage());
            return false;
        }
    }

    // BULK INSERT (transactional)
    public boolean insertAll(List<WorkoutExercise> rows) {
        if (rows == null || rows.isEmpty()) return true;
        final String sql = """
            INSERT INTO workout_exercise(workout_id, exercise_id, order_idx, target_sets, target_reps_or_secs)
            VALUES(?,?,?,?,?)
            """;
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            enableFK(c);
            c.setAutoCommit(false);
            for (WorkoutExercise we : rows) {
                ps.setInt(1, we.getWorkoutId());
                ps.setInt(2, we.getExerciseId());
                ps.setInt(3, we.getOrderIdx());
                ps.setInt(4, we.getTargetSets());
                ps.setInt(5, we.getTargetRepsOrSecs());
                ps.addBatch();
            }
            ps.executeBatch();
            c.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("❌ insertAll workout_exercise failed: " + e.getMessage());
            return false;
        }
    }

    // READ: all rows for a workout (ordered by order_idx)
    public List<WorkoutExercise> getByWorkout(int workoutId) {
        final String sql = """
            SELECT workout_id, exercise_id, order_idx, target_sets, target_reps_or_secs
              FROM workout_exercise
             WHERE workout_id=?
             ORDER BY order_idx
            """;
        List<WorkoutExercise> out = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            enableFK(c);
            ps.setInt(1, workoutId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new WorkoutExercise(
                            rs.getInt("workout_id"),
                            rs.getInt("exercise_id"),
                            rs.getInt("order_idx"),
                            rs.getInt("target_sets"),
                            rs.getInt("target_reps_or_secs")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ getByWorkout failed: " + e.getMessage());
        }
        return out;
    }

    // READ (JOIN): rows with exercise name/muscle for GUI display
    public List<WorkoutExerciseView> getByWorkoutWithExercise(int workoutId) {
        final String sql = """
            SELECT we.workout_id, we.exercise_id, we.order_idx,
                   we.target_sets, we.target_reps_or_secs,
                   e.name AS exercise_name, e.muscle, e.equipment, e.type, e.difficulty
              FROM workout_exercise we
              JOIN exercise e ON e.id = we.exercise_id
             WHERE we.workout_id=?
             ORDER BY we.order_idx
            """;
        List<WorkoutExerciseView> out = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            enableFK(c);
            ps.setInt(1, workoutId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new WorkoutExerciseView(
                            rs.getInt("workout_id"),
                            rs.getInt("exercise_id"),
                            rs.getInt("order_idx"),
                            rs.getInt("target_sets"),
                            rs.getInt("target_reps_or_secs"),
                            rs.getString("exercise_name"),
                            rs.getString("muscle"),
                            rs.getString("equipment"),
                            rs.getString("difficulty"),
                            rs.getString("type")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ getByWorkoutWithExercise failed: " + e.getMessage());
        }
        return out;
    }

    // UPDATE targets (sets / reps_or_secs) for one row
    public boolean updateTargets(int workoutId, int orderIdx, int targetSets, int targetRepsOrSecs) {
        final String sql = """
            UPDATE workout_exercise
               SET target_sets=?, target_reps_or_secs=?
             WHERE workout_id=? AND order_idx=?
            """;
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            enableFK(c);
            ps.setInt(1, targetSets);
            ps.setInt(2, targetRepsOrSecs);
            ps.setInt(3, workoutId);
            ps.setInt(4, orderIdx);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("❌ updateTargets failed: " + e.getMessage());
            return false;
        }
    }

    // REORDER: swap two positions in a workout
    public boolean swapOrder(int workoutId, int aIdx, int bIdx) {
        final String getMaxSql = "SELECT COALESCE(MAX(order_idx),0) FROM workout_exercise WHERE workout_id=?";
        final String updSql = "UPDATE workout_exercise SET order_idx=? WHERE workout_id=? AND order_idx=?";
        try (Connection c = DriverManager.getConnection(URL)) {
            enableFK(c);
            c.setAutoCommit(false);

            int tempIdx;
            try (PreparedStatement ps = c.prepareStatement(getMaxSql)) {
                ps.setInt(1, workoutId);
                try (ResultSet rs = ps.executeQuery()) {
                    tempIdx = rs.next() ? rs.getInt(1) + 1 : 1; // valid (>0)
                }
            }

            try (PreparedStatement ps = c.prepareStatement(updSql)) {
                // A -> temp
                ps.setInt(1, tempIdx); ps.setInt(2, workoutId); ps.setInt(3, aIdx); ps.executeUpdate();
                // B -> A
                ps.setInt(1, aIdx);    ps.setInt(2, workoutId); ps.setInt(3, bIdx); ps.executeUpdate();
                // temp -> B
                ps.setInt(1, bIdx);    ps.setInt(2, workoutId); ps.setInt(3, tempIdx); ps.executeUpdate();
            }

            c.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("❌ swapOrder failed: " + e.getMessage());
            try { /* best-effort rollback */ } finally { }
            return false;
        }
    }


    // DELETE one row (by workout + order)
    public boolean deleteOne(int workoutId, int orderIdx) {
        final String sql = "DELETE FROM workout_exercise WHERE workout_id=? AND order_idx=?";
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            enableFK(c);
            ps.setInt(1, workoutId);
            ps.setInt(2, orderIdx);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("❌ deleteOne failed: " + e.getMessage());
            return false;
        }
    }

    // DELETE all rows for a workout
    public boolean deleteAllForWorkout(int workoutId) {
        final String sql = "DELETE FROM workout_exercise WHERE workout_id=?";
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            enableFK(c);
            ps.setInt(1, workoutId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("❌ deleteAllForWorkout failed: " + e.getMessage());
            return false;
        }
    }

    // CLONE: copy all rows from one workout to another (preserve order)
    public int cloneFromTo(int srcWorkoutId, int destWorkoutId) {
        final String sql = """
            INSERT INTO workout_exercise(workout_id, exercise_id, order_idx, target_sets, target_reps_or_secs)
            SELECT ?, exercise_id, order_idx, target_sets, target_reps_or_secs
              FROM workout_exercise
             WHERE workout_id=?
            ORDER BY order_idx
            """;
        try (Connection c = DriverManager.getConnection(URL);
             PreparedStatement ps = c.prepareStatement(sql)) {
            enableFK(c);
            ps.setInt(1, destWorkoutId);
            ps.setInt(2, srcWorkoutId);
            return ps.executeUpdate(); // rows inserted
        } catch (SQLException e) {
            System.out.println("❌ cloneFromTo failed: " + e.getMessage());
            return 0;
        }
    }
}
