package rental;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;

public class ManagerSessionBean extends Session implements ManagerSession {

    public ManagerSessionBean(CarRentalAgency cra, String name) throws RemoteException {
        super(cra, name);
    }

    @Override
    protected void pushToRegistry() throws RemoteException {
    /*
    in order to conserve system resources, RMI is designed to support the reuse of sockets (i.e. ports).
If you are working in a simple scenario with one Java Virtual Machine, you can therefore safely use the
same port number (within your port range) for multiple exported objects when using the remote setup.
    */
        ManagerSession server_side_stub = (ManagerSession) UnicastRemoteObject.exportObject(this,
                RentalServer.MANAGER_SESSIONS_PORT);
        getCra().getRegistry().rebind(getRegistryName(), server_side_stub);
    }

    @Override
    public void registerCRC(String name) throws Exception {
        getCra().registerCRC(name);
    }

    @Override
    public void unregisterCRC(String name) throws RemoteException {
        getCra().unregisterCRC(name);
    }




    @Override
    public int getNumberOfReservationsForCarType(String carRentalName, String carType)
            throws RemoteException, Exception {
        return getCra().getNumberOfReservationsForCarType(carRentalName, carType);
    }

    @Override
    public int getNumberOfReservationsByRenter(String clientName) throws RemoteException {
        return getCra().getNumberOfReservationsByRenter(clientName);
    }

    @Override
    public Set<String> getBestClients() throws RemoteException {
        return getCra().getBestClients();
    }

    @Override
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws Exception {
        return getCra().getMostPopularCarTypeIn(carRentalCompanyName, year);
    }

}