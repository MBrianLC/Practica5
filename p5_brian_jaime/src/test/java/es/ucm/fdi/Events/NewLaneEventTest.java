package es.ucm.fdi.Events;

import org.junit.Assert;
import org.junit.Test;

import es.ucm.fdi.Exceptions.SimulatorException;
import es.ucm.fdi.SimulatedObjects.Junction;
import es.ucm.fdi.SimulatedObjects.Road;
import es.ucm.fdi.Simulator.RoadMap;

/** 
 * La clase NewLaneEventTest se encarga de probar que NewLaneEvent funciona correctamente.
 * @author Jaime Fernández y Brian Leiva
*/

public class NewLaneEventTest {
	
	/**
	 * Método que prueba el método execute de NewLaneEvent.
	 */
	
	@Test
	public void testExecute(){
		RoadMap m = new RoadMap();
		Junction a = new Junction("j35");
		Junction b = new Junction("j6");
		m.addJunction(a);
		m.addJunction(b);
		NewLaneEvent r = new NewLaneEvent(3, "r46", "j35", "j6", 20, 60, 2);
		
		try {
			r.execute(m);
		} catch (SimulatorException e) {
			Assert.fail();
			System.out.println("Fallo en la ejecución");
		}
		
		Road x = m.getRoads().get(m.getRoads().size() - 1);
		Assert.assertEquals("El ID de la autopista creada es correcto", "r46", x.getID());
		Assert.assertEquals("El cruce inicial es correcto", "j35", x.getIni().getID());
		Assert.assertEquals("El cruce final es correcto", "j6", x.getFin().getID());
	}
}

