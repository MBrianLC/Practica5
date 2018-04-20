package es.ucm.fdi.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import es.ucm.fdi.model.Simulator.Listener;
import es.ucm.fdi.model.Simulator.TrafficSimulator.EventType;
import es.ucm.fdi.model.Simulator.TrafficSimulator.UpdateEvent;

@SuppressWarnings("serial")
public class TextComponentSim extends JPanel implements ActionListener,Listener {

	private final String LOAD = "load";
	private final String SAVE = "save";
	private final String CLEAR = "clear";
	private final String QUIT = "quit";

	private JFileChooser fc;
	private JTextArea textArea;

	public TextComponentSim(String title, Boolean editable) {
		super(new BorderLayout());
		this.setBorder(BorderFactory.createTitledBorder(title));
		initGUI(editable);
	}

	private void initGUI(Boolean editable) {

		// text area
		textArea = new JTextArea("");
		textArea.setEditable(editable);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		JScrollPane area = new JScrollPane(textArea);
		area.setPreferredSize(new Dimension(500, 500));
		this.add(area);
		
		addEditor();

		// tool bar
		this.add(createJTolBar(), BorderLayout.PAGE_START);

		// we create the file chooser only once
		fc = new JFileChooser();

	}

	public JToolBar createJTolBar() {
		JToolBar toolBar = new JToolBar();

		JButton load = new JButton();
		load.setActionCommand(LOAD);
		load.setToolTipText("Load a file");
		load.addActionListener(this);
		load.setIcon(new ImageIcon(loadImage("resources/icons/open.png")));
		toolBar.add(load);

		JButton save = new JButton();
		save.setActionCommand(SAVE);
		save.setToolTipText("Save a file");
		save.addActionListener(this);
		save.setIcon(new ImageIcon(loadImage("resources/icons/save.png")));
		toolBar.add(save);

		JButton clear = new JButton();
		clear.setActionCommand(CLEAR);
		clear.setToolTipText("Clear Text");
		clear.addActionListener(this);
		clear.setIcon(new ImageIcon(loadImage("resources/icons/clear.png")));
		toolBar.add(clear);

		return toolBar;
	}
	
	private void addEditor() {
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

	public void actionPerformed(ActionEvent e) {
		if (LOAD.equals(e.getActionCommand()))
			loadFile();
		else if (SAVE.equals(e.getActionCommand()))
			saveFile();
		else if (CLEAR.equals(e.getActionCommand()))
			textArea.setText("");
		else if (QUIT.equals(e.getActionCommand()))
			System.exit(0);
	}

	private void saveFile() {
		int returnVal = fc.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			writeFile(file, textArea.getText());
		}
	}

	private void loadFile() {
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			String s = readFile(file);
			textArea.setText(s);
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

	private static Image loadImage(String path) {
		return Toolkit.getDefaultToolkit().createImage(path);
	}

	public void update(UpdateEvent ue, String error) {
		if (ue.getEvent().equals(EventType.ADVANCED)){
			// work in progress
		}
	}
}
