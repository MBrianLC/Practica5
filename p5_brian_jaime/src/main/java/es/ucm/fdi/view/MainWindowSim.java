package es.ucm.fdi.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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
public class MainWindowSim extends JFrame implements ActionListener, Listener {
	private Controller contr;
	private RoadMap map;
	private List<Event> events;
	private int time;
	private OutputStream reportsOutputStream;
	
	private final String LOAD = "load";
	private final String SAVE = "save";
	private final String SAVE_REPORT = "savereport";
	private final String CLEAR = "clear";
	private final String QUIT = "quit";
	
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
	
	//public MainWindowSim(TrafficSimulator tsim, String inFileName, Controller contr)
	
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
		
		addMenuBar(); // barra de menus
		//addToolBar(); // barra de herramientas
		addEventsEditor();
		//addEventsView(); // cola de eventos
		//addReportsArea(); // zona de informes
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
	
	private void addEventsEditor(){
		contentPanel1 = new TextComponentSim("Events", true);
	}
	
	private void addMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		JMenuItem loadEvents = new JMenuItem("Load Events", KeyEvent.VK_L);
		loadEvents.setActionCommand(LOAD);
		loadEvents.setToolTipText("Load a file");
		loadEvents.addActionListener(this);
		fileMenu.add(loadEvents);
		JMenuItem saveEvents = new JMenuItem("Save Events", KeyEvent.VK_S);
		saveEvents.setActionCommand(SAVE);
		saveEvents.setToolTipText("Save a file");
		saveEvents.addActionListener(this);
		fileMenu.add(saveEvents);
		fileMenu.addSeparator();
		JMenuItem saveReport = new JMenuItem("Save Report", KeyEvent.VK_R);
		saveReport.setActionCommand(SAVE_REPORT);
		saveReport.setToolTipText("Save the reports");
		saveReport.addActionListener(this);
		fileMenu.add(saveReport);
		fileMenu.addSeparator();
		JMenuItem exit = new JMenuItem("Exit", KeyEvent.VK_E);
		exit.setActionCommand(QUIT);
		exit.setToolTipText("Exit");
		exit.addActionListener(this);
		fileMenu.add(exit);
		
		simulatorMenu = new JMenu("Simulator");
		menuBar.add(simulatorMenu);
		
		reportsMenu = new JMenu("Reports");
		menuBar.add(reportsMenu);
		
		this.setJMenuBar(menuBar);
	}
	
	/*public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainWindowSim();
			}
		});
	}*/
	
	public void actionPerformed(ActionEvent e) {
		if (LOAD.equals(e.getActionCommand()))
			loadFile();
		else if (SAVE.equals(e.getActionCommand()))
			saveFile();
		else if (CLEAR.equals(e.getActionCommand()))
			eventsEditor.setText("");
		else if (QUIT.equals(e.getActionCommand()))
			System.exit(0);
	}

	private void saveFile() {
		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			writeFile(file, eventsEditor.getText());
		}
	}

	private void loadFile() {
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String s = readFile(file);
			eventsEditor.setText(s);
		}
	}
	
	public static String readFile(File file) {
		String s = "";
		try {
			s = new Scanner(file).useDelimiter("\\A").next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return s;
	}

	public static void writeFile(File file, String content) {
		try {
			PrintWriter pw = new PrintWriter(file);
			pw.print(content);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void update(UpdateEvent ue, String error) {
		
	}
}
