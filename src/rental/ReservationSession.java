package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

public interface ReservationSession extends Remote{
    public Quote createQuote() throws RemoteException;
    public Collection<Quote> getCurrentQuotes() throws RemoteException;
    public void confirmQuotes() throws RemoteException;
    public Collection<CarType> getAvailableCarTypes() throws RemoteException;
    public CarType getCheapestCarType() throws RemoteException;
}
