package interfaces;

import models.Car;
import models.Incident;
import models.JRG;
import java.util.List;

public interface IDispatchStrategy {

    List<Car> selectCars(Incident incident, List<JRG> jrgs);

}