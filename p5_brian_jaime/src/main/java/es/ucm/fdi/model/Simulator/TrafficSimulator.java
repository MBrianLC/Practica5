package es.ucm.fdi.model.Simulator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import es.ucm.fdi.model.Exceptions.SimulatorException;
import es.ucm.fdi.model.Events.Event;
import es.ucm.fdi.model.SimulatedObjects.Junction;
import es.ucm.fdi.model.SimulatedObjects.Road;
import es.ucm.fdi.model.SimulatedObjects.SimObject;
import es.ucm.fdi.model.Simulator.Listener;
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
	private RoadMap SimObjects;
	private List<Listener> listeners = new ArrayList<>();
	
	/** 
	 * Constructor de la clase TrafficSimulator.
	*/
	public TrafficSimulator() {
		this.SimObjects = new RoadMap();
		this.eventos = new MultiTreeMap<>();
		this.contadorTiempo = 0;
	}
	
	/** 
	 * Añade un evento a la lista de eventos.
	 * @param e : Evento
	*/
	public void insertaEvento(Event e) {
		if (e.getTime() < contadorTiempo)
			throw new IllegalArgumentException("Invalid time");
		eventos.putValue(e.getTime(), e);
	}
	
	public void addSimulatorListener(Listener l) {
		listeners.add(l);
		UpdateEvent ue = new UpdateEvent(EventType.REGISTERED);
		// evita pseudo-recursividad
		SwingUtilities.invokeLater(()->l.update(ue, "ERROR"));
	}
	
	public void removeListener(Listener l) {
		listeners.remove(l);
	}
	
	// uso interno, evita tener que escribir el mismo bucle muchas veces
	private void fireUpdateEvent(EventType type, String error) {
		// envia un evento apropiado a todos los listeners
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
		int limiteTiempo = this.contadorTiempo + pasosSimulacion - 1;
		while (this.contadorTiempo <= limiteTiempo) {
			List<Event> eventActuales = eventos.get(contadorTiempo);
			if (eventActuales != null) {
				for (Event e : eventActuales)
					e.execute(SimObjects);
			}
			for (Road r : SimObjects.getRoads())
				r.avanza();
			for (Junction j : SimObjects.getJunctions())
				j.avanza();
			this.contadorTiempo++;
			report().store(o);
		}
	}


	public enum EventType {
		REGISTERED, RESET, NEWEVENT, ADVANCED, ERROR;
	}

	// clase interna en el simulador
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

		public List<Event> getEvenQueue() {
			return eventos.get(contadorTiempo);
		}
		
		public int getCurrentTime() {
			return contadorTiempo;
		}
	}
}
