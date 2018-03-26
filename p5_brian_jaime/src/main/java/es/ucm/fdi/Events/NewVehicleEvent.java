package es.ucm.fdi.Events;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.Exceptions.SimulatorException;
import es.ucm.fdi.SimulatedObjects.Junction;
import es.ucm.fdi.SimulatedObjects.Vehicle;
import es.ucm.fdi.Simulator.RoadMap;

/** 
 * La clase NewVehicleEvent se encarga de crear un vehículo
 * @author Jaime Fernández y Brian Leiva
*/

public class NewVehicleEvent extends Event{
	protected String id;
	protected String[] cruces;
	protected int max;
	
	/** 
	 * Constructor de la clase NewVehicleEvent
	 * @param time Entero que representa el momento en el que ocurrirá el evento.
	 * @param id String con el identificador del cruce
	 * @param max Entero con la velocidad máxima del vehículo
	 * @param cruces Array de strings con el itinerario de cruces del vehículo
	*/
	
	public NewVehicleEvent(int time, String id, int max, String[] cruces) {
		super(time);
		this.id = id;
		this.max = max;
		this.cruces = cruces;
	}
	
	/** 
	 * Método que ejecuta el evento de creación de un nuevo vehículo.
	 * @param map El mapa de carreteras e intersecciones.
	 * @throws SimulatorException 
	*/
	
	public void execute(RoadMap map) throws SimulatorException {
		
		List<Junction> itinerario = new ArrayList<>();
		try {
			for (String n : cruces)
				itinerario.add(map.getJunction(n));
			Vehicle v = new Vehicle(id, max, itinerario);
			v.moverASiguienteCarretera(itinerario.get(0).road(v));
			map.addVehicle(v);
		}
		catch(NullPointerException e) {
			throw new SimulatorException("Vehicle " + id + ": invalid itinerary");
		}
		catch(IllegalArgumentException e) {
			throw new SimulatorException("Vehicle " + id + ": id already exists");
		}
	}
}
