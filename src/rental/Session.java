package rental;

import java.rmi.RemoteException;
import java.util.Arrays;

public abstract class Session {
    public Session(CarRentalAgency cra, String name) throws RemoteException{
        this.cra = cra;
        this.name = name;

        // synchronize because what if name gets taken inbetween

        synchronized (cra.getRegistry()) {

            while ( Arrays.asList( cra.getRegistry().list()).contains(name)) {
                name += 'x';
            }
            this.registryName = name;

            pushToRegistry();
        }
    }

    /**
     * Pushes a remote interface of the object to the registry of the cra.
     * @throws RemoteException
     */
    protected abstract void pushToRegistry() throws RemoteException;

    private final String registryName;

    public String getRegistryName() {
        return registryName;
    }



    private final String name;

    public String getName() {
        return name;
    }

    protected CarRentalAgency getCra() {
        return this.cra;
    }
    
    private final CarRentalAgency cra;


    /**
     * Destructor
     * @throws Exception
     */
    public void closeSession() throws Exception{
        // I do not put this in the explicit destructor (if that even is a thing in java) because the rmi registery
        // will hold a reference to this object, causing the garbage collector to not delete it
        getCra().getRegistry().unbind(getRegistryName());
    }

    

}
