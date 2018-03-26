package es.ucm.fdi.Control;

import java.io.IOException;
import java.io.OutputStream;

import es.ucm.fdi.Exceptions.SimulatorException;
import es.ucm.fdi.Events.EventBuilder;
import es.ucm.fdi.Events.NewJunctionEventBuilder;
import es.ucm.fdi.Events.NewRoadEventBuilder;
import es.ucm.fdi.Events.NewVehicleEventBuilder;
import es.ucm.fdi.Events.VehicleFaultyEventBuilder;
import es.ucm.fdi.Simulator.TrafficSimulator;
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
	}
	
	/** 
	 * Método que lee las secciones de eventos, les asigna el builder correspondiente a cada una, y ejecuta la simulación.
	 * @param sim : La simulación de tráfico
	 * @throws IOException 
	 * @throws SimulatorException 
	*/
	public void execute(TrafficSimulator sim) throws IOException, SimulatorException {

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
