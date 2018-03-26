package es.ucm.fdi.Events;

import es.ucm.fdi.Exceptions.SimulatorException;
import es.ucm.fdi.AdvancedObjects.Dirt;
import es.ucm.fdi.SimulatedObjects.Junction;
import es.ucm.fdi.SimulatedObjects.Road;
import es.ucm.fdi.Simulator.RoadMap;

/** 
 * La clase NewDirtEvent se encarga de crear un camino
 * @author Jaime Fernández y Brian Leiva
*/

public class NewDirtEvent extends NewRoadEvent{
	
	/** 
	 * Constructor de la clase NewDirtEvent
	 * @param time Entero que representa el momento en el que ocurrirá el evento.
	 * @param id String con el identificador del camino
	 * @param sr String con el identificador del cruce inicial
	 * @param dest String con el identificador del cruce final
	 * @param max Entero con la velocidad máxima permitida en el camino
	 * @param length Entero con la longitud del camino
	*/

	public NewDirtEvent(int time, String id, String sr, String dest, int max, int length) {
		super(time, id, sr, dest, max, length);
	}
	
	/** 
	 * Método que ejecuta el evento de creación de un nuevo camino
	 * @param map El mapa de carreteras e intersecciones.
	 * @throws SimulatorException 
	*/

	public void execute(RoadMap map) throws SimulatorException {
		try {
			Junction ini = map.getJunction(sr);
			Junction fin = map.getJunction(dest);
			Road r = new Dirt(id, length, max, ini, fin);
			ini.addSale(r);
			fin.addEntra(r);
			map.addRoad(r);
		}
		catch(NullPointerException e) {
			throw new SimulatorException("Dirt " + id + ": invalid source/destiny junctions");
		}
		catch(IllegalArgumentException e) {
			throw new SimulatorException("Dirt " + id + ": id already exists");
		}
	}

}
