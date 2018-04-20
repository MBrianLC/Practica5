package es.ucm.fdi.view;

import java.awt.BorderLayout;
import java.io.File;
import java.io.OutputStream;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

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
	private JMenu fileMenu;
	private JMenu simulatorMenu;
	private JMenu reportsMenu;
	private JToolBar toolBar;
	private JFileChooser fc;
	private File currentFile;
	private JButton loadButton;
	private JButton saveButton;
	private JButton clearEventsButton;
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
	private JTextArea eventsEditor; // editor de eventos
	private JTable eventsTable; // cola de eventos
	private JTextArea reportsArea; // zona de informes
	private JTable vehiclesTable; // tabla de vehiculos
	private JTable roadsTable; // tabla de carreteras
	private JTable junctionsTable; // tabla de cruces
	
	public MainWindowSim(TrafficSimulator tsim, String inFileName, Controller contr){
		super("Traffic Simulator");
		this.contr = contr;
		currentFile = inFileName != null ? new File(inFileName) : null;
		//reportsOutputStream = new JTextAreaOutputStream(reportsArea,null);
		//contr.setOutputStream(reportsOutputStream); // ver sección 8
		initGUI();
		tsim.addSimulatorListener(this);
	}
	
	public void initGUI(){
		mainPanel = new JPanel(new BorderLayout());
		this.setContentPane(mainPanel);
		contentPanel1 = new JPanel();
		contentPanel1.setLayout(new BoxLayout(contentPanel1, BoxLayout.Y_AXIS));
		contentPanel2 = new JPanel();
		contentPanel2.setLayout(new BoxLayout(contentPanel2,BoxLayout.X_AXIS));
		
		//addMenuBar(); // barra de menus
		//addToolBar(); // barra de herramientas
		contentPanel1 = new TextComponentSim("Events", true);
		//addEventsView(); // cola de eventos
		contentPanel2 = new TextComponentSim("Reports", false);
		//addVehiclesTable(); // tabla de vehiculos
		//addRoadsTable(); // tabla de carreteras
		//addJunctionsTable(); // tabla de cruces
		//addMap(); // mapa de carreteras
		//addStatusBar(); // barra de estado
		
		JSplitPane bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, contentPanel1, contentPanel2);
		mainPanel.add(bottomSplit);
		
		this.setContentPane(mainPanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500, 500);
		this.setVisible(true);
		bottomSplit.setDividerLocation(.5);
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
