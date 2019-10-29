/**
 * 
 */
package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author thomas
 *
 *	This class is the interface defined for the car rental company.
 *	This interface is what the client will be interacting with.
 *	The 'real'crc must implement this and will do the actual work.
 *	Put in this interface the methods you want to use in the client on the remote crc object.
 *
 */
public interface ICarRentalCompany extends Remote {
	
	// CarType, Quote, Reservation, ReservationConstraints and ReservationException have been made Serializable
	
	/**
	 * 
	 * CarTypes has been made serializable and Data is serializable by default.
	 * 
	 * @param start
	 * @param end
	 * @return
	 * @throws RemoteException
	 */
	public Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;
	
	
	
	
	/**
	 * You will be able to call this method now from the client.
	 * Quote has to be made serialisable, as wel as reservationConstraints if you would want
	 * To use this method
	 * 
	 * @param constraints
	 * @param client
	 * @return
	 * @throws ReservationException
	 * @throws RemoteException
	 */
	public Quote createQuote(ReservationConstraints constraints, String client)
			throws ReservationException, RemoteException;
	
	
	/**
	 * 
	 * @param quote
	 * @return
	 * @throws ReservationException
	 * @throws RemoteException
	 */
	public Reservation confirmQuote(Quote quote) throws ReservationException, RemoteException;
	
	
	/**
	 * 
	 * @param clientName
	 * @return
	 * @throws ReservationException
	 * @throws RemoteException
	 */
	public List<Reservation> getReservationsByRenter(String clientName) 
			throws RemoteException;
	
	/**
	 * 
	 * @param carType
	 * @return
	 * @throws RemoteException
	 */
	public int getNumberOfReservationsForCarType(String carType) throws RemoteException;

	public CarType getCheapestCarType(Date start, Date end) throws RemoteException;

	public boolean operatesInRegion(String region) throws RemoteException;

	public String getName() throws RemoteException;

	public Set<String> getAllClientNames() throws RemoteException;

	public CarType getMostPopularCarTypeIn(int year) throws RemoteException;

	public void cancelReservation(Reservation res) throws RemoteException;
}
