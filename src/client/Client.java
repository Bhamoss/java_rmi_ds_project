package client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;
import rental.ReservationSession;
import rental.CarType;
import rental.ICarRentalAgency;
import rental.ICarRentalCompany;
import rental.ManagerSession;

public class Client extends AbstractTestManagement<ReservationSession, ManagerSession>  {

	/********
	 * MAIN *
	 ********/

	private final static int LOCAL = 0;
	private final static int REMOTE = 1;
	public final static int RMI_PORT = 10926; // 10926-10930

	/**
	 * The `main` method is used to launch the client application and run the test
	 * script.
	 */
	public static void main(String[] args) throws Exception {
		// The first argument passed to the `main` method (if present)
		// indicates whether the application is run on the remote setup or not.
		int localOrRemote = (args.length == 1 && args[0].equals("REMOTE")) ? REMOTE : LOCAL;

		String agencyName = "agency";

		System.setSecurityManager(null);
		//TODO: change to carRentalAgency
		// the stub to filled with the remote object
		ICarRentalAgency client_side_cra_stub = null;
		Registry registry;

		// split into local and remote cases

		if (localOrRemote == LOCAL) {
			registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
			client_side_cra_stub = (ICarRentalAgency) registry.lookup(agencyName);
		} else {
			registry = LocateRegistry.getRegistry("192.168.104.76", Client.RMI_PORT);
			client_side_cra_stub = (ICarRentalAgency) registry.lookup(agencyName);
		}

		// An example reservation scenario on car rental company 'Hertz' would be...
		Client client = new Client("simpleTrips", localOrRemote, client_side_cra_stub, registry);

		// starting here we can use the stub, when we have implemented everything ok
		// I suggest adding a crc field to the client holding a stub so we can use the
		// stub in the mehtods

		// this runs the tests of the supervisors
		client.run();
	}

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public Client(String scriptFile, int localOrRemote, ICarRentalAgency icra, Registry registry) {
		super(scriptFile);
		setCra_stub(icra);
		this.registry = registry;
	}

	// The car rental company interface stub
	private ICarRentalAgency cra_stub;

	private ICarRentalAgency getCra_stub() {
		return cra_stub;
	}

	private void setCra_stub(ICarRentalAgency cra_stub) {
		this.cra_stub = cra_stub;
	}

	private final Registry registry;

	/**
	 * @return the registry
	 */
	private Registry getRegistry() {
		return this.registry;
	}

	/***************************************************
	 * Session methods
	 ****************************************************/

	/**
	 * Creates a reservation session on the server, which posts itself onto the registry on the server, and returns the identifier.
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	private String reserveReservationSession(String name) throws Exception{
		return getCra_stub().reserveReservationSession(name);
	}


	@Override
	protected ReservationSession getNewReservationSession(String name) throws Exception {
		String stub = reserveReservationSession(name);
		return (ReservationSession) getRegistry().lookup(stub);
	}

	private String reserveManagerSession(String name) throws Exception{
		return getCra_stub().reserveManagerSession(name);
	}

	@Override
	protected ManagerSession getNewManagerSession(String name, String carRentalName) throws Exception {
		String stub = reserveManagerSession(name);
		return (ManagerSession) getRegistry().lookup(stub);
	}


	/********************************************************
	 * Reservation session methods
	 ********************************************************/


	@Override
	protected void checkForAvailableCarTypes(ReservationSession session, Date start, Date end) throws Exception {
		System.out.print("\nAvailabel car types for period ");
		System.out.print(start);
		System.out.print(" - ");
		System.out.print(end);
		System.out.print("\n");
		System.out.println(session.checkForAvailableCarTypes(start, end)); //stream().forEach(x -> System.out.println(x));
	}

	@Override
	protected void addQuoteToSession(ReservationSession session, String name, Date start, Date end, String carType, String region) throws RemoteException, ReservationException {
		ReservationConstraints rc = new ReservationConstraints(start, end, carType, region);
		session.addQuote(rc);
	}

	@Override
	protected List<Reservation> confirmQuotes(ReservationSession session, String name) throws RemoteException, ReservationException {
		// rollback possible here, can be done on CRA level but youĺl need error handling
		return session.confirmQuotes(name);
	}

	@Override
    protected String getCheapestCarType(ReservationSession session, Date start, Date end, String region) throws RemoteException {
		return session.getCheapestCarType(start, end, region);
	}


	/***********************************************************
	 * MANAGERSESSION
	 * 
	 * @throws Exception
	 ***********************************************************/

	protected void registerCrc(ManagerSession session, String name) throws Exception {
		session.registerCRC(name);
	}

    protected void unregisterCrc(ManagerSession session, String name) throws RemoteException{
		session.unregisterCRC(name);
	}

	@Override
    protected int getNumberOfReservationsForCarType(ManagerSession ms, String carRentalName, String carType) throws RemoteException {
		return ms.getNumberOfReservationsForCarType(carRentalName, carType);
	}

	@Override
    protected int getNumberOfReservationsByRenter(ManagerSession ms, String clientName) throws RemoteException {
		return ms.getNumberOfReservationsByRenter(clientName);
	}

	/**
	 * Alls renters who have the highest number of reservations
	 **/
	@Override
    protected Set<String> getBestClients(ManagerSession ms) throws RemoteException {
		return ms.getBestClients();
	}


	/**
	 * 
	 **/
	@Override
    protected CarType getMostPopularCarTypeIn(ManagerSession ms, String carRentalCompanyName, int year) throws RemoteException {
		return ms.getMostPopularCarTypeIn(carRentalCompanyName, year);
	}

}

	