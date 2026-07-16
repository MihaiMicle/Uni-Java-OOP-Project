package repository;

import domain.Identifiable;
import filter.IFilter;
import java.util.ArrayList;

public interface IRepository<ID, T extends Identifiable<ID>> {
    void add(T item) throws RepositoryException;
    void delete(ID id) throws RepositoryException;
    void update(T item) throws RepositoryException;
    ArrayList<T> getAll();
    T getByID(ID id);
}
