package rental;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CarRentalAgency implements ICarRentalAgency {

    public CarRentalAgency() throws RemoteException {
        this.companies = new HashMap<String, ICarRentalCompany>();
        // The registry everyone will use is on the CRA server, so this can be hardcoded
        registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
    }

    private final Registry registry;

    public Registry getRegistry() {
        return this.registry;
    }

    
    // A company is itself responsible for binding itself to the registry
    // of the CRA
    private final Map<String, ICarRentalCompany> companies;

    private void addCompany(String name, ICarRentalCompany company) {
        this.companies.put(name, company);
    }

    private void removeCompany(String name) {
        this.companies.remove(name);
    }

    public void registerCRC(String name) throws Exception{
        try {
            ICarRentalCompany newCompany = (ICarRentalCompany) getRegistry().lookup(name);
            addCompany(name, newCompany);
        }
        catch (NotBoundException e){
            System.out.println(name + ": the crc with that name has not been made available on the local registry");
            throw e;
        }
        catch (Exception e ) {throw e;}
    }

    public void unregisterCRC(String name) {
        // crc is responsible for removing itself from the registry, TODO: we do it?
        removeCompany(name);
    }



    /*
    in order to conserve system resources, RMI is designed to support the reuse of sockets (i.e. ports).
If you are working in a simple scenario with one Java Virtual Machine, you can therefore safely use the
same port number (within your port range) for multiple exported objects when using the remote setup.
    */

    public String reserveReservationSession(String name) throws RemoteException{
        // the bean pushes itself onto the registry when created 
        ReservationSessionBean server_side_rs = new ReservationSessionBean(this, name);
        return server_side_rs.getRegistryName();
    }
    
    public String reserveManagerSession(String name) throws RemoteException {
        ManagerSessionBean server_side_ms = new ManagerSessionBean(this, name);
        return server_side_ms.getRegistryName();
    }
    
    /*******************************************
     * RESERVATION session functions.
     */

    public String checkForAvailableCarTypes(Date start, Date end) throws RemoteException {
        Set<CarType> availableTypes = new HashSet<CarType>();
        for (ICarRentalCompany crc : companies.values() ){
            //TODO: duplicates?
            availableTypes.addAll(crc.getAvailableCarTypes(start, end));
        }
        String ret = "";
        for (CarType type : availableTypes) {
            ret += type.toString() + '\n';
        }
        return ret;
    }
    

    // TODO
	public Quote createQuote(ReservationConstraints constraints, String client)
			throws ReservationException {
                return null;
    }

    //TODO
    // synchronised here solves the problem of different confirmquotes 
    public synchronized List<Reservation> confirmQuotes(String name) throws RemoteException {
        // TODO: checking if quots are still valid and not yet taken
        // TODO: when confirming quotes, synchronize crc so we cannot get a createQuote and confirmQuote together
        return null;
    }


    //TODO
    public String getCheapestCarType(Date start, Date end, String region) throws RemoteException {
        return null ;
    }

    /*******************************************
     * MANAGER session functions.
     */

    //TODO
    public int getNumberOfReservationsForCarType(String carRentalName, String carType) throws RemoteException {
        return 0;
    }


    //TODO
    public int getNumberOfReservationsByRenter(String clientName) throws RemoteException {
        return 0;
    }

    //TODO
    public Set<String> getBestClients() throws RemoteException {
        return null;
    }

    //TODO
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws RemoteException {
        return null;
    }
    
}