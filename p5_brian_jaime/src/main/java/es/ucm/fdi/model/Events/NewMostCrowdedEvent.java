package es.ucm.fdi.model.Events;

import es.ucm.fdi.model.Exceptions.SimulatorException;
import es.ucm.fdi.model.AdvancedObjects.MostCrowded;
import es.ucm.fdi.model.Simulator.RoadMap;

/** 
 * La clase NewMostCrowdedEvent se encarga de crear un cruce congestionado
 * @author Jaime Fernández y Brian Leiva
*/

public class NewMostCrowdedEvent extends NewJunctionEvent{	
	
	/** 
	 * Constructor de la clase NewMostCrowdedEvent
	 * @param time : Entero que representa el momento en el que ocurrirá el evento.
	 * @param id : String con el identificador del camino
	*/

	public NewMostCrowdedEvent(int time, String id) {
		super(time, id);
	}
	
	/** 
	 * Método que ejecuta el evento de creación de un nuevo cruce congestionado
	 * @param map : El mapa de carreteras e intersecciones.
	 * @throws SimulatorException 
	*/

	public void execute(RoadMap map) throws SimulatorException {
		try{
			map.addJunction(new MostCrowded(id));
		}
		catch(IllegalArgumentException e) {
			throw new SimulatorException("MostCrowded " + id + ": id already exists");
		}
	}

}
