package rental;

public abstract class Session {
    public Session(CarRentalAgency cra, String name){
        this.cra = cra;
        this.name = name;
    }



    private final String name;

    public String getName() {
        return name;
    }

    private CarRentalAgency getCra() {
        return this.cra;
    }
    
    private final CarRentalAgency cra;

    

}