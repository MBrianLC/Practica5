package es.ucm.fdi.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import es.ucm.fdi.control.Controller;
import es.ucm.fdi.extra.graphlayout.GraphLayout;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.model.Exceptions.SimulatorException;
import es.ucm.fdi.model.SimulatedObjects.SimObject;
import es.ucm.fdi.model.Simulator.Listener;
import es.ucm.fdi.model.Simulator.RoadMap;
import es.ucm.fdi.model.Simulator.TrafficSimulator;
import es.ucm.fdi.model.Simulator.TrafficSimulator.UpdateEvent;

@SuppressWarnings("serial")
public class MainWindowSim extends JFrame implements ActionListener, Listener {
	private Controller contr;
	private RoadMap map;
	private List<EventIndex> events;
	private int time;
	private OutputStream reportsOutputStream;
	
	private final String LOAD = "load";
	private final String SAVE = "save";
	private final String SAVE_REPORT = "saveReport";
	private final String GEN_REPORT = "genReport";
	private final String CLEAR_REPORT = "clearReport";
	private final String CHECK_IN = "checkIn";
	private final String RUN = "run";
	private final String STOP = "stop";
	private final String RESET = "reset";
	private final String CLEAR = "clear";
	private final String QUIT = "quit";
	
	private JPanel mainPanel;
	private JPanel stateBar;
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
	private JLabel statusBarText;
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
	private JTable eventsQueue; // tabla de eventos
	private JTable vehiclesTable; // tabla de vehículos
	private JTable roadsTable; // tabla de carreteras
	private JTable junctionsTable; // tabla de cruces
	private JTextArea reportsArea; // zona de informes
	
	public MainWindowSim() {
	
	//public MainWindowSim(TrafficSimulator tsim, String inFileName, Controller contr){
		super("Traffic Simulator");
		/*this.contr = contr;
		map = tsim.getMap();
		int cont = 0;
		for (int i = 0; i < contr.getTime(); ++i) {
			List<Event> eventsMismoTiempo = tsim.getEvents().get(i);
			if (eventsMismoTiempo != null) {
				for (Event e : eventsMismoTiempo) {
					events.add(new EventIndex(cont, e));
					++cont;
				}
			}
		}
		currentFile = inFileName != null ? new File(inFileName) : null;
		*/
		//reportsOutputStream = new JTextAreaOutputStream(reportsArea,null);
		//contr.setOutputStream(reportsOutputStream); // ver sección 8
		initGUI();
		//tsim.addSimulatorListener(this);
	}
	
	public void initGUI(){
		fc = new JFileChooser();
		mainPanel = new JPanel(new BorderLayout());
		this.setContentPane(mainPanel);
		
		addMenuBar(); // barra de menus
		addToolBar(); // barra de herramientas
		//addEventsEditor();
		//addEventsView(); // cola de eventos
		//addReportsArea(); // zona de informes
		contentPanel1 = new TextComponentSim("Events", false);
		contentPanel2 = new TextComponentSim("Table", false);
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
		JMenuItem run = new JMenuItem("Run");
		run.setActionCommand(RUN);
		run.setToolTipText("Run simulation");
		run.addActionListener(this);
		simulatorMenu.add(run);
		JMenuItem reset = new JMenuItem("Reset");
		reset.setActionCommand(RESET);
		reset.setToolTipText("Reset simulation");
		reset.addActionListener(this);
		simulatorMenu.add(reset);
		
		reportsMenu = new JMenu("Reports");
		menuBar.add(reportsMenu);
		JMenuItem genRep = new JMenuItem("Generate");
		genRep.setActionCommand(GEN_REPORT);
		genRep.setToolTipText("Generate reports");
		genRep.addActionListener(this);
		reportsMenu.add(genRep);
		JMenuItem clearRep = new JMenuItem("Clear");
		clearRep.setActionCommand(CLEAR_REPORT);
		clearRep.setToolTipText("Clear reports");
		clearRep.addActionListener(this);
		reportsMenu.add(clearRep);
		
		this.setJMenuBar(menuBar);
	}
	
	private void addToolBar() {   
		toolBar = new JToolBar();    
		mainPanel.add(toolBar, BorderLayout.PAGE_START);  
		
		loadButton = new JButton();
		loadButton.setActionCommand(LOAD);
		loadButton.setToolTipText("Load a file");
		loadButton.addActionListener(this);	
	    loadButton.setIcon(new ImageIcon("src/main/resources/icons/open.png"));
		toolBar.add(loadButton);
		
		saveButton = new JButton();
		saveButton.setActionCommand(SAVE);
		saveButton.setToolTipText("Save a file");
		saveButton.addActionListener(this);
		saveButton.setIcon(new ImageIcon("src/main/resources/icons/save.png"));
		toolBar.add(saveButton);
		
		clearEventsButton = new JButton(); 
		clearEventsButton.setActionCommand(CLEAR);
		clearEventsButton.setToolTipText("Clear events");
		clearEventsButton.addActionListener(this);
		clearEventsButton.setIcon(new ImageIcon("src/main/resources/icons/clear.png"));
		toolBar.add(clearEventsButton);
		
		checkInEventsButton = new JButton();
		checkInEventsButton.setActionCommand(CHECK_IN);
		checkInEventsButton.setToolTipText("Insert an event");
		checkInEventsButton.addActionListener(this);
		checkInEventsButton.setIcon(new ImageIcon("src/main/resources/icons/events.png"));
		toolBar.add(checkInEventsButton);
		
		runButton = new JButton(); 
		runButton.setActionCommand(RUN);
		runButton.setToolTipText("Run simulation");
		runButton.addActionListener(this);
		runButton.setIcon(new ImageIcon("src/main/resources/icons/play.png"));
		toolBar.add(runButton);
		
		resetButton = new JButton();
		resetButton.setActionCommand(RESET);
		resetButton.setToolTipText("Reset simulation");
		resetButton.addActionListener(this);
		resetButton.setIcon(new ImageIcon("src/main/resources/icons/reset.png"));
		toolBar.add(resetButton);
		
		toolBar.add(new JLabel(" Steps: "));   
		stepsSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 1000, 1));
		stepsSpinner.addChangeListener(new ChangeListener() {
    		public void stateChanged(ChangeEvent e) {
    	    	contr.setTime((int)stepsSpinner.getValue());
			}
		});
		toolBar.add(stepsSpinner);
		
		toolBar.add(new JLabel(" Time: "));
		timeViewer = new JTextField("0", 5);  
		toolBar.add(timeViewer);
		
		toolBar.addSeparator(); 
		
		genReportsButton = new JButton();
		genReportsButton.setActionCommand(GEN_REPORT);
		genReportsButton.setToolTipText("Generate reports");
		genReportsButton.addActionListener(this);
		genReportsButton.setIcon(new ImageIcon("src/main/resources/icons/report.png"));
		toolBar.add(genReportsButton);
		
		clearReportsButton = new JButton();
		clearReportsButton.setActionCommand(CLEAR_REPORT);
		clearReportsButton.setToolTipText("Clear reports");
		clearReportsButton.addActionListener(this);
		clearReportsButton.setIcon(new ImageIcon("src/main/resources/icons/delete_report.png"));
		toolBar.add(clearReportsButton);
		
		saveReportsButton = new JButton();
		saveReportsButton.setActionCommand(SAVE_REPORT);
		saveReportsButton.setToolTipText("Save reports");
		saveReportsButton.addActionListener(this);
		saveReportsButton.setIcon(new ImageIcon("src/main/resources/icons/save_report.png"));
		toolBar.add(saveReportsButton);
		
		toolBar.addSeparator(); 
		
		quitButton = new JButton();
		quitButton.setActionCommand(QUIT);
		quitButton.setToolTipText("Exit");
		quitButton.addActionListener(this);
		quitButton.setIcon(new ImageIcon("src/main/resources/icons/exit.png"));
		toolBar.add(quitButton);
		
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainWindowSim();
			}
		});
	}
	
	public void actionPerformed(ActionEvent e) {
		if (LOAD.equals(e.getActionCommand()))
			loadFile();
		else if (SAVE.equals(e.getActionCommand()))
			saveFile();
		else if (SAVE_REPORT.equals(e.getActionCommand()))
			saveReport();
		else if (CLEAR_REPORT.equals(e.getActionCommand()))
			reportsArea.setText("");
		else if (RUN.equals(e.getActionCommand())){
			runButton.setActionCommand(STOP);
			runButton.setToolTipText("Stop simulation");
			runButton.addActionListener(this);
			runButton.setIcon(new ImageIcon("src/main/resources/icons/stop.png"));
			runSim();
		}
		else if (RESET.equals(e.getActionCommand())){
			resetSim();
		}
		else if (GEN_REPORT.equals(e.getActionCommand())) {
			genReport();
		}
		else if (STOP.equals(e.getActionCommand())) {
			runButton.setActionCommand(RUN);
			runButton.setToolTipText("Run simulation");
			runButton.addActionListener(this);
			runButton.setIcon(new ImageIcon("src/main/resources/icons/play.png"));
			try {
				stop();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		else if (CLEAR.equals(e.getActionCommand()))
			eventsEditor.setText("");
		else if (CHECK_IN.equals(e.getActionCommand()))
			try {
				checkInEvent();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		else if (QUIT.equals(e.getActionCommand()))
			System.exit(0);
	}

	private void saveFile() {
		stateBar.remove(statusBarText);
		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			try {
				writeFile(file, eventsEditor.getText());
			} catch (IOException e) {
				statusBarText.setText("ERROR: The file has not been saved");    
			}
		}
		statusBarText.setText("The file have been saved!");    
		stateBar.add(statusBarText);
	}
	
	private void saveReport() {
		int returnVal = fc.showSaveDialog(null);
		stateBar.remove(statusBarText);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			try {
				writeFile(file, reportsArea.getText());
			} catch (IOException e) {
				statusBarText.setText("ERROR: The reports have not been saved");    
			}
		}
		statusBarText.setText("All reports have been saved!");    
		stateBar.add(statusBarText);
	}

	private void loadFile() {
		int returnVal = fc.showOpenDialog(null);
		stateBar.remove(statusBarText);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String s;
			try {
				s = readFile(file);
				eventsEditor.setText(s);
				statusBarText.setText("Events have been loaded to the simulator!"); 
				currentFile = file;
				contentPanel2.setBorder(BorderFactory.createTitledBorder("Events: " + currentFile.getName()));
				
			} catch (IOException e) {
				e.printStackTrace();
				statusBarText.setText("ERROR: File not found");    
			}
		}
		stateBar.add(statusBarText);
	}
	
	private void runSim(){
		try {
			InputStream in = new FileInputStream(eventsEditor.getText());
			contr.setIni(new Ini(in));
			contr.execute(new TrafficSimulator());
		} 
		catch (IOException ex) {
			ex.printStackTrace();
		} 
		catch (SimulatorException ex) {
			ex.printStackTrace();
		}
	}
	
	private void checkInEvent() throws IOException {
		contr.loadEvents(new ByteArrayInputStream(eventsEditor.getText().getBytes())); 
	}
	
	private void stop() throws InterruptedException {
		contr.wait(); 
	}
	
	private void resetSim(){
		time = 0;
		reportsArea.setText("");
		// resetear el resto de componentes también (excepto eventsEditor)
	}
	
	private void genReport(){
		Map<String, String> m = new LinkedHashMap<>();
		String reporte = "";
		for (SimObject sim : map.getSimObjects()) {
			sim.report(time, m);
			reporte += "[" + m.get("") + "]\n";
			for (String key : m.keySet()){
				reporte += key + " = " + m.get(key) + '\n';
			}
			reporte += '\n';
		}
		reportsArea.setText(reporte);
	}
	
	public static String readFile(File file) throws IOException {
		String s = "";
		try {
			Scanner sc = new Scanner(file);
			s = sc.useDelimiter("\\A").next();
			sc.close();
		} catch (FileNotFoundException e) {
			throw new IOException();
		}
		return s;
	}

	public static void writeFile(File file, String content) throws IOException {
		try {
			PrintWriter pw = new PrintWriter(file);
			pw.print(content);
			pw.close();
		} catch (IOException e) {
			throw new IOException();
		}
	}

	private class ListOfMapsTableModel extends AbstractTableModel {

		private List<Object> elements;
		private String[] fieldNames;
		
		public ListOfMapsTableModel(List<Object> objectList, String[] names) {
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
			return ((Describable) elements.get(rowIndex)).describe().get(fieldNames[columnIndex]);
		}
	}
	
	private void addEventsEditor(){
		contentPanel2 = new JPanel(new BorderLayout());
		contentPanel2.setBorder(BorderFactory.createTitledBorder("Events: " + currentFile.getName()));
		eventsEditor = new JTextArea("");
		eventsEditor.setEditable(true);
		eventsEditor.setLineWrap(true);
		eventsEditor.setWrapStyleWord(true);
		JScrollPane area = new JScrollPane(eventsEditor);
		area.setPreferredSize(new Dimension(500, 500));
		this.add(area);
		
		addEditor(eventsEditor);
	}
	
	private void addEventsQueue() {
		JPanel tablePanel = new JPanel(new BorderLayout());
		List<Object> objectList = new ArrayList<Object>(events);
		String[] fieldNames = {"#", "Time", "Type"};
		ListOfMapsTableModel tableMaps = new ListOfMapsTableModel(objectList, fieldNames); 
		eventsQueue = new JTable(tableMaps); 
		tablePanel.add(eventsQueue);
		tablePanel.add(new JScrollPane());
		mainPanel.add(tablePanel);
	}
	
	private void addVehiclesTable() {
		JPanel tablePanel = new JPanel(new BorderLayout());
		List<Object> objectList = new ArrayList<Object>(map.getVehicles());
		String[] fieldNames = {"ID", "Road", "Location", "Speed", "Km", "Faulty Units", "Itinerary"};
		ListOfMapsTableModel tableMaps = new ListOfMapsTableModel(objectList, fieldNames); 
		vehiclesTable = new JTable(tableMaps); 
		tablePanel.add(vehiclesTable);
		tablePanel.add(new JScrollPane());
		mainPanel.add(tablePanel);	
	}
	
	private void addRoadsTable() {
		JPanel tablePanel = new JPanel(new BorderLayout());
		List<Object> objectList = new ArrayList<Object>(map.getRoads());
		String[] fieldNames = {"ID", "Source", "Target", "Length", "Max Speed", "Vehicles"};
		ListOfMapsTableModel tableMaps = new ListOfMapsTableModel(objectList, fieldNames);
		roadsTable = new JTable(tableMaps); 
		tablePanel.add(roadsTable);
		tablePanel.add(new JScrollPane());
		mainPanel.add(tablePanel);
	}
	
	private void addJunctionsTable() {
		JPanel tablePanel = new JPanel(new BorderLayout());
		List<Object> objectList = new ArrayList<Object>(map.getJunctions());
		String[] fieldNames = {"ID", "Green", "Red"};
		ListOfMapsTableModel tableMaps = new ListOfMapsTableModel(objectList, fieldNames); 
		junctionsTable = new JTable(tableMaps); 
		tablePanel.add(junctionsTable);
		tablePanel.add(new JScrollPane());
		mainPanel.add(tablePanel);
	}
	
	private void addStatusBar() {  
		stateBar = new JPanel(new BorderLayout());
		statusBarText = new JLabel("Welcome to the simulator!");    
		stateBar.add(statusBarText);
		mainPanel.add(stateBar);
	}
	
	private void addMap() {  
		mainPanel.add(new GraphLayout());
	}
	
	private void addEditor(JTextArea textArea) {
		// create the events pop-up menu
		JPopupMenu _editorPopupMenu = new JPopupMenu();
		
		JMenuItem clearOption = new JMenuItem("Clear");
		clearOption.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
			}
		});

		JMenuItem exitOption = new JMenuItem("Exit");
		exitOption.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		JMenu subMenu = new JMenu("Insert");

		String[] greetings = { "Hola!", "Hello!", "Ciao!" };
		for (String s : greetings) {
			JMenuItem menuItem = new JMenuItem(s);
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					textArea.insert(s, textArea.getCaretPosition());
				}
			});
			subMenu.add(menuItem);
		}

		
		_editorPopupMenu.add(subMenu);
		_editorPopupMenu.addSeparator();
		_editorPopupMenu.add(clearOption);
		_editorPopupMenu.add(exitOption);

		// connect the popup menu to the text area _editor
		textArea.addMouseListener(new MouseListener() {

			@Override
			public void mousePressed(MouseEvent e) {
				showPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				showPopup(e);
			}

			private void showPopup(MouseEvent e) {
				if (e.isPopupTrigger() && _editorPopupMenu.isEnabled()) {
					_editorPopupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

	}

	public void update(UpdateEvent ue, String error) {
		switch (ue.getEvent()) {
			case ADVANCED:{
				timeViewer.setText(String.valueOf(ue.getCurrentTime()));
				break;
			}
			case RESET:{
				timeViewer.setText("0");
				break;
			}
			case NEWEVENT:{
				statusBarText.setText("New event inserted");
				break;
			}
			case ERROR:{
				statusBarText.setText(error);
				break;
			}
		default:
			break;
		}
	}
}
