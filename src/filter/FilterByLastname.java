package filter;

import domain.Client;

public class FilterByLastname implements IFilter<Client> {
    private String lastname;

    public FilterByLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public boolean accept(Client elem) {
        return elem.getLastname().toLowerCase().contains(lastname.toLowerCase());

    }
}
