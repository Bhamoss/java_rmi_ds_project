package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

public interface ManagerSession extends Remote {
    public void registerCRC() throws RemoteException;
    public void unregisterCRC() throws RemoteException;
    public Collection<String> getCRCs() throws RemoteException;
    public Collection<CarType> getCRCCarTypes(String crc_name) throws RemoteException;
    public int getNbReservations(CarType type) throws RemoteException;
    public String getBestCustomer() throws RemoteException;
    public int getNbReservationsRenter(String renter) throws RemoteException;
    public String getMostPopularCRC(Date date) throws RemoteException;
}
