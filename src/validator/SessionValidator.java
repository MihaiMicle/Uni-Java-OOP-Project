package validator;

import domain.Session;
import java.util.regex.Pattern;

public class SessionValidator implements IValidator<Session> {

    private static final Pattern DATE_PATTERN = Pattern.compile(
            "^(0[1-9]|[12][0-9]|3[01])\\.(0[1-9]|1[012])\\.(20\\d\\d)$");
    private static final Pattern TIME_PATTERN = Pattern.compile(
            "^([01][0-9]|2[0-3]):([0-5][0-9])$");
    // The patterns were searched on the Internet

    @Override
    public void validate(Session session) throws ValidationException {
        StringBuilder errors = new StringBuilder();

        if (session.getID() <= 0) {
            errors.append("Session ID must be a positive integer.\n");
        }
        if (session.getClientID() <= 0) {
            errors.append("Client ID must be a positive integer.\n");
        }
        if (session.getDate() == null || !DATE_PATTERN.matcher(session.getDate()).matches()) {
            errors.append("Date format must be dd.mm.yyyy (e.g., 25.10.2025)\n");
        }
        if (session.getTime() == null || !TIME_PATTERN.matcher(session.getTime()).matches()) {
            errors.append("Time format must be hh:mm (e.g., 14:30)\n");
        }
        if (session.getWorkoutDescription() == null || session.getWorkoutDescription().trim().isEmpty()) {
            errors.append("Workout description cannot be empty.\n");
        }

        if (errors.length() > 0) {
            throw new ValidationException(errors.toString());
        }
    }
}