package rental;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import client.Client;

import java.rmi.registry.*;

public class RentalServer {
	
	private final static int LOCAL = 0;
	private final static int REMOTE = 1;
	public   static int RMI_PORT = 10926; //10926-10930
	public   static int RESERVATION_SESSIONS_PORT = 10927; 
	public   static int MANAGER_SESSIONS_PORT = 10928;
	public   static int CRCS_PORT = 10929;
	public   static int CRA_PORT = 10930;
	public   static String RMI_IP = "192.168.104.76";



	public static void main(String[] args) throws ReservationException,
			NumberFormatException, IOException {
		// The first argument passed to the `main` method (if present)
		// indicates whether the application is run on the remote setup or not.
		int localOrRemote = (args.length == 1 && args[0].equals("REMOTE")) ? REMOTE : LOCAL;
			
		//TODO: // create a cra and its stub
		//TODO: read in the other csv and push it also to the cra registry
		//TODO: Then create a master session to register them on the cra
		

		/*******************************************
		 * 	CREATING AND REGISTERING OUR CAR COMPANIES
		 */

		// Reading in the data of the test companies
		CrcData dhertz  = loadData("hertz.csv");
		CrcData ddockx = loadData("dockx.csv");

		CarRentalCompany hertz = new CarRentalCompany(dhertz.name, dhertz.regions, dhertz.cars);
		CarRentalCompany dockx = new CarRentalCompany(ddockx.name, ddockx.regions, ddockx.cars);
		
		// set security managaer to null
		System.setSecurityManager(null);
		
		Registry registry;
		
		if (localOrRemote == LOCAL) {
			// main when executing local

			// important to set variables because the classes use them
			// 0 means it will choose a random free port
			RESERVATION_SESSIONS_PORT = 0; 
			MANAGER_SESSIONS_PORT = 0;
			CRCS_PORT = 0;
			CRA_PORT = 0;
			RMI_IP = "127.0.0.1";
			
			// we zoeken naar onze registry, wat op de locale pc zit in dit geval
			// ant script creates it itself, and I don't wanna mess with ants. Sneaky buggers.
			registry = LocateRegistry.getRegistry(RMI_IP, RMI_PORT);
		}
		else {
		// main when executing remote
		// create our own registry when executing remote
			registry = LocateRegistry.createRegistry(RMI_PORT);
		}

		registry.rebind("hertz", (ICarRentalCompany) UnicastRemoteObject.exportObject(hertz, CRCS_PORT));
		registry.rebind("dockx", (ICarRentalCompany) UnicastRemoteObject.exportObject(dockx, CRCS_PORT));

		/*****************************************************
		 * CREATING THE AGENCY
		 */

		CarRentalAgency agency = new CarRentalAgency();
		// binding it as agency on the CRA port on the local registry
		registry.rebind("agency", (ICarRentalAgency) UnicastRemoteObject.exportObject(agency, CRA_PORT));

		/*****************************************************
		 * Registring our companies on the agency (we have to to it via rmi because that would be how you normally do it)
		 */

		// no script or local or remote thing necesary because we do not want to execute the script (DONT CALL RUN)
		// agency can also just be a cast to cicumvent using rmi
		Client client = new Client(null, 0, (ICarRentalAgency) agency, registry);
		//TODO: what is this carrentalname for??
		try{
			ManagerSession m = client.getNewManagerSession("init", "?");
		}
		catch (Exception e){
			// should never happen
		}

		
	}

	public static CrcData loadData(String datafile)
			throws ReservationException, NumberFormatException, IOException {

		CrcData out = new CrcData();
		int nextuid = 0;

		// open file
		InputStream stream = MethodHandles.lookup().lookupClass().getClassLoader().getResourceAsStream(datafile);
		if (stream == null) {
			System.err.println("Could not find data file " + datafile);
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		StringTokenizer csvReader;
		
		try {
			// while next line exists
			while (in.ready()) {
				String line = in.readLine();
				
				if (line.startsWith("#")) {
					// comment -> skip					
				} else if (line.startsWith("-")) {
					csvReader = new StringTokenizer(line.substring(1), ",");
					out.name = csvReader.nextToken();
					out.regions = Arrays.asList(csvReader.nextToken().split(":"));
				} else {
					// tokenize on ,
					csvReader = new StringTokenizer(line, ",");
					// create new car type from first 5 fields
					CarType type = new CarType(csvReader.nextToken(),
							Integer.parseInt(csvReader.nextToken()),
							Float.parseFloat(csvReader.nextToken()),
							Double.parseDouble(csvReader.nextToken()),
							Boolean.parseBoolean(csvReader.nextToken()));
					System.out.println(type);
					// create N new cars with given type, where N is the 5th field
					for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
						out.cars.add(new Car(nextuid++, type));
					}
				}
			}
		} finally {
			in.close();
		}

		return out;
	}
	
	static class CrcData {
		public List<Car> cars = new LinkedList<Car>();
		public String name;
		public List<String> regions =  new LinkedList<String>();
	}

}
