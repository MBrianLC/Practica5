package es.ucm.fdi.Events;

import es.ucm.fdi.Exceptions.SimulatorException;
import es.ucm.fdi.Simulator.RoadMap;

/** 
 * La clase Event es una clase abstracta de la cual heredan los eventos.
 * @author Jaime Fernández y Brian Leiva
*/

public abstract class Event {
	private int time;
	
	/** 
	 * Constructor de la clase Event
	 * @param t Entero que representa el momento en el que ocurrirá el evento.
	*/
	public Event(int t) {
		time = t;
	}
	
	/** 
	 * Método para obtener el momento en el que ocurrirá el evento.
	*/
	public int getTime() {
		return time;
	}
	
	/** 
	 * Método abstracto que ejecuta el evento
	 * @param map El mapa de carreteras e intersecciones.
	 * @throws SimulatorException 
	*/
	public abstract void execute(RoadMap map) throws SimulatorException;
	
}
