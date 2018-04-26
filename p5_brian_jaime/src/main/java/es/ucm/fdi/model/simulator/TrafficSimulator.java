package es.ucm.fdi.model.simulator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.exceptions.SimulatorException;
import es.ucm.fdi.model.simobjects.Junction;
import es.ucm.fdi.model.simobjects.Road;
import es.ucm.fdi.model.simobjects.SimObject;
import es.ucm.fdi.model.simulator.Listener;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.ini.IniSection;
import es.ucm.fdi.util.MultiTreeMap;

/** 
 * La clase TrafficSimulator representa el simulador.
 * @author Jaime Fernández y Brian Leiva
*/

public class TrafficSimulator {
	private int contadorTiempo;
	private MultiTreeMap<Integer, Event> eventos;
	private List<Event> eventsQueue;
	private RoadMap SimObjects;
	private List<Listener> listeners = new ArrayList<>();
	
	/** 
	 * Constructor de la clase TrafficSimulator.
	*/
	
	public TrafficSimulator() {
		SimObjects = new RoadMap();
		eventos = new MultiTreeMap<>();
		contadorTiempo = 0;
		eventsQueue = new ArrayList<>();
	}
	
	/** 
	 * Añade un evento a la lista de eventos.
	 * @param e : Evento
	*/
	
	public void insertaEvento(Event e) {
		if (e.getTime() < 0) {
			fireUpdateEvent(EventType.ERROR, "ERROR: Invalid time");
			throw new IllegalArgumentException("Invalid time");
		}
		if (e.getTime() >= contadorTiempo) {
			eventos.putValue(e.getTime(), e);
			eventsQueue.add(e);
			fireUpdateEvent(EventType.NEWEVENT, "");
		}
	}
	
	public void addSimulatorListener(Listener l) {
		listeners.add(l);
		UpdateEvent ue = new UpdateEvent(EventType.REGISTERED);
		SwingUtilities.invokeLater(()->l.update(ue, ""));
	}
	
	public void removeListener(Listener l) {
		listeners.remove(l);
	}
	
	public void resetEvents() {
		eventos = new MultiTreeMap<>();
	}
	
	public void resetSim() {
		SimObjects = new RoadMap();
		eventos = new MultiTreeMap<>();
		contadorTiempo = 0;
		eventsQueue = new ArrayList<>();
		fireUpdateEvent(EventType.RESET, "");
	}
	
	/** 
	 * Envía un evento apropiado a todos los listeners.
	 * @param type : Tipo de evento
	 * @param error : String que detalla el error (evento tipo ERROR)
	*/	
	
	private void fireUpdateEvent(EventType type, String error) {
		UpdateEvent ue = new UpdateEvent(type);
		for (Listener l : listeners) l.update(ue, "ERROR");
	}
	
	/** 
	 * Devuelve el informe de salida en formato Ini.
	 * @return salida : Informe del simulador
	*/
	
	public Ini report() {
		Map<String, String> m = new LinkedHashMap<>();
		Ini salida = new Ini();
		for (SimObject sim : SimObjects.getSimObjects()) {
			sim.report(contadorTiempo, m);
			IniSection s = new IniSection(m.get(""));
			for (String key: m.keySet()) {
				if (key != "") s.setValue(key, m.get(key));
			}
			salida.addsection(s);
			m.clear();
		}
		return salida;
	}
	
	/** 
	 * Ejecución del simulador.
	 * @param pasosSimulacion : Tiempo que dura la simulación
	 * @param o : Flujo de salida
	 * @throws IOException 
	 * @throws SimulatorException 
	*/
	
	public void execute(int pasosSimulacion, OutputStream o) throws IOException, SimulatorException {
		int limiteTiempo = contadorTiempo + pasosSimulacion - 1;
		while (contadorTiempo <= limiteTiempo) {
			List<Event> eventActuales = eventos.get(contadorTiempo);
			if (eventActuales != null) {
				for (Event e : eventActuales)
					e.execute(SimObjects);
			}
			for (Road r : SimObjects.getRoads())
				r.avanza();
			for (Junction j : SimObjects.getJunctions())
				j.avanza();
			contadorTiempo++;
			if (o != null) report().store(o);
			fireUpdateEvent(EventType.ADVANCED, "");
		}
	}
	
	/** 
	 * Devuelve el mapa de objetos simulados.
	 * @return RoadMap con los SimObjects de la simulación
	*/
	
	public RoadMap getMap() {
		return SimObjects;
	}
	
	/** 
	 * Devuelve la lista de eventos pendientes de la simulación.
	 * @return Cola de eventos
	*/
	
	public List<Event> getEventQueue(){
		return eventsQueue;
	}

	/** 
	 * Enumerado con el tipo de evento.
	*/
	
	public enum EventType {
		REGISTERED, RESET, NEWEVENT, ADVANCED, ERROR;
	}

	/** 
	 * Clase interna en el simulador para los posibles eventos.
	*/
	
	public class UpdateEvent {
		EventType type;
		
		public UpdateEvent(EventType type) {
			this.type = type;
		}
		
		public EventType getEvent() {
			return type;
		}
		
		public RoadMap getRoadMap() {
			return SimObjects;
		}

		public List<Event> getEventQueue() {
			List<Event> aux = new ArrayList<>(eventsQueue);
			for (Event e : aux) {
				if (e.getTime() <= contadorTiempo)
					eventsQueue.remove(e);
			}
			return eventsQueue;
		}
		
		public int getCurrentTime() {
			return contadorTiempo;
		}
	}
}
