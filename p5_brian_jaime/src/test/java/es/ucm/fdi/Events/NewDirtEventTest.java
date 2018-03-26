package es.ucm.fdi.Events;

import org.junit.Assert;
import org.junit.Test;

import es.ucm.fdi.Exceptions.SimulatorException;
import es.ucm.fdi.SimulatedObjects.Junction;
import es.ucm.fdi.SimulatedObjects.Road;
import es.ucm.fdi.Simulator.RoadMap;

/** 
 * La clase NewDirtEventTest se encarga de probar que NewDirtEvent funciona correctamente.
 * @author Jaime Fernández y Brian Leiva
*/

public class NewDirtEventTest {
	
	/**
	 * Método que prueba el método execute de NewDirtEvent.
	 */
	
	@Test
	public void testExecute(){
		RoadMap m = new RoadMap();
		Junction a = new Junction("j8");
		Junction b = new Junction("j1");
		m.addJunction(a);
		m.addJunction(b);
		NewDirtEvent r = new NewDirtEvent(3, "r9", "j8", "j1", 20, 60);
		
		try {
			r.execute(m);
		} catch (SimulatorException e) {
			Assert.fail();
			System.out.println("Fallo en la ejecución");
		}
		
		Road x = m.getRoads().get(m.getRoads().size() - 1);
		Assert.assertEquals("El ID del camino creado es correcto", "r9", x.getID());
		Assert.assertEquals("El cruce inicial es correcto", "j8", x.getIni().getID());
		Assert.assertEquals("El cruce final es correcto", "j1", x.getFin().getID());
	}
}
