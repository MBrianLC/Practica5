package es.ucm.fdi.model.Events;

import es.ucm.fdi.model.Exceptions.SimulatorException;
import es.ucm.fdi.model.AdvancedObjects.RoundRobin;
import es.ucm.fdi.model.Simulator.RoadMap;

/** 
 * La clase NewRoundRobinEvent se encarga de crear un cruce circular
 * @author Jaime Fernández y Brian Leiva
*/

public class NewRoundRobinEvent extends NewJunctionEvent{
	private int max_time_slice, min_time_slice;
	private String type;
	
	
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
		type = "New RoundRobin " + id;
	}
	
	/** 
	 * Método que devuelve un String con el tipo de evento (Cola de eventos)
	 * @return El tipo de evento 
	*/	
	
	public String getType() {
		return type;
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
