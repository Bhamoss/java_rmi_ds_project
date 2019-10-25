package rental;

import java.util.HashMap;
import java.util.Map;

public class CarRentalAgency implements ICarRentalAgency{

    public CarRentalAgency(){
        this.companies = new HashMap<String, ICarRentalCompany>();
    }

    private final Map<String, ICarRentalCompany> companies;
    

}