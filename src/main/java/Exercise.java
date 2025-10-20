

public class Exercise {
    private int profileId;
    private int id;
    private String name;
    private String muscle;
    private String equipment;   // e.g., Barbell, Dumbbell, Bodyweight
    private String difficulty;  // e.g., Easy, Moderate, Hard
    private String type;        // e.g., REPS or SECS



    public Exercise(int profileId, String name, String muscle, String equipment, String difficulty, String type) {
        this.profileId = profileId;
        this.name = name;
        this.muscle = muscle;
        this.equipment = equipment;
        this.difficulty = difficulty;
        this.type = type;
    }


    public Exercise(int profileID, int id, String name, String muscle, String equipment, String difficulty, String type) {
        this(profileID, name, muscle, equipment, difficulty, type);
        this.id = id;
    }

    // getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getMuscle() { return muscle; }
    public String getEquipment() { return equipment; }
    public String getDifficulty() { return difficulty; }
    public String getType() { return type; }
    public int getProfileId() { return profileId; }


    // setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setMuscle(String muscle) { this.muscle = muscle; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setType(String type) { this.type = type; }
    public void setProfileId(int profileId) { this.profileId = profileId; }

    private String profileName; // display-only
    public void setProfileName(String n) { this.profileName = n; }
    @Override public String toString() {
        return "Exercise{" +
                "id=" + id +
                ", profile=" + (profileName != null ? ("'" + profileName + "'") : ("#" + profileId)) +
                ", name='" + name + '\'' +
                ", muscle='" + muscle + '\'' +
                ", equipment='" + equipment + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

}
