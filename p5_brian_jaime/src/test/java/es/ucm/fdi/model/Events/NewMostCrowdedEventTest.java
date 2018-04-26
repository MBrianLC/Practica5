package es.ucm.fdi.model.Events;

import org.junit.Assert;
import org.junit.Test;

import es.ucm.fdi.model.Exceptions.SimulatorException;
import es.ucm.fdi.model.SimulatedObjects.Junction;
import es.ucm.fdi.model.Simulator.RoadMap;

/** 
 * La clase NewMostCrowdedEventTest se encarga de probar que NewMostCrowdedEvent funciona correctamente.
 * @author Jaime Fernández y Brian Leiva
*/

public class NewMostCrowdedEventTest {
	
	@Test
	public void testExecute(){
		RoadMap m = new RoadMap();
		NewMostCrowdedEvent j = new NewMostCrowdedEvent(3, "j9");
		
		try {
			j.execute(m);
		} catch (SimulatorException e) {
			Assert.fail();
			System.out.println("Fallo en la ejecución");
		}
		
		Junction x = m.getJunctions().get(m.getJunctions().size() - 1);
		Assert.assertEquals("El ID del cruce creado es correcto", "j9", x.getID());
	}
}