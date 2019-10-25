package rental;

public abstract class Session {
    public Session(CarRentalAgency cra){
        this.cra = cra;
    }

    private CarRentalAgency getCra() {
        return this.cra;
    }
    
    private final CarRentalAgency cra;

    

}