package rental;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class CarRentalCompany implements ICarRentalCompany{

	private static Logger logger = Logger.getLogger(CarRentalCompany.class.getName());
	
	private List<String> regions;
	private String name;
	private List<Car> cars;
	private Map<String,CarType> carTypes = new HashMap<String, CarType>();

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public CarRentalCompany(String name, List<String> regions, List<Car> cars) {
		logger.log(Level.INFO, "<{0}> Car Rental Company {0} starting up...", name);
		setName(name);
		this.cars = cars;
		setRegions(regions);
		for(Car car:cars)
			carTypes.put(car.getType().getName(), car.getType());
		logger.log(Level.INFO, this.toString());
	}

	/********
	 * NAME *
	 ********/

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

    /***********
     * Regions *
     **********/
    private void setRegions(List<String> regions) {
        this.regions = regions;
    }
    
    public List<String> getRegions() {
        return this.regions;
    }
    
    public boolean operatesInRegion(String region) {
        return this.regions.contains(region);
    }
	
	/*************
	 * CAR TYPES *
	 *************/

	public Collection<CarType> getAllCarTypes() {
		return carTypes.values();
	}
	
	public CarType getCarType(String carTypeName) {
		if(carTypes.containsKey(carTypeName))
			return carTypes.get(carTypeName);
		throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
	}
	
	// mark
	public boolean isAvailable(String carTypeName, Date start, Date end) {
		logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[]{name, carTypeName});
		if(carTypes.containsKey(carTypeName)) {
			return getAvailableCarTypes(start, end).contains(carTypes.get(carTypeName));
		} else {
			throw new IllegalArgumentException("<" + carTypeName + "> No car type of name " + carTypeName);
		}
	}
	
	public Set<CarType> getAvailableCarTypes(Date start, Date end) {
		Set<CarType> availableCarTypes = new HashSet<CarType>();
		for (Car car : cars) {
			if (car.isAvailable(start, end)) {
				availableCarTypes.add(car.getType());
			}
		}
		return availableCarTypes;
	}

	/**
	 * returns the cheapest car type available in the given period
	 */
	public CarType getCheapestCarType(Date start, Date end) {
		double cheapestPrice = Double.MAX_VALUE;
		CarType cheapestCarType;
		for (CarType ct : getAvailableCarTypes(start, end)) {
			if (cheapestPrice > ct.getRentalPricePerDay()) {
				cheapestCarType = ct;
				cheapestPrice = ct.getRentalPricePerDay();
			}
		}
		return cheapestCarType;

	}

	/**
	 * Get the most popular car type of the given year
	 */
	public CarType getMostPopularCarTypeIn(int year) {

		Map<CarType, Integer> cntCarTypes = new Map<CarType, Integer>();
		Integer amountReservations;
		for (Reservation r : getAllReservationsIn(year)) {
			amountReservations = 1;
			if (cntCarTypes.containsKey(r.getCarType())) {
				amountReservations += CarTypes.get(r.getCarType());
			}
			cntCarTypes.put(r.getCarType(), amountReservations);
		}


		

	}
	
	
	/*********
	 * CARS *
	 *********/
	
	private List<Car> getCars(){
		return this.cars;
	}
	
	private Car getCar(int uid) {
		for (Car car : cars) {
			if (car.getId() == uid)
				return car;
		}
		throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
	}
	
	private List<Car> getAvailableCars(String carType, Date start, Date end) {
		List<Car> availableCars = new LinkedList<Car>();
		for (Car car : cars) {
			if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
				availableCars.add(car);
			}
		}
		return availableCars;
	}

	/***********
	 * CLIENTS *
	 ***********/

	public Set<String> getAllClientNames() {
		Set<String> clients = new Set<String>();
		for (Reservation r : getAllReservations()) {
			clients.add(r.getCarRenter());
		}

		return clients;
	}

	
	/****************
	 * RESERVATIONS *
	 ****************/
	
	public int getNumberOfReservationsForCarType(String carType) {
		return getAllReservations().stream()
				.filter(x -> x.getCarType().equals(carType))
				.collect(Collectors.toList())
				.size();
	}
	
	public List<Reservation> getReservationsByRenter(String clientName) 
			throws ReservationException {
		return getAllReservations().stream()
				.filter(x -> x.getCarRenter().equals(clientName))
				.collect(Collectors.toList());
	}
	
	private List<Reservation> getAllReservations() {
		List<Reservation> reservations = new LinkedList<>();
		Iterator<Reservation> reservation_it;
		
		for (Car car : getCars()) {
			reservation_it = car.getReservations();
			while (reservation_it.hasNext()) {
				reservations.add(reservation_it.next());
			}
		}
		
		return reservations;
		
	}

	private List<Reservation> getAllReservationsIn(int year) {
		List<Reservation> reservations = new LinkedList<>();
		Iterator<Reservation> reservation_it;
		Reservation tmpReservation;
		for (Car car : getCars()) {
			reservation_it = car.getReservations();
			while (reservation_it.hasNext()) {
				tmpReservation = reservation_it.next();
				if (tmpReservation.getStartDate().getYear() == year) {
					reservations.add(tmpReservation);
				}
			}
		}
		
		return reservations;
		
	}

	public Quote createQuote(ReservationConstraints constraints, String client)
			throws ReservationException {
		logger.log(Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}", 
                        new Object[]{name, client, constraints.toString()});
		
				
		if(!operatesInRegion(constraints.getRegion()) || !isAvailable(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate()))
			throw new ReservationException("<" + name
				+ "> No cars available to satisfy the given constraints.");

		CarType type = getCarType(constraints.getCarType());
		
		double price = calculateRentalPrice(type.getRentalPricePerDay(),constraints.getStartDate(), constraints.getEndDate());
		
		return new Quote(client, constraints.getStartDate(), constraints.getEndDate(), getName(), constraints.getCarType(), price);
	}

	// Implementation can be subject to different pricing strategies
	private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
		return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime())
						/ (1000 * 60 * 60 * 24D));
	}

	// made this method synchronised
	public synchronized Reservation confirmQuote(Quote quote) throws ReservationException {
		logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[]{name, quote.toString()});
		List<Car> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
		if(availableCars.isEmpty())
			throw new ReservationException("Reservation failed, all cars of type " + quote.getCarType()
	                + " are unavailable from " + quote.getStartDate() + " to " + quote.getEndDate());
		Car car = availableCars.get((int)(Math.random()*availableCars.size()));
		
		Reservation res = new Reservation(quote, car.getId());
		car.addReservation(res);
		return res;
	}

	public void cancelReservation(Reservation res) {
		logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[]{name, res.toString()});
		getCar(res.getCarId()).removeReservation(res);
	}
	
	@Override
	public String toString() {
		return String.format("<%s> CRC is active in regions %s and serving with %d car types", name, listToString(regions), carTypes.size());
	}
	
	private static String listToString(List<? extends Object> input) {
		StringBuilder out = new StringBuilder();
		for (int i=0; i < input.size(); i++) {
			if (i == input.size()-1) {
				out.append(input.get(i).toString());
			} else {
				out.append(input.get(i).toString()+", ");
			}
		}
		return out.toString();
	}
	
}