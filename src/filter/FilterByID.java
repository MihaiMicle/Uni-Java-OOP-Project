package filter;

import domain.Identifiable;

public class FilterByID<T extends Identifiable<Integer>> implements IFilter<T> {
    private Integer id;

    public FilterByID(Integer id) {
        this.id = id;
    }

    @Override
    public boolean accept(T item) {
        return item.getID().equals(id);
    }
}
