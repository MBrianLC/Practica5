package es.ucm.fdi.Events;

import es.ucm.fdi.Exceptions.SimulatorException;
import es.ucm.fdi.AdvancedObjects.RoundRobin;
import es.ucm.fdi.Simulator.RoadMap;

/** 
 * La clase NewRoundRobinEvent se encarga de crear un cruce circular
 * @author Jaime Fernández y Brian Leiva
*/

public class NewRoundRobinEvent extends NewJunctionEvent{
	private int max_time_slice, min_time_slice;
	
	
	/** 
	 * Constructor de la clase NewRoundRobinEvent
	 * @param time Entero que representa el momento en el que ocurrirá el evento.
	 * @param id String con el identificador del camino
	 * @param max Máximo valor del intervalo de tiempo
	 * @param min Mínimo valor del intervalo de tiempo
	*/

	public NewRoundRobinEvent(int time, String id, int max_time_slice, int min_time_slice) {
		super(time, id);
		this.max_time_slice = max_time_slice;
		this.min_time_slice = min_time_slice;
	}
	
	/** 
	 * Método que ejecuta el evento de creación de un nuevo cruce circular
	 * @param map El mapa de carreteras e intersecciones.
	 * @throws SimulatorException 
	*/

	public void execute(RoadMap map) throws SimulatorException {
		try{
			map.addJunction(new RoundRobin(id, max_time_slice, min_time_slice));
		}
		catch(IllegalArgumentException e) {
			throw new SimulatorException("RoundRobin " + id + ": id already exists");
		}
	}

}
