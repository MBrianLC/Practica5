package es.ucm.fdi.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.model.Events.Event;

public class EventList implements Describable{
	
	private int time, actual;
	private List<Event> eventos;

	public EventList(int index, int actual, List<Event> events) {
		time = index;
		eventos = events;
	}

	public Map<String, String> describe() {
		Map<String, String> out = new HashMap<>();
		for (int i = 0; i < eventos.size(); ++i) {
			out.put("#", String.valueOf(actual + i));
			out.put("Time", String.valueOf(time));
			out.put("Type", eventos.get(i).getType());
		}
		return out;
	}

}
