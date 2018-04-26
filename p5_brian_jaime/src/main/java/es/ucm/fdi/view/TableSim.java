package es.ucm.fdi.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import es.ucm.fdi.model.events.Event;
import es.ucm.fdi.model.simulator.Listener;
import es.ucm.fdi.model.simulator.RoadMap;
import es.ucm.fdi.model.simulator.TrafficSimulator.UpdateEvent;

public class TableSim implements Listener{
	
	private String[] fieldNamesQ = {"#", "Time", "Type"};
	private String[] fieldNamesV = {"ID", "Road", "Location", "Speed", "Km", "Faulty Units", "Itinerary"};
	private String[] fieldNamesR = {"ID", "Source", "Target", "Length", "Max Speed", "Vehicles"};
	private String[] fieldNamesJ = {"ID", "Green", "Red"};

	private ListOfMapsTableModel eTableMaps;
	private ListOfMapsTableModel vTableMaps;
	private ListOfMapsTableModel rTableMaps;
	private ListOfMapsTableModel jTableMaps;
	
	private JTable eventsQueue; // tabla de eventos
	private JTable vehiclesTable; // tabla de vehículos
	private JTable roadsTable; // tabla de carreteras
	private JTable junctionsTable; // tabla de cruces
	
	private JPanel eventsPanel;
	private JPanel vehiclesPanel;
	private JPanel roadsPanel;
	private JPanel junctionsPanel;
	
	private RoadMap map;
	private List<EventIndex> events;
	
	public TableSim(RoadMap map, List<EventIndex> events) {
		this.map = map;
		this.events = events;
		addEventsQueue(); // cola de eventos
		addVehiclesTable(); // tabla de vehiculos
		addRoadsTable(); // tabla de carreteras
		addJunctionsTable(); // tabla de cruces
	}
	
	public JPanel getEventPanel() {
		return eventsPanel;
	}
	
	public JPanel getVehiclesPanel() {
		return vehiclesPanel;
	}
	
	public JPanel getRoadsPanel() {
		return roadsPanel;
	}
	
	public JPanel getJunctionsPanel() {
		return junctionsPanel;
	}
	
	@SuppressWarnings("serial")
	private class ListOfMapsTableModel extends AbstractTableModel{

		private List<Describable> elements;
		private String[] fieldNames;
		
		public ListOfMapsTableModel(List<Describable> objectList, String[] names) {
			elements = objectList;
			fieldNames = names;
		}
		
		@Override // fieldNames es un String[] con nombrs de col.
		public String getColumnName(int columnIndex) {
			return fieldNames[columnIndex];
		}
		@Override // elements contiene la lista de elementos
		public int getRowCount() {
			return elements.size();
		}
		@Override
		public int getColumnCount() {
			return fieldNames.length;
		}
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return elements.get(rowIndex).describe().get(fieldNames[columnIndex]);
		}
		
		public void update(List<Describable> objectList) {
			elements = objectList;
		}
	}
	
	private void addEventsQueue() {
		eventsPanel = new JPanel(new BorderLayout());
		List<Describable> objectList = new ArrayList<Describable>(events);
		eTableMaps = new ListOfMapsTableModel(objectList, fieldNamesQ); 
		eventsQueue = new JTable(eTableMaps); 
		JScrollPane sp = new JScrollPane(eventsQueue);
		sp.setPreferredSize(new Dimension(500, 500));
		eventsPanel.setBorder(BorderFactory.createTitledBorder("Events Queue"));
		eventsPanel.add(sp);
	}
	
	private void addVehiclesTable() {
		vehiclesPanel = new JPanel(new BorderLayout());
		List<Describable> objectList = new ArrayList<Describable>(map.getVehicles());
		vTableMaps = new ListOfMapsTableModel(objectList, fieldNamesV); 
		vehiclesTable = new JTable(vTableMaps); 
		JScrollPane sp = new JScrollPane(vehiclesTable);
		sp.setPreferredSize(new Dimension(500, 500));
		vehiclesPanel.setBorder(BorderFactory.createTitledBorder("Vehicles"));
		vehiclesPanel.add(sp);	
	}
	
	private void addRoadsTable() {
		roadsPanel = new JPanel(new BorderLayout());
		List<Describable> objectList = new ArrayList<Describable>(map.getRoads());
		rTableMaps = new ListOfMapsTableModel(objectList, fieldNamesR);
		roadsTable = new JTable(rTableMaps);
		JScrollPane sp = new JScrollPane(roadsTable);
		sp.setPreferredSize(new Dimension(500, 500));
		roadsPanel.setBorder(BorderFactory.createTitledBorder("Roads"));
		roadsPanel.add(sp);
	}
	
	private void addJunctionsTable() {
		junctionsPanel = new JPanel(new BorderLayout());
		List<Describable> objectList = new ArrayList<Describable>(map.getJunctions());
		jTableMaps = new ListOfMapsTableModel(objectList, fieldNamesJ); 
		junctionsTable = new JTable(jTableMaps); 
		JScrollPane sp = new JScrollPane(junctionsTable);
		sp.setPreferredSize(new Dimension(500, 500));
		junctionsPanel.setBorder(BorderFactory.createTitledBorder("Junctions"));
		junctionsPanel.add(sp);
	}
	
	private List<EventIndex> getEvents(List<Event> eventos){
		List<EventIndex> events = new ArrayList<>();
		for (int i = 0; i < eventos.size(); ++i) {
			events.add(new EventIndex(i, eventos.get(i)));
		}
		return events;
	}
	
	private void updateTable(ListOfMapsTableModel tableMaps, List<Describable> elements) {
		tableMaps.update(new ArrayList<Describable>(elements));
		tableMaps.fireTableDataChanged();
	}
	
	public void update(UpdateEvent ue, String error) {
		switch (ue.getEvent()) {
			case ADVANCED:{
				updateTable(eTableMaps, new ArrayList<Describable>(getEvents(ue.getEventQueue())));
				updateTable(vTableMaps, new ArrayList<Describable>(ue.getRoadMap().getVehicles()));
				updateTable(rTableMaps, new ArrayList<Describable>(ue.getRoadMap().getRoads()));
				updateTable(jTableMaps, new ArrayList<Describable>(ue.getRoadMap().getJunctions()));
				break;
			}	
			case NEWEVENT:{
				updateTable(eTableMaps, new ArrayList<Describable>(getEvents(ue.getEventQueue())));
				break;
			}
			case RESET:{
				updateTable(eTableMaps, new ArrayList<Describable>());
				updateTable(vTableMaps, new ArrayList<Describable>());
				updateTable(rTableMaps, new ArrayList<Describable>());
				updateTable(jTableMaps, new ArrayList<Describable>());
				break;
			}
		default:
			break;
		}
	}
}
