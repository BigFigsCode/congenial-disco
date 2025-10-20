

public class WorkoutExercise {
    private int workoutId;
    private int exerciseId;
    private int orderIdx;             // position in the workout (1..n)
    private int targetSets;
    private int targetRepsOrSecs;     // reps or seconds depending on exercise.type

    public WorkoutExercise(int workoutId, int exerciseId, int orderIdx, int targetSets, int targetRepsOrSecs) {
        this.workoutId = workoutId;
        this.exerciseId = exerciseId;
        this.orderIdx = orderIdx;
        this.targetSets = targetSets;
        this.targetRepsOrSecs = targetRepsOrSecs;
    }

    public int getWorkoutId() { return workoutId; }
    public int getExerciseId() { return exerciseId; }
    public int getOrderIdx() { return orderIdx; }
    public int getTargetSets() { return targetSets; }
    public int getTargetRepsOrSecs() { return targetRepsOrSecs; }

    public void setWorkoutId(int workoutId) { this.workoutId = workoutId; }
    public void setExerciseId(int exerciseId) { this.exerciseId = exerciseId; }
    public void setOrderIdx(int orderIdx) { this.orderIdx = orderIdx; }
    public void setTargetSets(int targetSets) { this.targetSets = targetSets; }
    public void setTargetRepsOrSecs(int targetRepsOrSecs) { this.targetRepsOrSecs = targetRepsOrSecs; }

    @Override public String toString() {
        return "WorkoutExercise{" +
                "workoutId=" + workoutId +
                ", exerciseId=" + exerciseId +
                ", orderIdx=" + orderIdx +
                ", targetSets=" + targetSets +
                ", targetRepsOrSecs=" + targetRepsOrSecs +
                '}';
    }
}
