package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ICarRentalAgency extends Remote {

    public ReservationSession getNewReservationSession(String name) throws RemoteException;

    public ManagerSession getNewManagerSession(String name, String carRentalName) throws RemoteException;
}