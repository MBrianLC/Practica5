package es.ucm.fdi.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class MainWindowSim extends JFrame {
	
	public MainWindowSim(){
		super("Traffic Simulator");
		initGUI();
	}
	
	public void initGUI(){
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		JSplitPane bottomSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new TextComponentSim("Events", true), new TextComponentSim("Reports", false));
		mainPanel.add(bottomSplit);
		
		this.setContentPane(mainPanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(500, 500);
		this.setVisible(true);
		bottomSplit.setDividerLocation(.5);
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainWindowSim();
			}
		});
	}
}
