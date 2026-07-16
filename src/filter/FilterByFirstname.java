package filter;

import domain.Client;

public class FilterByFirstname implements IFilter<Client>{
    private String firstname;

    public FilterByFirstname(String firstname) {
        this.firstname = firstname;
    }

    @Override
    public boolean accept(Client elem) {
        return elem.getFirstname().toLowerCase().contains(firstname.toLowerCase());
    }
}
