package rental;

public class ManagerSessionBean extends Session implements ManagerSession {

    public ManagerSessionBean(CarRentalAgency cra, String name) {
        super(cra, name);
    }

}