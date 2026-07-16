package repository;

import domain.Identifiable;
import filter.IFilter;
import java.util.ArrayList;

public class FilteredRepository<ID, T extends Identifiable<ID>> extends MemoryRepository<ID,T> {
    public ArrayList<T> filter(IFilter<T> filter) {
        ArrayList<T> filtered = new ArrayList<>();
        for(T elem : this.getAll()) {
            if(filter.accept(elem)) {
                filtered.add(elem);
            }
        }
        return filtered;
    }
}
