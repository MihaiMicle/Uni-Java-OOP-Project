package repository;

import domain.Identifiable;
import java.util.ArrayList;

public abstract class FileRepository<ID, T extends Identifiable<ID>> extends FilteredRepository<ID, T> {
    String filename;

    protected abstract void readFromFile();
    protected abstract void writeToFile();

    public FileRepository(String filename) {
        this.filename = filename;
        readFromFile();
    }

    @Override
    public void add(T elem) throws RepositoryException {
        super.add(elem);
        writeToFile();
    }

    @Override
    public void delete(ID id) throws RepositoryException {
        super.delete(id);
        writeToFile();
    }

    @Override
    public void update(T elem) throws RepositoryException {
        super.update(elem);
        writeToFile();
    }
}
