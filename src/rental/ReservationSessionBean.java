package rental;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationSessionBean extends Session implements ReservationSession {

    public ReservationSessionBean(CarRentalAgency cra, String name) throws RemoteException {
        super(cra, name);
        this.quotes = new ArrayList<Quote>();
    }

    @Override
    protected void pushToRegistry() throws RemoteException {
    /*
    in order to conserve system resources, RMI is designed to support the reuse of sockets (i.e. ports).
If you are working in a simple scenario with one Java Virtual Machine, you can therefore safely use the
same port number (within your port range) for multiple exported objects when using the remote setup.
    */
        ReservationSession server_side_stub = (ReservationSession) UnicastRemoteObject.exportObject(this, RentalServer.RESERVATION_SESSIONS_PORT);
        getCra().getRegistry().rebind(getRegistryName(), server_side_stub);
    }


    private final List<Quote> quotes;
    
    private void addQuote(Quote quote) {
        quotes.add(quote);
    }

    @Override
    public String checkForAvailableCarTypes(Date start, Date end) throws RemoteException {
        return getCra().checkForAvailableCarTypes(start, end);
    }


    @Override
    public void addQuote(ReservationConstraints rc) throws RemoteException, ReservationException {
        Quote newQuote = getCra().createQuote(rc, getName());
        addQuote(newQuote);
    }

    //TODO: CONCURRENCY
    @Override
    public List<Reservation> confirmQuotes(String name) throws RemoteException, ReservationException {
        // we still have a problem if something happens to the car rental company now, but we could make the confirmQuote a synchronised method
        // we don't need the name parameter?
        return getCra().confirmQuotes(quotes);
    }

    @Override
    public String getCheapestCarType(Date start, Date end, String region) throws RemoteException {
        return getCra().getCheapestCarType(start, end, region);
    }

}