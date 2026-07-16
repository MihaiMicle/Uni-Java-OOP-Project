package validator;

import domain.Client;
import java.util.regex.Pattern;

public class ClientValidator implements IValidator<Client> {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
    // The patterns were searched on the Internet

    @Override
    public void validate(Client client) throws ValidationException {
        StringBuilder errors = new StringBuilder();

        if (client.getID() <= 0) {
            errors.append("Client ID must be a positive integer!\n");
        }
        if (client.getFirstname() == null || client.getFirstname().trim().isEmpty()) {
            errors.append("First name cannot be empty!\n");
        }
        if (client.getLastname() == null || client.getLastname().trim().isEmpty()) {
            errors.append("Last name cannot be empty!\n");
        }

        if (client.getEmail() == null || !EMAIL_PATTERN.matcher(client.getEmail()).matches()) {
            errors.append("Email is not valid. Example: user@domain.com\n");
        }

        if (client.getPhone() == null || !PHONE_PATTERN.matcher(client.getPhone()).matches()) {
            errors.append("Phone number must be exactly 10 digits! Example: 0712345678\n");
        }

        if (errors.length() > 0) {
            throw new ValidationException(errors.toString());
        }
    }
}