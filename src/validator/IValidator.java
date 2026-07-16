package validator;

import domain.Identifiable;

public interface IValidator<T extends Identifiable> {
    void validate(T entity) throws ValidationException;
}