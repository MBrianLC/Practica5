package es.ucm.fdi.model.Simulator;

import es.ucm.fdi.model.Simulator.TrafficSimulator.UpdateEvent;

public interface Listener {
	
	void update(UpdateEvent ue, String error);
	
}