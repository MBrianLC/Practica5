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
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.Action;
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
import es.ucm.fdi.control.SimulatorAction;
import es.ucm.fdi.extra.dialog.ReportWindow;
import es.ucm.fdi.extra.graphlayout.RoadMapGraph;
import es.ucm.fdi.extra.texteditor.TextEditor;
import es.ucm.fdi.ini.Ini;
import es.ucm.fdi.model.exceptions.SimulatorException;
import es.ucm.fdi.model.simulator.Listener;
import es.ucm.fdi.model.simulator.RoadMap;
import es.ucm.fdi.model.simulator.TrafficSimulator;
import es.ucm.fdi.model.simulator.TrafficSimulator.UpdateEvent;

@SuppressWarnings("serial")
public class MainWindowSim extends JFrame implements Listener {
	private Controller contr;
	private TrafficSimulator tsim;
	private RoadMap map;
	private List<EventIndex> events;
	private int time;
	private OutputStream reportsOutputStream;
	
	private TableSim tableSim;
	private RoadMapGraph rmGraph;
	private TextEditor textEditor;
	private ReportWindow reports;
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
	private JSpinner stepsSpinner;
	private JTextField timeViewer;
	private JTextArea eventsEditor; // editor de eventos
	private JTextArea reportsArea; // zona de informes
	
	Action exit = new SimulatorAction(Command.Quit, "exit.png",
			"Exit",
			KeyEvent.VK_E, "control shift E", () -> System.exit(0));
	
	Action load = new SimulatorAction(Command.Load, "open.png",
			"Load a file",
			KeyEvent.VK_L, "control shift L", () -> loadFile());
	
	Action save = new SimulatorAction(Command.Save, "save.png",
			"Save a file",
			KeyEvent.VK_S, "control shift S", () -> saveFile());
	
	Action clear = new SimulatorAction(Command.Clear, "clear.png",
			"Clear events",
			KeyEvent.VK_C, "control shift C", () -> eventsEditor.setText(""));
	
	Action checkIn = new SimulatorAction(Command.CheckIn, "events.png",
			"Insert an event",
			KeyEvent.VK_C, "control shift I", () -> checkInEvent());
	
	Action run = new SimulatorAction(Command.Run, "play.png",
			"Run simulation",
			KeyEvent.VK_R, "control shift P", () -> runSim());	
	
	Action saveReport = new SimulatorAction(Command.SaveReport, "save_report.png",
			"Save reports",
			KeyEvent.VK_S, "control shift R", () -> saveReport());	
	
	Action genReport = new SimulatorAction(Command.GenReport, "report.png",
			"Generate reports",
			KeyEvent.VK_R, "control shift F", () -> genReport());
	
	Action clearReport = new SimulatorAction(Command.ClearReport, "delete_report.png",
			"Clear reports",
			KeyEvent.VK_C, "control shift M", () -> reportsArea.setText(""));
	
	Action reset = new SimulatorAction(Command.Reset, "reset.png",
			"Reset simulation",
			KeyEvent.VK_R, "control shift N", () -> resetSim());
	
	Action output = new SimulatorAction(Command.Output, null,
			"Redirect simulation's output to text area",
			KeyEvent.VK_R, "control shift O", () -> redirectOutput());
	
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
		fileMenu.add(load);
		fileMenu.add(save);
		fileMenu.addSeparator();
		fileMenu.add(saveReport);
		
		fileMenu.addSeparator();
		
		fileMenu.add(exit);
		
		simulatorMenu = new JMenu("Simulator");
		menuBar.add(simulatorMenu);
		simulatorMenu.add(run);
		simulatorMenu.add(reset);
		simulatorMenu.add(output);
		
		reportsMenu = new JMenu("Reports");
		menuBar.add(reportsMenu);
		reportsMenu.add(genReport);
		reportsMenu.add(clearReport);
		
		this.setJMenuBar(menuBar);
	}
	
	private void addToolBar() {   
		toolBar = new JToolBar();    
		mainPanel.add(toolBar, BorderLayout.PAGE_START);  
		
		toolBar.add(load);
		toolBar.add(save);
		toolBar.add(checkIn);
		toolBar.add(run);
		toolBar.add(reset);
		
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
		
		toolBar.add(genReport);
		toolBar.add(clearReport);
		toolBar.add(saveReport);
		
		toolBar.addSeparator(); 
		
		toolBar.add(exit);
		
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
	
	private void checkInEvent() {
		try {
			contr.setIni(new Ini(new ByteArrayInputStream(eventsEditor.getText().getBytes())));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		reports = new ReportWindow(map, time);
		if (reports.getReport() != null) {
			reportsArea.setText(reports.getReport());
		}
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
	
	private String newVehicle() {
		StringBuilder sb = new StringBuilder("\n[new_vehicle]\n");
		sb.append("time = \n");
		sb.append("id = \n");
		sb.append("max_speed = \n");
		sb.append("itinerary = \n");
		return sb.toString();
	}
	
	private String newRoad() {
		StringBuilder sb = new StringBuilder("\n[new_road]\n");
		sb.append("time = \n");
		sb.append("id = \n");
		sb.append("src = \n");
		sb.append("dest = \n");
		sb.append("max_speed = \n");
		sb.append("length = \n");
		return sb.toString();
	}
	
	private String newJunction() {
		StringBuilder sb = new StringBuilder("\n[new_junction]\n");
		sb.append("time = \n");
		sb.append("id = \n");
		return sb.toString();
	}
	
	private void addEditor(JTextArea textArea) {
		// create the events pop-up menu
		JPopupMenu _editorPopupMenu = new JPopupMenu();
		
		JMenu subMenu = new JMenu("Add Template");
		
		JMenuItem templateRROption = new JMenuItem("New RR Junction");
		templateRROption.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder(newJunction());
				sb.append("type = rr\n");
				sb.append("max_time_slice = \n");
				sb.append("min_time_slice = \n");
				textArea.append(sb.toString());
			}
		}); 
		subMenu.add(templateRROption);
		JMenuItem templateMCOption = new JMenuItem("New MC Junction");
		templateMCOption.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				textArea.append(newJunction() + "type = mc\n");
			}
		});
		subMenu.add(templateMCOption);
		JMenuItem templateJOption = new JMenuItem("New Junction");
		templateJOption.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				textArea.append(newJunction());
			}
		});
		subMenu.add(templateJOption);
		JMenuItem templateDirtOption = new JMenuItem("New Dirt Road");
		templateDirtOption.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				textArea.append(newRoad() + "type = dirt\n");
			}
		});
		subMenu.add(templateDirtOption);
		JMenuItem templateLanesOption = new JMenuItem("New Lanes Road");
		templateLanesOption.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder(newRoad());
				sb.append("type = lanes\n");
				sb.append("lanes = \n");
				textArea.append(sb.toString());
			}
		});
		subMenu.add(templateLanesOption);
		JMenuItem templateROption = new JMenuItem("New Road");
		templateROption.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				textArea.append(newRoad());
			}
		});
		subMenu.add(templateROption);
		JMenuItem templateBikeOption = new JMenuItem("New Bike");
		templateBikeOption.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				textArea.append(newVehicle() + "type = bike\n");
			}
		});
		subMenu.add(templateBikeOption);
		JMenuItem templateCarOption = new JMenuItem("New Car");
		templateCarOption.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder(newVehicle());
				sb.append("type = car\n");
				sb.append("resistance = \n");
				sb.append("fault_probability = \n");
				sb.append("max_fault_duration = \n");
				sb.append("seed = \n");
				textArea.append(sb.toString());
			}
		});
		subMenu.add(templateCarOption);
		JMenuItem templateVOption = new JMenuItem("New Vehicle");
		templateVOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				textArea.append(newVehicle());
			}
		});
		subMenu.add(templateVOption);
		JMenuItem templateFaultyOption = new JMenuItem("Make Vehicle Faulty");
		templateFaultyOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				StringBuilder sb = new StringBuilder("\n[make_vehicle_faulty]\n");
				sb.append("time = \n");
				sb.append("vehicles = \n");
				sb.append("duration = \n");
				textArea.append(sb.toString());
			}
		});
		subMenu.add(templateFaultyOption);
		
		_editorPopupMenu.add(subMenu);
		_editorPopupMenu.addSeparator();
		_editorPopupMenu.add(load);
		_editorPopupMenu.add(save);
		_editorPopupMenu.add(clear);

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
