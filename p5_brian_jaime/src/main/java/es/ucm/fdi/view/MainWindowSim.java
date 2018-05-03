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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import es.ucm.fdi.control.Controller;
import es.ucm.fdi.extra.dialog.ReportWindow;
import es.ucm.fdi.extra.graphlayout.RoadMapGraph;
import es.ucm.fdi.extra.texteditor.TextEditor;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.model.exceptions.SimulatorException;
import es.ucm.fdi.model.simobjects.SimObject;
import es.ucm.fdi.model.simulator.Listener;
import es.ucm.fdi.model.simulator.RoadMap;
import es.ucm.fdi.model.simulator.TrafficSimulator;
import es.ucm.fdi.model.simulator.TrafficSimulator.UpdateEvent;

@SuppressWarnings("serial")
public class MainWindowSim extends JFrame implements ActionListener, Listener {
	private Controller contr;
	private TrafficSimulator tsim;
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
	private final String RESET = "reset";
	private final String OUTPUT = "output";
	private final String CLEAR = "clear";
	private final String QUIT = "quit";
	
	private TableSim tableSim;
	private RoadMapGraph rmGraph;
	private TextEditor textEditor;
	private ReportWindow dialog;
	private JPanel mainPanel;
	private JPanel stateBar;
	private JPanel editorPanel;
	private JPanel reportsPanel;
	private JPanel mapPanel;
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
	private JButton resetButton;
	private JSpinner stepsSpinner;
	private JTextField timeViewer;
	private JButton genReportsButton;
	private JButton clearReportsButton;
	private JButton saveReportsButton;
	private JButton quitButton;
	private JTextArea eventsEditor; // editor de eventos
	private JTextArea reportsArea; // zona de informes
	
	public MainWindowSim(TrafficSimulator tsim, String inFileName, Controller contr) {
		super("Traffic Simulator");
		this.contr = contr;
		this.tsim = tsim;
		map = tsim.getMap();
		events = new ArrayList<>();
		for (int i = 0; i < tsim.getEventQueue().size(); ++i) {
			events.add(new EventIndex(i, tsim.getEventQueue().get(i)));
		}
		currentFile = inFileName != null ? new File(inFileName) : null;
		initGUI();
		reportsOutputStream = new JTextAreaOutputStream(reportsArea);
		contr.setOutputStream(reportsOutputStream);
		this.tsim.addSimulatorListener(this);
		this.tsim.addSimulatorListener(tableSim);
		this.tsim.addSimulatorListener(rmGraph);
	}
	
	public void initGUI(){
		fc = new JFileChooser();
		mainPanel = new JPanel(new BorderLayout());
		this.setContentPane(mainPanel);
		
		addMenuBar(); // barra de menus
		addToolBar(); // barra de herramientas
		addEventsEditor();
		
		//textEditor = new TextEditor();
		
		tableSim = new TableSim(map, events);
		
		addReportsArea(); // zona de informes
		
		addMap(); // mapa de carreteras
		
		addStatusBar(); // barra de estado
		
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
		panel1.add(stateBar);
		panel2.add(editorPanel);
		panel2.add(tableSim.getEventPanel());
		panel2.add(reportsPanel);
		panel3.add(panel4);
		panel3.add(mapPanel);
		panel4.add(tableSim.getVehiclesPanel());
		panel4.add(tableSim.getRoadsPanel());
		panel4.add(tableSim.getJunctionsPanel());
		
		mainPanel.add(panel1);
		if (currentFile != null) {
			try {
				String s = readFile(currentFile);
				eventsEditor.setText(s);
				editorPanel.setBorder(BorderFactory.createTitledBorder("Events: " + currentFile.getName()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		this.setContentPane(mainPanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1000, 1000);
		this.setVisible(true);
	}
	
	private class JTextAreaOutputStream extends OutputStream{
		
		private JTextArea textArea;

		public JTextAreaOutputStream(JTextArea textArea) {
			this.textArea = textArea;
		}

		public void write(int b) throws IOException {
			textArea.append(String.valueOf((char)b));
	        textArea.setCaretPosition(textArea.getDocument().getLength());
		}
		
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
		JMenuItem output = new JMenuItem("Redirect Output");
		output.setActionCommand(OUTPUT);
		output.setToolTipText("Redirect simulation's output to text area");
		output.addActionListener(this);
		simulatorMenu.add(output);
		
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
			runSim();
		}
		else if (RESET.equals(e.getActionCommand())){
			resetSim();
		}
		else if (OUTPUT.equals(e.getActionCommand())){
			redirectOutput();	
		}
		else if (GEN_REPORT.equals(e.getActionCommand())) {
			genReport();
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
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String s;
			try {
				s = readFile(file);
				eventsEditor.setText(s);
				statusBarText.setText("Events have been loaded to the simulator!"); 
				currentFile = file;
				editorPanel.setBorder(BorderFactory.createTitledBorder("Events: " + currentFile.getName()));
				
			} catch (IOException e) {
				e.printStackTrace();
				statusBarText.setText("ERROR: File not found");    
			}
		}
		stateBar.add(statusBarText);
	}
	
	private void runSim() {
		try {
			tsim.resetEvents();
			contr.setTime((int)stepsSpinner.getValue());
			contr.execute(tsim);
			tableSim = new TableSim(map, events);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (SimulatorException ex) {
			ex.printStackTrace();
		}
	}
	
	private void checkInEvent() throws IOException {
		contr.setIni(new Ini(new ByteArrayInputStream(eventsEditor.getText().getBytes())));
	}
	
	private void resetSim(){
		time = 0;
		reportsArea.setText("");
		tsim.resetSim();
	}
	
	private void redirectOutput() {
		if (reportsOutputStream != null) {
			reportsOutputStream = null;
		}
		else {
			reportsOutputStream = new JTextAreaOutputStream(reportsArea);
		}
		contr.setOutputStream(reportsOutputStream);
	}
	
	private void genReport(){
		dialog = new ReportWindow(map, time);
		reportsArea.setText(dialog.getReport());
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
	
	private void addEventsEditor(){
		editorPanel = new JPanel(new BorderLayout());
		editorPanel.setBorder(BorderFactory.createTitledBorder("Events: " + currentFile.getName()));
		eventsEditor = new JTextArea("");
		eventsEditor.setEditable(true);
		eventsEditor.setLineWrap(true);
		eventsEditor.setWrapStyleWord(true);
		JScrollPane area = new JScrollPane(eventsEditor);
		area.setPreferredSize(new Dimension(500, 500));
		editorPanel.add(area);
		
		addEditor(eventsEditor);
	}
	
	private void addReportsArea(){
		reportsPanel = new JPanel(new BorderLayout());
		reportsPanel.setBorder(BorderFactory.createTitledBorder("Reports"));
		reportsArea = new JTextArea("");
		reportsArea.setEditable(false);
		reportsArea.setLineWrap(true);
		reportsArea.setWrapStyleWord(true);
		JScrollPane area = new JScrollPane(reportsArea);
		area.setPreferredSize(new Dimension(500, 500));
		reportsPanel.add(area);
	}
	
	private void addStatusBar() {  
		stateBar = new JPanel(new BorderLayout());
		statusBarText = new JLabel("Welcome to the simulator!");    
		stateBar.add(statusBarText);
		mainPanel.add(stateBar);
	}
	
	private void addMap() {  
		mapPanel = new JPanel(new BorderLayout());
		rmGraph = new RoadMapGraph(new RoadMap());
		JScrollPane sp = new JScrollPane(rmGraph._graphComp);
		sp.setPreferredSize(new Dimension(500, 500));
		mapPanel.add(sp);
	}
	
	private void addEditor(JTextArea textArea) {
		// create the events pop-up menu
		JPopupMenu _editorPopupMenu = new JPopupMenu();
		
		JMenuItem loadOption = new JMenuItem("Load");
		loadOption.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				loadFile();
			}
		});
		
		JMenuItem saveOption = new JMenuItem("Save");
		saveOption.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});
		
		JMenuItem clearOption = new JMenuItem("Clear");
		clearOption.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.setText("");
			}
		});

		JMenu subMenu = new JMenu("Add Template");
		
		JMenuItem templateRROption = new JMenuItem("New RR Junction");
		templateRROption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = "\n[new_junction]\n";
				s += "time = \n";
				s += "id = \n";
				s += "type = rr\n";
				s += "max_time_slice = \n";
				s += "min_time_slice = \n";
				textArea.append(s);
			}
		}); 
		subMenu.add(templateRROption);
		JMenuItem templateMCOption = new JMenuItem("New MC Junction");
		templateMCOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = "\n[new_junction]\n";
				s += "time = \n";
				s += "id = \n";
				s += "type = mc\n";
				textArea.append(s);
			}
		});
		subMenu.add(templateMCOption);
		JMenuItem templateJOption = new JMenuItem("New Junction");
		templateJOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = "\n[new_junction]\n";
				s += "time = \n";
				s += "id = \n";
				textArea.append(s);
			}
		});
		subMenu.add(templateJOption);
		JMenuItem templateDirtOption = new JMenuItem("New Dirt Road");
		templateDirtOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = "\n[new_road]\n";
				s += "time = \n";
				s += "id = \n";
				s += "src = \n";
				s += "dest = \n";
				s += "max_speed = \n";
				s += "length = \n";
				s += "type = dirt\n";
				textArea.append(s);
			}
		});
		subMenu.add(templateDirtOption);
		JMenuItem templateLanesOption = new JMenuItem("New Lanes Road");
		templateLanesOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = "\n[new_road]\n";
				s += "time = \n";
				s += "id = \n";
				s += "src = \n";
				s += "dest = \n";
				s += "max_speed = \n";
				s += "length = \n";
				s += "type = lanes\n";
				s += "lanes = \n";
				textArea.append(s);
			}
		});
		subMenu.add(templateLanesOption);
		JMenuItem templateROption = new JMenuItem("New Road");
		templateROption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder();
				sb.append("\n[new_road]\n");
				sb.append("time = \n");
				sb.append("id = \n");
				sb.append("src = \n");
				sb.append("dest = \n");
				sb.append("max_speed = \n");
				sb.append("length = \n");
				textArea.append(sb.toString());
			}
		});
		subMenu.add(templateROption);
		JMenuItem templateBikeOption = new JMenuItem("New Bike");
		templateBikeOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = "\n[new_vehicle]\n";
				s += "time = \n";
				s += "id = \n";
				s += "itinerary = \n";
				s += "max_speed = \n";
				s += "type = bike\n";
				textArea.append(s);
			}
		});
		subMenu.add(templateBikeOption);
		JMenuItem templateCarOption = new JMenuItem("New Car");
		templateCarOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = "\n[new_vehicle]\n";
				s += "time = \n";
				s += "id = \n";
				s += "itinerary = \n";
				s += "max_speed = \n";
				s += "type = car\n";
				s += "resistance = \n";
				s += "fault_probability = \n";
				s += "max_fault_duration = \n";
				s += "seed = \n";
				textArea.append(s);
			}
		});
		subMenu.add(templateCarOption);
		JMenuItem templateVOption = new JMenuItem("New Vehicle");
		templateVOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = "\n[new_vehicle]\n";
				s += "time = \n";
				s += "id = \n";
				s += "max_speed = \n";
				s += "itinerary = \n";
				textArea.append(s);
			}
		});
		subMenu.add(templateVOption);
		JMenuItem templateFaultyOption = new JMenuItem("Make Vehicle Faulty");
		templateFaultyOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = "\n[make_vehicle_faulty]\n";
				s += "time = \n";
				s += "vehicles = \n";
				s += "duration = \n";
				textArea.append(s);
			}
		});
		subMenu.add(templateFaultyOption);
		
		_editorPopupMenu.add(subMenu);
		_editorPopupMenu.addSeparator();
		_editorPopupMenu.add(loadOption);
		_editorPopupMenu.add(saveOption);
		_editorPopupMenu.add(clearOption);

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
				time = ue.getCurrentTime();
				timeViewer.setText(String.valueOf(time));
				map = ue.getRoadMap();
				break;
			}
			case RESET:{
				timeViewer.setText("0");
				map = ue.getRoadMap();
				events = new ArrayList<EventIndex>();
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
