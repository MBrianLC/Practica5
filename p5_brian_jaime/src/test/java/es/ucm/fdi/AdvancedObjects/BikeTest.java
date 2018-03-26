package es.ucm.fdi.AdvancedObjects;

import java.util.ArrayList;

import org.junit.Test;

import es.ucm.fdi.SimulatedObjects.Junction;

import java.util.List;
import static org.junit.Assert.*;

public class BikeTest {
	List<Junction> itinerario = new ArrayList<>();
	Bike b1 = new Bike("b1", 15, itinerario);
	
	@Test
	public void faultyFalseTest() {
		b1.setVelocidadActual(5);
		b1.setTiempoAveria(1);
		assertFalse("El vehículo no debería haberse averiado", b1.getAveria());
	}	
	
	@Test
	public void faultyTrueTest() {
		b1.setVelocidadActual(10);
		b1.setTiempoAveria(1);
		assertTrue("El vehículo debería haberse averiado", b1.getAveria());
	}
}