package rental;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CarRentalAgency implements ICarRentalAgency {

    public CarRentalAgency() throws RemoteException {
        this.companies = new HashMap<String, ICarRentalCompany>();
        // The registry everyone will use is on the CRA server, so this can be hardcoded
        registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
    }

    private final Registry registry;

    private Registry getRegistry() {
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
        ICarRentalCompany newCompany = (ICarRentalCompany) getRegistry().lookup(name);
        addCompany(name, newCompany);
    }

    public void unregisterCRC(String name) {
        removeCompany(name);
    }



    /*
    in order to conserve system resources, RMI is designed to support the reuse of sockets (i.e. ports).
If you are working in a simple scenario with one Java Virtual Machine, you can therefore safely use the
same port number (within your port range) for multiple exported objects when using the remote setup.
    */

    public String reserveReservationSession(String name) throws RemoteException{
        
        ReservationSessionBean server_side_rs = new ReservationSessionBean(this, name);
        ReservationSession server_side_stub = (ReservationSession) UnicastRemoteObject.exportObject(server_side_rs, RentalServer.RESERVATION_SESSIONS_PORT);

        while ( Arrays.asList( registry.list()).contains(name)) {
            name += 'x';
        }
			
			// we make our stub available in the registry
		registry.rebind(name, server_side_stub);
        return name;
    }
    
    public String reserveManagerSession(String name) throws RemoteException {
        ManagerSessionBean server_side_ms = new ManagerSessionBean(this, name);
        ManagerSession server_side_stub = (ManagerSession) UnicastRemoteObject.exportObject(server_side_ms, RentalServer.MANAGER_SESSIONS_PORT);

        while ( Arrays.asList( registry.list()).contains(name)) {
            name += 'x';
        }
			
			// we make our stub available in the registry
		registry.rebind(name, server_side_stub);
        return name;
	}
    

}