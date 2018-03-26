package es.ucm.fdi.Events;

import org.junit.Assert;
import org.junit.Test;

import es.ucm.fdi.Exceptions.SimulatorException;
import es.ucm.fdi.SimulatedObjects.Junction;
import es.ucm.fdi.Simulator.RoadMap;

/** 
 * La clase NewJunctionEventTest se encarga de probar que NewJunctionEvent funciona correctamente.
 * @author Jaime Fernández y Brian Leiva
*/

public class NewJunctionEventTest {
	
	/**
	 * Método que prueba el método execute de NewJunctionEvent.
	 */
	
	@Test
	public void testExecute(){
		RoadMap m = new RoadMap();
		NewJunctionEvent j = new NewJunctionEvent(3, "j7");
		
		try {
			j.execute(m);
		} catch (SimulatorException e) {
			Assert.fail();
			System.out.println("Fallo en la ejecución");
		}
		
		Junction x = m.getJunctions().get(m.getJunctions().size() - 1);
		Assert.assertEquals("El ID del cruce creado es correcto", "j7", x.getID());
	}
}

