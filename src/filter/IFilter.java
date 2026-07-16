package filter;

import domain.Identifiable;

public interface IFilter<T extends Identifiable> {
    public boolean accept(T elem);
}
