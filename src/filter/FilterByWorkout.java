package filter;

import domain.Session;

public class FilterByWorkout implements IFilter<Session> {
    private String workout;

    public FilterByWorkout(String workout) {
    this.workout = workout;}

    @Override
    public boolean accept(Session elem) {
        return elem.getWorkoutDescription().toLowerCase().contains(workout.toLowerCase());

    }
}
