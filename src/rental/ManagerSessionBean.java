package rental;

public class ManagerSessionBean extends Session implements ManagerSession {

    public ManagerSessionBean(CarRentalAgency cra) {
        super(cra);
    }

}