package repository;

public class RepositoryException extends Exception {
    String massage;

    public RepositoryException(String massage) {
        this.massage = massage;
    }

    @Override
    public String getMessage() {
        return massage;
    }
}
