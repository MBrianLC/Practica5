package es.ucm.fdi.control;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.model.Exceptions.SimulatorException;
import es.ucm.fdi.model.Events.Event;
import es.ucm.fdi.model.Events.EventBuilder;
import es.ucm.fdi.model.Events.NewJunctionEventBuilder;
import es.ucm.fdi.model.Events.NewRoadEventBuilder;
import es.ucm.fdi.model.Events.NewVehicleEventBuilder;
import es.ucm.fdi.model.Events.VehicleFaultyEventBuilder;
import es.ucm.fdi.model.Simulator.TrafficSimulator;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.ini.IniSection;

/** 
 * La clase Controller controla la simulación.
 * @author Jaime Fernández y Brian Leiva
*/

public class Controller {
	private EventBuilder[] events = {
			new NewVehicleEventBuilder(), new NewRoadEventBuilder(),
			new NewJunctionEventBuilder(), new VehicleFaultyEventBuilder() };
	private Ini ini;
	private OutputStream out;
	private List<Event> eventos;
	private int timeLimit;
	
	/** 
	 * Constructor de la clase Controller.
	 * @param ini : Flujo de entrada (formato ini)
	 * @param out : Flujo de salida
	 * @param timeLimit : Tiempo durante el que se ejecuta la simulación
	*/
	public Controller(Ini ini, OutputStream out, Integer timeLimit) {
		this.ini = ini;
		this.out = out;
		this.timeLimit = timeLimit;
		eventos = new ArrayList<>();
	}
	
	/** 
	 * Método set para ini.
	 * @param ini : Nuevo texto en formato Ini
	*/
	
	public void setIni (Ini ini){
		this.ini = ini;
	}
	
	/** 
	 * Método set para timeLimit.
	 * @param ini : Nuevo límite de tiempo
	*/
	
	public void setTime (int timeLimit){
		this.timeLimit = timeLimit;
	}
	
	/** 
	 * Método get para timeLimit.
	 * @return Límite de tiempo (pasos de simulación)
	*/
	
	public int getTime (){
		return timeLimit;
	}
	
	public void loadEvents(InputStream in) throws IOException {
		for (IniSection n : ini.getSections()) {
			boolean b = false;
			try {
				if (!n.getTag().isEmpty()) {
					for (EventBuilder eBuilder : events) {
						if (n.getTag().equals(eBuilder.type())) {
							eventos.add(eBuilder.parse(n));
							b = true;
						}
					}
				}
				if (!b) throw new IllegalArgumentException("Incorrect tag: " + n.getTag());
			}
			catch(IllegalArgumentException e) {
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}
		ini = new Ini(in);
	}
	
	/** 
	 * Método que lee las secciones de eventos, les asigna el builder correspondiente a cada una, y ejecuta la simulación.
	 * @param sim : La simulación de tráfico
	 * @throws IOException 
	 * @throws SimulatorException 
	 * @throws InterruptedException 
	*/
	
	public void execute(TrafficSimulator sim) throws IOException, SimulatorException, InterruptedException {
		
		for (Event e : eventos) {
			sim.insertaEvento(e);
		}
		for (IniSection n : ini.getSections()) {
			boolean b = false;
			try {
				if (!n.getTag().isEmpty()) {
					for (EventBuilder eBuilder : events) {
						if (n.getTag().equals(eBuilder.type())) {
							sim.insertaEvento(eBuilder.parse(n));
							b = true;
						}
					}
				}
				if (!b) throw new IllegalArgumentException("Incorrect tag: " + n.getTag());
			}
			catch(IllegalArgumentException e) {
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}
		sim.execute(timeLimit, out);
	}
}
