package filter;

import domain.Client;

public class FilterByPhone implements IFilter<Client>{
    private String phone;

    public FilterByPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean accept(Client elem) {
        return elem.getPhone().toLowerCase().contains(phone.toLowerCase());
    }
}
