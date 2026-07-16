package filter;

import domain.Client;

public class FilterByEmail implements IFilter<Client>{
    private String email;

    public FilterByEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean accept(Client elem) {
        return elem.getEmail().toLowerCase().contains(email.toLowerCase());
    }
}
