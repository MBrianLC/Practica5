package es.ucm.fdi.extra.graphlayout;

import javax.swing.*;

import es.ucm.fdi.model.SimulatedObjects.Junction;
import es.ucm.fdi.model.SimulatedObjects.Road;
import es.ucm.fdi.model.SimulatedObjects.Vehicle;
import es.ucm.fdi.model.Simulator.Listener;
import es.ucm.fdi.model.Simulator.RoadMap;
import es.ucm.fdi.model.Simulator.TrafficSimulator.UpdateEvent;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class GraphLayout extends JFrame implements Listener {
	
	public GraphComponent _graphComp;
    
	public GraphLayout() {
		super("Dialog Example");
		initGUI(null);
	}

	private void initGUI(RoadMap roadMap) {

		JPanel mainPanel = new JPanel(new BorderLayout() );
		
		_graphComp = new GraphComponent();
		mainPanel.add(_graphComp, BorderLayout.CENTER);
		
		if (roadMap != null) generateGraph(roadMap);
		
		this.setContentPane(mainPanel);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);

	}

	protected void generateGraph(RoadMap roadMap) {

		Graph g = new Graph();
		Map<Junction, Node> js = new HashMap<>();
		for (Junction j : roadMap.getJunctions()) {
			Node n = new Node(j.getID());
			js.put(j, n); // <-- para convertir Junction a Node en aristas
			g.addNode(n);
		}
		for (Road r : roadMap.getRoads()) {
			Edge e = new Edge(r.getID(), js.get(r.getIni()), js.get(r.getFin()), r.getLong());
			for (int i = 0; i < r.getLong(); ++i) {
				if (r.getVehicles().containsKey(i)){
					for(Vehicle v: r.getVehicles().get(i)){
						Dot d = new Dot(v.getID(), v.getPos());
						e.addDot(d);
					}
				}
			}
			g.addEdge(e);
		}
		
		_graphComp.setGraph(g);

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new GraphLayout();
			}
		});
	}

	@Override
	public void update(UpdateEvent ue, String error) {
		initGUI(ue.getRoadMap());
	}
}