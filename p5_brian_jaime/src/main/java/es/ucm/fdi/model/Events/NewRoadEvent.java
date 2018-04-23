package es.ucm.fdi.model.Events;

import es.ucm.fdi.model.Exceptions.SimulatorException;
import es.ucm.fdi.model.SimulatedObjects.Junction;
import es.ucm.fdi.model.SimulatedObjects.Road;
import es.ucm.fdi.model.Simulator.RoadMap;

/** 
 * La clase NewRoadEvent se encarga de crear una carretera
 * @author Jaime Fernández y Brian Leiva
*/

public class NewRoadEvent extends Event{
	protected String id, sr, dest;
	private String type;
	protected int max, length;
	
	/** 
	 * Constructor de la clase NewRoadEvent
	 * @param time : Entero que representa el momento en el que ocurrirá el evento.
	 * @param id : String con el identificador de la carretera
	 * @param sr : String con el identificador del cruce inicial
	 * @param dest : String con el identificador del cruce final
	 * @param max : Entero con la velocidad máxima permitida en la carretera
	 * @param length : Entero con la longitud de la carretera
	*/

	public NewRoadEvent(int time, String id, String sr, String dest, int max, int length) {
		super(time);
		this.id = id;
		this.sr = sr;
		this.dest = dest;
		this.max = max;
		this.length = length;
		type = "New Road " + id;
	}
	
	/** 
	 * Método que devuelve un String con el tipo de evento (Cola de eventos)
	 * @return El tipo de evento 
	*/	
	
	public String getType() {
		return type;
	}
	
	/** 
	 * Método que ejecuta el evento de creación de una nueva carretera
	 * @param map : El mapa de carreteras e intersecciones.
	 * @throws SimulatorException 
	*/

	public void execute(RoadMap map) throws SimulatorException {
		try {
			Junction ini = map.getJunction(sr);
			Junction fin = map.getJunction(dest);
			Road r = new Road(id, length, max, ini, fin);
			ini.addSale(r);
			fin.addEntra(r);
			map.addRoad(r);
		}
		catch(NullPointerException e) {
			throw new SimulatorException("Road " + id + ": invalid source/destiny junctions");
		}
		catch(IllegalArgumentException e) {
			throw new SimulatorException("Road " + id + ": id already exists");
		}
	}
	
}
