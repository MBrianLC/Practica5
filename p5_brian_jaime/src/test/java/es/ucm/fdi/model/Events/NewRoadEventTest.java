package es.ucm.fdi.model.Events;

import org.junit.Assert;
import org.junit.Test;

import es.ucm.fdi.model.Exceptions.SimulatorException;
import es.ucm.fdi.model.SimulatedObjects.Junction;
import es.ucm.fdi.model.SimulatedObjects.Road;
import es.ucm.fdi.model.Simulator.RoadMap;

/** 
 * La clase NewRoadEventTest se encarga de probar que NewRoadEvent funciona correctamente.
 * @author Jaime Fernández y Brian Leiva
*/

public class NewRoadEventTest {

	@Test
	public void testExecute(){
		RoadMap m = new RoadMap();
		Junction a = new Junction("j3");
		Junction b = new Junction("j6");
		m.addJunction(a);
		m.addJunction(b);
		NewRoadEvent r = new NewRoadEvent(3, "r45", "j3", "j6", 20, 60);
		
		try {
			r.execute(m);
		} catch (SimulatorException e) {
			Assert.fail();
			System.out.println("Fallo en la ejecución");
		}
		
		Road x = m.getRoads().get(m.getRoads().size() - 1);
		Assert.assertEquals("El ID de la carretera creada es correcto", "r45", x.getID());
		Assert.assertEquals("El cruce inicial es correcto", "j3", x.getIni().getID());
		Assert.assertEquals("El cruce final es correcto", "j6", x.getFin().getID());
	}
}
