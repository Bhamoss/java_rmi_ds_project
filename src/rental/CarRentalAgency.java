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
        registry = LocateRegistry.getRegistry("127.0.0.1", RentalServer.RMI_PORT);
    }

    private final Registry registry;

    public Registry getRegistry() {
        return this.registry;
    }

    
    // A company is itself responsible for binding itself to the registry
    // of the CRA
    // keys = name of registry
    // values = crc's
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

    /**
     * returns the carrentalcompany which has the given name 
     *      if there are multiple crc's, the first one is returned
     * @param crcName
     * @return
     */
    private ICarRentalCompany getCarRentalCompany(String crcName) {
        for (ICarRentalCompany crc : companies.values()) {
            if (crc.getName().equals(crcName)) {
                return crc;
            }
        }
        return null;
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


    
    /**
     * Find a cheapest car type that is available in the given period and region.
     *
     * @param start start time of the period
     * @param end end time of the period
     * @param region region of interest (if null, no limitation by region)
     *
     * @return name of a cheapest car type for the given period
     *
     * @throws Exception if things go wrong, throw exception
     */
    public String getCheapestCarType(Date start, Date end, String region) throws RemoteException {
        
        double cheapestPrice = Double.MAX_VALUE;
        CarType cheapestCarType, tmpCarType;

        for (ICarRentalCompany crc : companies.values()) {
            if (region == null || crc.operatesInRegion(region)) {
                tmpCarType = crc.getCheapestCarType(start, end);
                if (cheapestPrice > tmpCarType.getRentalPricePerDay()) {
                    cheapestPrice = tmpCarType.getRentalPricePerDay();
                    cheapestCarType = tmpCarType;
                }
            }
        }


        return cheapestCarType;
    }

    /*******************************************
     * MANAGER session functions.
     */

    /**
     * Get the number of reservations for a particular car type.
     *
     * @param carRentalName name of the rental company managed by this session
     * @param carType name of the car type
     * @return number of reservations for this car type
     *
     * @throws Exception if things go wrong, throw exception
     */    
    public int getNumberOfReservationsForCarType(String carRentalName, String carType) throws RemoteException, Exception {
        
        ICarRentalCompany crc = getCarRentalCompany(carRentalName);
        if (crc == null)
            throw new Exception("Car rental name is not registered in this carrentalagency.");
        return crc.getNumberOfReservationsForCarType(carType);
    }


    /**
     * Get the number of reservations made by the given renter (across whole
     * rental agency).
     *
     * @param clientName name of the renter
     * @return	the number of reservations of the given client (across whole
     * rental agency)
     *
     * @throws Exception if things go wrong, throw exception
     */
    public int getNumberOfReservationsByRenter(String clientName) throws RemoteException {
        int nbReservations = 0;
        for (ICarRentalCompany crc : companies.values()) {
            nbReservations += crc.getReservationsByRenter(clientName);
        }
        return nbReservations;
    }

    /**
     * Get the (list of) best clients, i.e. clients that have highest number of
     * reservations (across all rental agencies).
     *
     * @return set of best clients
     * @throws Exception if things go wrong, throw exception
     */
    public Set<String> getBestClients() throws RemoteException {

        Set<String> bestClients = new Set<String>();
        int highestNbReservations = 0;
        
        int tmpNbReservations;
        for (String clientName : getAllClientNames()) {
            tmpNbReservations = getNumberOfReservationsByRenter(clientName);
            if (tmpNbReservations > highestNbReservations) {
                // delete alle clients uit bestClients en voeg deze eraan toe, update highestNbReservations
                highestNbReservations = tmpNbReservations;
                bestClients.clear();
            }
            if (tmpNbReservations == highestNbReservations) {
                bestClients.add(clientName);
            }
        }
        
        return bestClients;
    }

    /**
     * returns a list of all the clients
     * @return
     * @throws RemoteException
     */
    private Set<String> getAllClientNames() throws RemoteException {
        Set<String> allClients = new Set<String>();

        for (ICarRentalCompany crc : companies.values()) {
            allClients.addAll(crc.getAllClientNames());
        } 
        return allClients;
    }

    /**
     * Get the most popular car type in the given car rental company.
     *
     * @param	carRentalCompanyName The name of the car rental company.
     * @param year year in question
     * @return the most popular car type in the given car rental company
     *
     * @throws Exception carRentalCompanyName not found
     */
    public CarType getMostPopularCarTypeIn(String carRentalCompanyName, int year) throws Exception, RemoteException {
        ICarRentalCompany crc = getCarRentalCompany(carRentalCompanyName);
        if (crc == null)
            throw new Exception("Car rental company is not registered.");
            
        return crc.getMostPopularCarTypeIn(year);
    }
    
}