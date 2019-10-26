package rental;

import java.util.ArrayList;
import java.util.Collection;

public class ReservationSessionBean extends Session implements ReservationSession {

    public ReservationSessionBean(CarRentalAgency cra) {
        super(cra);
        this.quotes = new ArrayList<Quote>();
    }

    private final Collection<Quote> quotes;

}