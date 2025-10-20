

public class Session {
    private int id;
    private int profileId;
    private Integer workoutId;     // nullable (free-style)
    private String dateIso;        // ISO-8601 like "2025-10-19T21:30:00"
    private int totalMinutes;
    private Integer rpe;           // 1..10 or null
    private String notes;          // nullable

    // Constructor for INSERT (no id yet)
    public Session(int profileId, Integer workoutId, String dateIso,
                   int totalMinutes, Integer rpe, String notes) {
        this.profileId = profileId;
        this.workoutId = workoutId;
        this.dateIso = dateIso;
        this.totalMinutes = totalMinutes;
        this.rpe = rpe;
        this.notes = notes;
    }

    // Full constructor (READ)
    public Session(int id, int profileId, Integer workoutId, String dateIso,
                   int totalMinutes, Integer rpe, String notes) {
        this(profileId, workoutId, dateIso, totalMinutes, rpe, notes);
        this.id = id;
    }

    // Getters
    public int getId() { return id; }
    public int getProfileId() { return profileId; }
    public Integer getWorkoutId() { return workoutId; }
    public String getDateIso() { return dateIso; }
    public int getTotalMinutes() { return totalMinutes; }
    public Integer getRpe() { return rpe; }
    public String getNotes() { return notes; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setProfileId(int profileId) { this.profileId = profileId; }
    public void setWorkoutId(Integer workoutId) { this.workoutId = workoutId; }
    public void setDateIso(String dateIso) { this.dateIso = dateIso; }
    public void setTotalMinutes(int totalMinutes) { this.totalMinutes = totalMinutes; }
    public void setRpe(Integer rpe) { this.rpe = rpe; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override public String toString() {
        return "Session{" +
                "id=" + id +
                ", profileId=" + profileId +
                ", workoutId=" + workoutId +
                ", dateIso='" + dateIso + '\'' +
                ", totalMinutes=" + totalMinutes +
                ", rpe=" + rpe +
                ", notes='" + notes + '\'' +
                '}';
    }
}
