import java.util.List;
import java.util.Optional;

public class WorkoutDaoTest {
    public static void main(String[] args) {
        try { Class.forName("org.sqlite.JDBC"); } catch (Exception ignored) {}

        // 1) Ensure a profile exists (reuse your ProfileDao or the getOrCreate helper)
        ProfileDao pdao = new ProfileDao();
        int profileId = pdao.getOrCreateByName("Brandon", "Intermediate", "Hypertrophy", 5);

        // 2) Create DAO
        WorkoutDao dao = new WorkoutDao();

        // 3) INSERT
        Workout w = new Workout(profileId, "Upper Body A", 1, 90, 120, true);
        int wid = dao.insert(w);
        System.out.println("Inserted workout id: " + wid);

        // 4) READ by id
        Optional<Workout> loaded = dao.findById(wid);
        System.out.println("Loaded: " + loaded.orElse(null));

        // 5) READ by profile
        List<Workout> mine = dao.getByProfile(profileId);
        System.out.println("\nMy workouts:");
        mine.forEach(System.out::println);

        // 6) UPDATE
        w.setRestBetweenSetsSec(75);
        w.setName("Upper Body A (edit)");
        boolean updated = dao.update(w);
        System.out.println("\nUpdated? " + updated);
        System.out.println("After update: " + dao.findById(wid).orElse(null));

        // 7) FILTER by day
        System.out.println("\nMonday workouts:");
        dao.getByProfileAndDay(profileId, 1).forEach(System.out::println);

        // 8) DELETE
        boolean deleted = dao.delete(wid);
        System.out.println("\nDeleted? " + deleted);
        System.out.println("Find deleted: " + dao.findById(wid));
    }
}
