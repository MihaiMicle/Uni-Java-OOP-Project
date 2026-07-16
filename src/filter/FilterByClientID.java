package filter;

import domain.Session;

public class FilterByClientID  implements IFilter<Session> {
    private Integer clientID;

    public FilterByClientID(Integer clientID) {
        this.clientID = clientID;
    }

    @Override
    public boolean accept(Session elem) {
        return elem.getClientID().equals(clientID);
    }
}
