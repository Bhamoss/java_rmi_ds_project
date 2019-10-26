package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICarRentalAgency extends Remote {

    // STRINGS SERIALIZABLE

    public String reserveReservationSession(String name) throws RemoteException;

    public String reserveManagerSession(String name) throws RemoteException;

}