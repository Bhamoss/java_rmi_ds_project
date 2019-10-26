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
		


		CrcData data  = loadData("hertz.csv");
		CarRentalCompany server_side_crc = new CarRentalCompany(data.name, data.regions, data.cars);
		
		// set security managaer to null
		System.setSecurityManager(null);
		
		ICarRentalCompany server_side_stub = null;
		Registry registry;

		String ip;
		int rmiPort;
		
		if (localOrRemote == LOCAL) {
			// main when executing local
			
			
			
			// make our local object available THROUGH its remote interface
			server_side_stub = (ICarRentalCompany) UnicastRemoteObject.exportObject(server_side_crc, 0);
			
			// we zoeken naar onze registry, wat op de locale pc zit in dit geval
			registry = LocateRegistry.getRegistry("127.0.0.1", 1099);
			
			// we make our stub available in the registry
			registry.rebind("crc", server_side_stub);
			
		}
		else {
		// main when executing remote
			
			
			
			server_side_stub = (ICarRentalCompany) UnicastRemoteObject.exportObject(server_side_crc, STUB_PORT);
			
			
			registry = LocateRegistry.createRegistry(RMI_PORT);
			
			// we make our stub available in the registry
			registry.rebind("crc", server_side_stub);
			
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
