package es.ucm.fdi.view;

import java.util.HashMap;
import java.util.Map;

import es.ucm.fdi.model.Events.Event;

public class EventIndex implements Describable{
	
	private int time, actual;
	private Event evento;

	public EventIndex(int index, Event event) {
		actual = index;
		time = event.getTime();
		evento = event;
	}

	public Map<String, String> describe() {
		Map<String, String> out = new HashMap<>();
		out.put("#", String.valueOf(actual));
		out.put("Time", String.valueOf(time));
		out.put("Type", evento.getType());
		return out;
	}

}
