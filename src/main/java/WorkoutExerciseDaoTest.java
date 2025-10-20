

import java.util.List;

public class WorkoutExerciseDaoTest {
    public static void main(String[] args) {
        try { Class.forName("org.sqlite.JDBC"); } catch (Exception ignored) {}

        ProfileDao pdao = new ProfileDao();
        int profileId = pdao.getOrCreateByName("Brandon", "Intermediate", "Hypertrophy", 5);

        WorkoutDao wdao = new WorkoutDao();
        // Create a workout to attach exercises to
        Workout w = new Workout(profileId, "Upper Body A", 1, 90, 120, true);
        int wid = wdao.insert(w);

        ExerciseDao edao = new ExerciseDao();
        // Ensure two exercises exist for this profile
        String tag = String.valueOf(System.currentTimeMillis()%100000);
        int ex1 = edao.insert(new Exercise(profileId, "Bench Press "+tag, "Chest", "Barbell", "Hard", "REPS"));
        int ex2 = edao.insert(new Exercise(profileId, "Seated Row "+tag, "Back", "Cable", "Moderate", "REPS"));

        WorkoutExerciseDao wedao = new WorkoutExerciseDao();
        wedao.insert(new WorkoutExercise(wid, ex1, 1, 4, 8));
        wedao.insert(new WorkoutExercise(wid, ex2, 2, 3, 12));

        System.out.println("Ordered rows:");
        List<WorkoutExercise> rows = wedao.getByWorkout(wid);
        rows.forEach(System.out::println);

        System.out.println("\nWith exercise info:");
        wedao.getByWorkoutWithExercise(wid).forEach(System.out::println);

        System.out.println("\nSwap order 1<->2");
        wedao.swapOrder(wid, 1, 2);
        wedao.getByWorkoutWithExercise(wid).forEach(System.out::println);

        System.out.println("\nUpdate targets for order 1");
        wedao.updateTargets(wid, 1, 5, 5);
        wedao.getByWorkoutWithExercise(wid).forEach(System.out::println);

        // Clean up
        wdao.delete(wid); // cascades delete to workout_exercise
    }
}
