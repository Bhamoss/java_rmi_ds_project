package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

public interface ManagerSession extends Remote {

    // NO EXTRA SERIALIZABLE NEEDED (carType already serializable)

    public void registerCRC(String name) throws RemoteException;
    public void unregisterCRC(String name) throws RemoteException;

    public int getNumberOfReservationsForCarType(String carRentalName, String carType)  throws RemoteException;
    public int getNumberOfReservationsByRenter(String clientName)  throws RemoteException;
    public Set<String> getBestClients()  throws RemoteException;
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year)  throws RemoteException;

    /*
    public Collection<String> getCRCs() throws RemoteException;
    public Collection<CarType> getCRCCarTypes(String crc_name) throws RemoteException;
    public int getNbReservations(CarType type) throws RemoteException;
    public String getBestCustomer() throws RemoteException;
    public int getNbReservationsRenter(String renter) throws RemoteException;
    public String getMostPopularCRC(Date date) throws RemoteException;
    */
}
