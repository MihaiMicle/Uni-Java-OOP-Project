package repository;

import domain.Identifiable;
import filter.IFilter;
import java.util.*;

public class MemoryRepository<ID, T extends Identifiable<ID>> implements IRepository<ID, T> {
    protected Map<ID, T> map = new HashMap<>();

    public void add(T item) throws RepositoryException{
        if(map.containsKey(item.getID()))
            throw new RepositoryException("The ID: " + item.getID() + " already exists!");
        map.put(item.getID(), item);
    }

    public void delete(ID id) throws RepositoryException {

        if(!map.containsKey(id))
            throw new RepositoryException("The ID: " + id + " does not exists!");
        map.remove(id);
    }

    public void update(T item) throws RepositoryException {
        if(!map.containsKey(item.getID()))
            throw new RepositoryException("The ID: " + item.getID() + " does not exists!");
        map.put(item.getID(), item);
    }

    public ArrayList<T> getAll() {

        return new ArrayList<>(map.values());
    }

    public T getByID(ID id) {
        return map.get(id);
    }

}
