
public class WorkoutWithProfile {
    private int id;
    private int profileId;
    private String profileName;   // <- for display
    private String name;
    private Integer dayOfWeek;
    private int restBetweenSetsSec;
    private int restBetweenExercisesSec;
    private boolean active;
    private String createdAt;
    private String updatedAt;

    public WorkoutWithProfile(int id, int profileId, String profileName, String name,
                              Integer dayOfWeek, int restSets, int restEx,
                              boolean active, String createdAt, String updatedAt) {
        this.id = id; this.profileId = profileId; this.profileName = profileName;
        this.name = name; this.dayOfWeek = dayOfWeek;
        this.restBetweenSetsSec = restSets; this.restBetweenExercisesSec = restEx;
        this.active = active; this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    // ---------- Getters ----------
    public int getId() { return id; }
    public int getProfileId() { return profileId; }
    public String getProfileName() { return profileName; }
    public String getName() { return name; }
    public Integer getDayOfWeek() { return dayOfWeek; }
    public int getRestBetweenSetsSec() { return restBetweenSetsSec; }
    public int getRestBetweenExercisesSec() { return restBetweenExercisesSec; }
    public boolean isActive() { return active; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    // ---------- Setters ----------
    public void setId(int id) { this.id = id; }
    public void setProfileId(int profileId) { this.profileId = profileId; }
    public void setProfileName(String profileName) { this.profileName = profileName; }
    public void setName(String name) { this.name = name; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setRestBetweenSetsSec(int restBetweenSetsSec) { this.restBetweenSetsSec = restBetweenSetsSec; }
    public void setRestBetweenExercisesSec(int restBetweenExercisesSec) { this.restBetweenExercisesSec = restBetweenExercisesSec; }
    public void setActive(boolean active) { this.active = active; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }


    @Override public String toString() {
        return "WorkoutWithProfile{" +
                "id=" + id +
                ", profile='" + profileName + "' (id=" + profileId + ")" +
                ", name='" + name + '\'' +
                ", dayOfWeek=" + dayOfWeek +
                ", restBetweenSetsSec=" + restBetweenSetsSec +
                ", restBetweenExercisesSec=" + restBetweenExercisesSec +
                ", active=" + active +
                '}';
    }

    // getters...
}
