package es.ucm.fdi.Events;

import org.junit.Assert;
import org.junit.Test;

import es.ucm.fdi.Exceptions.SimulatorException;
import es.ucm.fdi.SimulatedObjects.Junction;
import es.ucm.fdi.SimulatedObjects.Road;
import es.ucm.fdi.SimulatedObjects.Vehicle;
import es.ucm.fdi.Simulator.RoadMap;

/** 
 * La clase NewCarEventTest se encarga de probar que NewCarEvent funciona correctamente.
 * @author Jaime Fernández y Brian Leiva
*/

public class NewCarEventTest {
	
	/**
	 * Método que prueba el método execute de NewCarEvent.
	 */
	
	@Test
	public void testExecute(){
		RoadMap m = new RoadMap();
		Junction a = new Junction("j3");
		Junction b = new Junction("j5");
		Junction c = new Junction("j6");
		Road k = new Road("r23", 90, 30, a, b);
		a.addSale(k);
		m.addJunction(a);
		m.addJunction(b);
		m.addJunction(c);
		String[] s = {"j3", "j5", "j6"};
		NewCarEvent v = new NewCarEvent(3, "v77", 20, s, 12, 0.8, 5, 589);
		
		try {
			v.execute(m);
		} catch (SimulatorException e) {
			Assert.fail();
			System.out.println("Fallo en la ejecución");
		}
		
		Vehicle x = m.getVehicles().get(m.getVehicles().size() - 1);
		Assert.assertEquals("El ID del vehiculo creado es correcto", "v77", x.getID());
		Assert.assertEquals("La carretera actual es correcta", "r23", x.getCarretera().getID());
		Assert.assertEquals("La posición es correcta", 0, x.getPos());
	}
}

