package filter;

import domain.Session;

public class FilterByTime implements IFilter<Session> {
    private String time;

    public FilterByTime(String time){
        this.time=time;
    }

    @Override
    public boolean accept(Session elem) {
        return elem.getTime().toLowerCase().contains(time.toLowerCase());
    }
}
