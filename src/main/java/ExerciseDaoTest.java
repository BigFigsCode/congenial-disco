import java.util.List;
import java.util.Optional;

public class ExerciseDaoTest {
    public static void main(String[] args) {
        try { Class.forName("org.sqlite.JDBC"); } catch (Exception ignored) {}

        ProfileDao pdao = new ProfileDao();
        int brandonId = pdao.getOrCreateByName("Brandon", "Intermediate", "Hypertrophy", 5);

        ExerciseDao dao = new ExerciseDao();

        String tag = String.valueOf(System.currentTimeMillis() % 100000);
        Exercise squat  = new Exercise(brandonId, "Back Squat " + tag, "Legs", "Barbell", "Hard", "REPS");
        Exercise pushup = new Exercise(brandonId, "Push-up "  + tag, "Chest", "Bodyweight", "Moderate", "REPS");
        int id1 = dao.insert(squat);
        int id2 = dao.insert(pushup);
        System.out.println("Inserted ids: " + id1 + ", " + id2);

        List<Exercise> all = dao.getAll();
        System.out.println("\nAll exercises:");
        all.forEach(System.out::println);

        Optional<Exercise> loaded = dao.findById(id1);
        System.out.println("\nLoaded by id: " + loaded.orElse(null));

        pushup.setDifficulty("Easy");
        pushup.setId(id2);
        System.out.println("Updated? " + dao.update(pushup));

        System.out.println("\nBy muscle 'Chest':");
        dao.findByMuscle("Chest").forEach(System.out::println);

        System.out.println("\nDeleted squat? " + dao.delete(id1));
        System.out.println("Find deleted id: " + dao.findById(id1));
    }
}
