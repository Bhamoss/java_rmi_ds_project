package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface ReservationSession extends Remote{

    // NO EXTRA SERIALIZABLE NEEDED (reservation already serialized an rest is serializable by default)

    public List<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;
    public void addQuote(ReservationConstraints rc)  throws RemoteException;
    public List<Reservation> confirmQuotes(String name) throws RemoteException;
    public String getCheapestCarType(Date start, Date end, String region)  throws RemoteException;
}
