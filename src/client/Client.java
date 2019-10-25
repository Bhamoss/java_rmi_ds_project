package client;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;
import rental.CarType;
import rental.ICarRentalCompany;

public class Client extends AbstractTestBooking {

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

		String carRentalCompanyName = "Hertz";

		System.setSecurityManager(null);
		//TODO: change to carRentalAgency
		// the stub to filled with the remote object
		ICarRentalCompany client_side_crc_stub = null;
		Registry registry;

		// split into local and remote cases

		if (localOrRemote == LOCAL) {
			registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
			client_side_crc_stub = (ICarRentalCompany) registry.lookup("crc");
		} else {
			registry = LocateRegistry.getRegistry("192.168.104.76", Client.RMI_PORT);
			client_side_crc_stub = (ICarRentalCompany) registry.lookup("crc");
		}

		// An example reservation scenario on car rental company 'Hertz' would be...
		Client client = new Client("simpleTrips", carRentalCompanyName, localOrRemote, client_side_crc_stub);

		// starting here we can use the stub, when we have implemented everything ok
		// I suggest adding a crc field to the client holding a stub so we can use the
		// stub in the mehtods

		// this runs the tests of the supervisors
		client.run();
	}

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public Client(String scriptFile, String carRentalCompanyName, int localOrRemote, ICarRentalCompany icrc) {
		super(scriptFile);
		setCrc_stub(icrc);
		// ask CRA for session and set it, fuk dat crc
	}

	// The car rental company interface stub
	private ICarRentalCompany crc_stub;

	private ICarRentalCompany getCrc_stub() {
		return crc_stub;
	}

	private void setCrc_stub(ICarRentalCompany crc_stub) {
		this.crc_stub = crc_stub;
	}

	/**
	 * Check which car types are available in the given period (across all companies
	 * and regions) and print this list of car types.
	 *
	 * @param start start time of the period
	 * @param end   end time of the period
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected void checkForAvailableCarTypes(Date start, Date end) throws RemoteException {
		System.out.print("\nAvailabel car types for period ");
		System.out.print(start);
		System.out.print(" - ");
		System.out.print(end);
		System.out.print("\n");
		getCrc_stub().getAvailableCarTypes(start, end).stream().forEach(x -> System.out.println(x));
	}

	/**
	 * Retrieve a quote for a given car type (tentative reservation).
	 * 
	 * @param clientName name of the client
	 * @param start      start time for the quote
	 * @param end        end time for the quote
	 * @param carType    type of car to be reserved
	 * @param region     region in which car must be available
	 * @return the newly created quote
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected Quote createQuote(String clientName, Date start, Date end, String carType, String region)
			throws ReservationException, RemoteException {

		// 1) put the info into constraints

		ReservationConstraints rc = new ReservationConstraints(start, end, carType, region);

		// 2) call the stub function with the constraints
		Quote q = getCrc_stub().createQuote(rc, clientName);

		// 3) print the results

		System.out.println(q);

		return q;
	}

	/**
	 * Confirm the given quote to receive a final reservation of a car.
	 * 
	 * @param quote the quote to be confirmed
	 * @return the final reservation of a car
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected Reservation confirmQuote(Quote quote) throws ReservationException, RemoteException {

		Reservation reservation = getCrc_stub().confirmQuote(quote);

		System.out.println(reservation);

		return reservation;
	}

	/**
	 * Get all reservations made by the given client.
	 *
	 * @param clientName name of the client
	 * @return the list of reservations of the given client
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected List<Reservation> getReservationsByRenter(String clientName)
			throws ReservationException, RemoteException {

		List<Reservation> reservations = getCrc_stub().getReservationsByRenter(clientName);

		reservations.stream().forEach(x -> System.out.println(x.clientInfo()));

		return reservations;
	}

	/**
	 * Get the number of reservations for a particular car type.
	 * 
	 * @param carType name of the car type
	 * @return number of reservations for the given car type
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected int getNumberOfReservationsForCarType(String carType) throws RemoteException {
		return getCrc_stub().getNumberOfReservationsForCarType(carType);
	}

	@Override
	protected Object getNewReservationSession(String name) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object getNewManagerSession(String name, String carRentalName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void checkForAvailableCarTypes(Object session, Date start, Date end) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void addQuoteToSession(Object session, String name, Date start, Date end, String carType, String region)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected List confirmQuotes(Object session, String name) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int getNumberOfReservationsByRenter(Object ms, String clientName) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int getNumberOfReservationsForCarType(Object ms, String carRentalName, String carType) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void processLine(String name, String cmd, List<Character> flags, StringTokenizer scriptLineTokens)
			throws ApplicationException {
		// TODO Auto-generated method stub

	}
}