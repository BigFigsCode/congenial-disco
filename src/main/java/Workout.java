

public class Workout {
    private int id;
    private int profileId;
    private String name;
    private Integer dayOfWeek;                 // 1..7 or null (template)
    private int restBetweenSetsSec;            // default 90
    private int restBetweenExercisesSec;       // default 120
    private boolean active;                    // 1=true, 0=false
    private String createdAt;                  // optional: read-only display
    private String updatedAt;                  // optional: read-only display

    // Constructors
    public Workout(int profileId, String name, Integer dayOfWeek,
                   int restBetweenSetsSec, int restBetweenExercisesSec,
                   boolean active) {
        this.profileId = profileId;
        this.name = name;
        this.dayOfWeek = dayOfWeek;
        this.restBetweenSetsSec = restBetweenSetsSec;
        this.restBetweenExercisesSec = restBetweenExercisesSec;
        this.active = active;
    }

    public Workout(int id, int profileId, String name, Integer dayOfWeek,
                   int restBetweenSetsSec, int restBetweenExercisesSec,
                   boolean active, String createdAt, String updatedAt) {
        this(profileId, name, dayOfWeek, restBetweenSetsSec, restBetweenExercisesSec, active);
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters/setters
    public int getId() { return id; }
    public int getProfileId() { return profileId; }
    public String getName() { return name; }
    public Integer getDayOfWeek() { return dayOfWeek; }
    public int getRestBetweenSetsSec() { return restBetweenSetsSec; }
    public int getRestBetweenExercisesSec() { return restBetweenExercisesSec; }
    public boolean isActive() { return active; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    public void setId(int id) { this.id = id; }
    public void setProfileId(int profileId) { this.profileId = profileId; }
    public void setName(String name) { this.name = name; }
    public void setDayOfWeek(Integer dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setRestBetweenSetsSec(int v) { this.restBetweenSetsSec = v; }
    public void setRestBetweenExercisesSec(int v) { this.restBetweenExercisesSec = v; }
    public void setActive(boolean active) { this.active = active; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    @Override public String toString() {
        return "Workout{" +
                "id=" + id +
                ", profileId=" + profileId +
                ", name='" + name + '\'' +
                ", dayOfWeek=" + dayOfWeek +
                ", restBetweenSetsSec=" + restBetweenSetsSec +
                ", restBetweenExercisesSec=" + restBetweenExercisesSec +
                ", active=" + active +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                '}';
    }
}
