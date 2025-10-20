

public class WorkoutExerciseView {
    private int workoutId;
    private int exerciseId;
    private int orderIdx;
    private int targetSets;
    private int targetRepsOrSecs;

    private String exerciseName;
    private String muscle;
    private String equipment;
    private String difficulty;
    private String type;

    public WorkoutExerciseView(int workoutId, int exerciseId, int orderIdx,
                               int targetSets, int targetRepsOrSecs,
                               String exerciseName, String muscle, String equipment,
                               String difficulty, String type) {
        this.workoutId = workoutId;
        this.exerciseId = exerciseId;
        this.orderIdx = orderIdx;
        this.targetSets = targetSets;
        this.targetRepsOrSecs = targetRepsOrSecs;
        this.exerciseName = exerciseName;
        this.muscle = muscle;
        this.equipment = equipment;
        this.difficulty = difficulty;
        this.type = type;
    }

    public int getWorkoutId() { return workoutId; }
    public int getExerciseId() { return exerciseId; }
    public int getOrderIdx() { return orderIdx; }
    public int getTargetSets() { return targetSets; }
    public int getTargetRepsOrSecs() { return targetRepsOrSecs; }
    public String getExerciseName() { return exerciseName; }
    public String getMuscle() { return muscle; }
    public String getEquipment() { return equipment; }
    public String getDifficulty() { return difficulty; }
    public String getType() { return type; }

    public void setWorkoutId(int workoutId) { this.workoutId = workoutId; }
    public void setExerciseId(int exerciseId) { this.exerciseId = exerciseId; }
    public void setOrderIdx(int orderIdx) { this.orderIdx = orderIdx; }
    public void setTargetSets(int targetSets) { this.targetSets = targetSets; }
    public void setTargetRepsOrSecs(int targetRepsOrSecs) { this.targetRepsOrSecs = targetRepsOrSecs; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    public void setMuscle(String muscle) { this.muscle = muscle; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setType(String type) { this.type = type; }

    @Override public String toString() {
        return "WorkoutExerciseView{" +
                "orderIdx=" + orderIdx +
                ", exercise='" + exerciseName + '\'' +
                ", targetSets=" + targetSets +
                ", targetRepsOrSecs=" + targetRepsOrSecs +
                ", muscle='" + muscle + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
