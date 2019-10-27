package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface ReservationSession extends Remote{

    // NO EXTRA SERIALIZABLE NEEDED (reservation and its exception already serialized an rest is serializable by default)

	public String checkForAvailableCarTypes(Date start, Date end) throws RemoteException;
    public void addQuote(ReservationConstraints rc)  throws RemoteException, ReservationException;
    public List<Reservation> confirmQuotes(String name) throws RemoteException, ReservationException;
    public String getCheapestCarType(Date start, Date end, String region)  throws RemoteException;

    public void closeSession() throws Exception;
}
