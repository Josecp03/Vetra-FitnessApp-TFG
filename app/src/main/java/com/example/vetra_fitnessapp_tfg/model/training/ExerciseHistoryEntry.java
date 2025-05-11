package com.example.vetra_fitnessapp_tfg.model.training;

import com.google.firebase.firestore.PropertyName;
import java.io.Serializable;
import java.util.List;

public class ExerciseHistoryEntry implements Serializable {
    @PropertyName("user_id")
    private String userId;
    @PropertyName("exercise_id")
    private String exerciseId;
    @PropertyName("exercise_name")
    private String exerciseName;
    @PropertyName("exercise_photo_url")
    private String exercisePhotoUrl;
    @PropertyName("date")
    private String date; // YYYY-MM-DD
    @PropertyName("sets")
    private List<SetRecord> sets;

    public ExerciseHistoryEntry() { }

    public ExerciseHistoryEntry(
            String userId,
            String exerciseId,
            String exerciseName,
            String exercisePhotoUrl,
            String date,
            List<SetRecord> sets
    ) {
        this.userId           = userId;
        this.exerciseId       = exerciseId;
        this.exerciseName     = exerciseName;
        this.exercisePhotoUrl = exercisePhotoUrl;
        this.date             = date;
        this.sets             = sets;
    }

    @PropertyName("user_id")
    public String getUserId() { return userId; }
    @PropertyName("user_id")
    public void setUserId(String userId) { this.userId = userId; }

    @PropertyName("exercise_id")
    public String getExerciseId() { return exerciseId; }
    @PropertyName("exercise_id")
    public void setExerciseId(String exerciseId) { this.exerciseId = exerciseId; }

    @PropertyName("exercise_name")
    public String getExerciseName() { return exerciseName; }
    @PropertyName("exercise_name")
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    @PropertyName("exercise_photo_url")
    public String getExercisePhotoUrl() { return exercisePhotoUrl; }
    @PropertyName("exercise_photo_url")
    public void setExercisePhotoUrl(String exercisePhotoUrl) { this.exercisePhotoUrl = exercisePhotoUrl; }

    @PropertyName("date")
    public String getDate() { return date; }
    @PropertyName("date")
    public void setDate(String date) { this.date = date; }

    @PropertyName("sets")
    public List<SetRecord> getSets() { return sets; }
    @PropertyName("sets")
    public void setSets(List<SetRecord> sets) { this.sets = sets; }

    public static class SetRecord implements Serializable {
        @PropertyName("set_number")
        private int setNumber;
        @PropertyName("weight")
        private int weight;
        @PropertyName("reps")
        private int reps;

        public SetRecord() { }
        public SetRecord(int setNumber, int weight, int reps) {
            this.setNumber = setNumber;
            this.weight    = weight;
            this.reps      = reps;
        }

        @PropertyName("set_number")
        public int getSetNumber() { return setNumber; }
        @PropertyName("set_number")
        public void setSetNumber(int setNumber) { this.setNumber = setNumber; }

        @PropertyName("weight")
        public int getWeight() { return weight; }
        @PropertyName("weight")
        public void setWeight(int weight) { this.weight = weight; }

        @PropertyName("reps")
        public int getReps() { return reps; }
        @PropertyName("reps")
        public void setReps(int reps) { this.reps = reps; }
    }
}
