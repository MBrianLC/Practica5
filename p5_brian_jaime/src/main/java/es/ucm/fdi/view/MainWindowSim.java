package es.ucm.fdi.view;

import java.awt.BorderLayout;
import java.io.OutputStream;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import es.ucm.fdi.control.Controller;
import es.ucm.fdi.model.Events.Event;
import es.ucm.fdi.model.Simulator.Listener;
import es.ucm.fdi.model.Simulator.RoadMap;
import es.ucm.fdi.model.Simulator.TrafficSimulator;
import es.ucm.fdi.model.Simulator.TrafficSimulator.UpdateEvent;

@SuppressWarnings("serial")
public class MainWindowSim extends JFrame implements Listener {
	private Controller contr;
	private RoadMap map;
	private List<Event> events;
	private int time;
	private OutputStream reportsOutputStream;
	
	private JPanel mainPanel;
	private JPanel contentPanel1;
	private JPanel contentPanel2;
	private JPanel contentPanel3;
	private JPanel contentPanel4;
	private JPanel contentPanel5;
	private JPanel contentPanel6;
	private JPanel contentPanel7;
	private JMenu simulatorMenu;
	private JMenu reportsMenu;
	private JToolBar toolBar;
	private JButton checkInEventsButton;
	private JButton runButton;
	private JButton stopButton;
	private JButton resetButton;
	private JSpinner stepsSpinner;
	private JTextField timeViewer;
	private JButton genReportsButton;
	private JButton clearReportsButton;
	private JButton saveReportsButton;
	private JButton quitButton;
	private JTable eventsTable; // cola de eventos
	private JTextArea reportsArea; // zona de informes
	private JTable vehiclesTable; // tabla de vehiculos
	private JTable roadsTable; // tabla de carreteras
	private JTable junctionsTable; // tabla de cruces
	
	//public MainWindowSim(TrafficSimulator tsim, String inFileName, Controller contr)
	
	public MainWindowSim(TrafficSimulator tsim, String inFileName, Controller contr){
		super("Traffic Simulator");
		this.contr = contr;
		//reportsOutputStream = new JTextAreaOutputStream(reportsArea,null);
		//contr.setOutputStream(reportsOutputStream); // ver sección 8
		initGUI();
		tsim.addSimulatorListener(this);
	}
	
	public void initGUI(){
		mainPanel = new JPanel(new BorderLayout());
		this.setContentPane(mainPanel);
		
		//addMenuBar(); // barra de menus
		//addToolBar(); // barra de herramientas
		contentPanel1 = new TextComponentSim("Events", true);
		//addEventsView(); // cola de eventos
		contentPanel2 = new TextComponentSim("Events Queue", false);
		contentPanel3 = new TextComponentSim("Reports", false);
		contentPanel4 = new TextComponentSim("Vehicles", false);
		contentPanel5 = new TextComponentSim("Roads", false);
		contentPanel6 = new TextComponentSim("Junctions", false);
		contentPanel7 = new TextComponentSim("Test", false);
		//addVehiclesTable(); // tabla de vehiculos
		//addRoadsTable(); // tabla de carreteras
		//addJunctionsTable(); // tabla de cruces
		//addMap(); // mapa de carreteras
		//addStatusBar(); // barra de estado
		
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1,BoxLayout.Y_AXIS));
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2,BoxLayout.X_AXIS));
		JPanel panel3 = new JPanel();
		panel3.setLayout(new BoxLayout(panel3,BoxLayout.X_AXIS));
		JPanel panel4 = new JPanel();
		panel4.setLayout(new BoxLayout(panel4,BoxLayout.Y_AXIS));
		
		panel1.add(panel2);
		panel1.add(panel3);
		panel2.add(contentPanel1);
		panel2.add(contentPanel2);
		panel2.add(contentPanel3);
		panel3.add(panel4);
		panel3.add(contentPanel7);
		panel4.add(contentPanel4);
		panel4.add(contentPanel5);
		panel4.add(contentPanel6);
		
		mainPanel.add(panel1);
		
		this.setContentPane(mainPanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000, 1000);
		this.setVisible(true);
	}
	
	/*public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainWindowSim();
			}
		});
	}*/

	public void update(UpdateEvent ue, String error) {
		
	}
}
