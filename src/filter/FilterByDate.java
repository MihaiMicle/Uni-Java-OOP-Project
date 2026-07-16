package filter;

import domain.Session;

public class FilterByDate implements IFilter<Session> {
    private String date;

    public FilterByDate(String date) {
        this.date = date;
    }

    @Override
    public boolean accept(Session elem) {

        return elem.getDate().toLowerCase().contains(date.toLowerCase());
    }
}
