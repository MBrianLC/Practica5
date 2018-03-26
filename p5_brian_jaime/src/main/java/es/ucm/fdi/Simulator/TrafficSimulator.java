package es.ucm.fdi.Simulator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.Exceptions.SimulatorException;
import es.ucm.fdi.Events.Event;
import es.ucm.fdi.SimulatedObjects.Junction;
import es.ucm.fdi.SimulatedObjects.Road;
import es.ucm.fdi.SimulatedObjects.SimObject;
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

}
