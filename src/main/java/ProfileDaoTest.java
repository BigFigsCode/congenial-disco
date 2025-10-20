

import java.util.List;

public class ProfileDaoTest {
    public static void main(String[] args) {
        ProfileDao dao = new ProfileDao();

        // Create some test profiles
        Profile p1 = new Profile("Brandon", "Intermediate", "Muscle Gain", 5);
        Profile p2 = new Profile("Tony", "Beginner", "Endurance", 3);

        // Insert into DB
        dao.insert(p1);
        dao.insert(p2);




        // Retrieve all profiles
        List<Profile> profiles = dao.getAllProfiles();

        System.out.println("\n📋 All Profiles:");
        for (Profile p : profiles) {
            System.out.println(p);
        }
    }
}
