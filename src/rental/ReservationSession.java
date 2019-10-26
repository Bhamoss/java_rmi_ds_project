package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface ReservationSession extends Remote{
    public void checkForAvailableCarTypes(ReservationSession session, Date start, Date end) throws RemoteException;
    public void addQuoteToSession(ReservationSession session, String name, Date start, Date end, String carType, String region);
    public List<Reservation> confirmQuotes(ReservationSession session, String name);
}
