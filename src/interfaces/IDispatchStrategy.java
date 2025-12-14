package interfaces;
import models.Incident;
import models.JRG;
import java.util.List;

// kontrakt dla strategii dysponowania jednostek (Warunek 3)
public interface IDispatchStrategy {
    void executeDispatch(Incident incident, List<JRG> jrgs, boolean isFalseAlarm);
}