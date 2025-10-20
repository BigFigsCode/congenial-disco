

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class SessionDaoTest {
    public static void main(String[] args) {
        try { Class.forName("org.sqlite.JDBC"); } catch (Exception ignored) {}

        // Ensure a profile + workout exist
        ProfileDao pdao = new ProfileDao();
        int brandonId = pdao.getOrCreateByName("Brandon", "Intermediate", "Hypertrophy", 5);

        WorkoutDao wdao = new WorkoutDao();
        Workout w = new Workout(brandonId, "Upper Body A", 1, 90, 120, true);
        int workoutId = wdao.insert(w);

        // 1) INSERT a session
        SessionDao sdao = new SessionDao();
        String nowIso = LocalDateTime.now().toString().substring(0,19); // e.g. 2025-10-19T22:05:30
        Session s = new Session(brandonId, workoutId, nowIso, 45, 7, "Felt strong.");
        int sid = sdao.insert(s);
        System.out.println("Inserted session id: " + sid);

        // 2) READ by id
        Optional<Session> loaded = sdao.findById(sid);
        System.out.println("Loaded: " + loaded.orElse(null));

        // 3) LIST by profile
        List<Session> mine = sdao.getByProfile(brandonId);
        System.out.println("\nMy sessions:");
        mine.forEach(System.out::println);

        // 4) UPDATE
        s.setTotalMinutes(50);
        s.setRpe(8);
        s.setNotes("Cranked it up.");
        boolean updated = sdao.update(s);
        System.out.println("\nUpdated? " + updated);
        System.out.println("After update: " + sdao.findById(sid).orElse(null));

        // 5) RANGE query (today to tomorrow)
        String from = nowIso.substring(0,10) + "T00:00:00";
        String to   = nowIso.substring(0,10) + "T23:59:59";
        System.out.println("\nToday:");
        sdao.getByProfileAndRange(brandonId, from, to).forEach(System.out::println);

        // 6) JOIN view (optional)
        // sdao.listWithNamesByProfile(brandonId).forEach(System.out::println);

        // 7) DELETE
        boolean deleted = sdao.delete(sid);
        System.out.println("\nDeleted? " + deleted);

        // cleanup workout (cascades session? no, FK is on session->workout SET NULL; so safe to delete)
        wdao.delete(workoutId);
    }
}
