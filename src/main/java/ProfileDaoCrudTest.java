
import java.util.Optional;

public class ProfileDaoCrudTest {
    public static void main(String[] args) {
        ProfileDao dao = new ProfileDao();

        // CREATE
        Profile p = new Profile("Brandon", "Intermediate", "Hypertrophy", 5);
        int newId = dao.insert(p);
        System.out.println("Inserted id = " + newId);

        // READ (by id)
        Optional<Profile> fromDb = dao.findById(newId);
        System.out.println("Loaded: " + fromDb.orElse(null));

        // UPDATE
        p.setGoal("Strength & Size");
        p.setDaysPerWeek(6);
        boolean updated = dao.update(p);
        System.out.println("Updated? " + updated);

        // READ again
        System.out.println("After update: " + dao.findById(newId).orElse(null));

        // DELETE
        boolean deleted = dao.delete(newId);
        System.out.println("Deleted? " + deleted);

        // READ again (should be empty)
        System.out.println("After delete: " + dao.findById(newId));
    }
}

