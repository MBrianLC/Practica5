package es.ucm.fdi.model.Events;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import es.ucm.fdi.model.Exceptions.SimulatorException;
import es.ucm.fdi.model.SimulatedObjects.Junction;
import es.ucm.fdi.model.SimulatedObjects.Vehicle;
import es.ucm.fdi.model.Simulator.RoadMap;

/** 
 * La clase VehicleFaultyEventTest se encarga de probar que VehicleFaultyEvent funciona correctamente.
 * @author Jaime Fernández y Brian Leiva
*/

public class VehicleFaultyEventTest {
	
	@Test
	public void testExecute(){
		RoadMap m = new RoadMap();
		String[] s = {"v3", "v5", "v7"};
		List<Junction> l = new ArrayList<>();
		Junction j = new Junction("j1");
		l.add(j);
		Vehicle a = new Vehicle("v3", 20, l);
		Vehicle b = new Vehicle("v5", 20, l);
		Vehicle c = new Vehicle("v7", 20, l);
		m.addVehicle(a);
		m.addVehicle(b);
		m.addVehicle(c);
		VehicleFaultyEvent e = new VehicleFaultyEvent(3, s, 4);
		
		try {
			e.execute(m);
		} catch (SimulatorException ex) {
			Assert.fail();
			System.out.println("Fallo en la ejecución");
		}
		
		for (String v : s){
			Assert.assertTrue("Los vehiculos han sido averiados", m.getVehicle(v).getAveria());
		}
	}
}