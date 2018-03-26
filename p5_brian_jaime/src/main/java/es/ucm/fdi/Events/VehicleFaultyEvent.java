package es.ucm.fdi.Events;

import es.ucm.fdi.Exceptions.SimulatorException;
import es.ucm.fdi.Simulator.RoadMap;

/** 
 * La clase VehicleFaultyEvent lleva a cabo la avería de los vehículos
 * @author Jaime Fernández y Brian Leiva
*/

public class VehicleFaultyEvent extends Event{
	private int duration;
	private String[] vehicles;
	
	/** 
	 * Constructor de la clase VehicleFaultyEvent
	 * @param time Entero que representa el momento en el que ocurrirá el evento.
	 * @param vehicles Array de strings con los vehículos que han de ser averiados
	 * @param duration Entero con la duración de la avería de los vehículos a averiar.
	*/

	public VehicleFaultyEvent(int t, String[] vehicles, int duration) {
		super(t);
		this.duration = duration;
		this.vehicles = vehicles;
	}
	
	/** 
	 * Método que ejecuta el evento de avería de vehículos.
	 * @param map El mapa de carreteras e intersecciones.
	 * @throws SimulatorException 
	*/

	public void execute(RoadMap map) throws SimulatorException {
		try {
		for (String v : vehicles)
			map.getVehicle(v).setTiempoAveria(duration);
		}
		catch(NullPointerException e) {
			throw new SimulatorException("VehicleFaulty " + ": invalid vehicles");
		}
	}
	
}
