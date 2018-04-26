package es.ucm.fdi.model.simobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.view.Describable;

/** 
 * La clase Junction representa un cruce del simulador.
 * @author Jaime Fernández y Brian Leiva
*/

public class Junction extends SimObject implements Describable{
	protected int k; //***
	protected List<Road> entrantes;
	protected List<Road> salientes;
	protected Map<Junction, Road> mapSaliente;
	
	/** 
	 * Constructor de la clase Junction.
	 * @param ident : Identificador
	*/
	public Junction(String ident){
		super(ident);
		k = -1;
		entrantes = new ArrayList<>();
		salientes = new ArrayList<>();
		mapSaliente = new HashMap<>();
	}
	
	/** 
	 * Introduce un vehiculo en la cola de su carretera.
	 * @param v : Vehículo
	*/
	public void entraVehiculo(Vehicle v){
		for (Road r : entrantes){
			if (r == v.getCarretera()) r.getQueue().add(v);
		}
	}
	
	/** 
	 * Devuelve la carretera en la que se encuentra un vehículo.
	 * @param v : Vehículo
	 * @return Carretera actual del vehículo
	*/
	public Road road(Vehicle v) {
		return mapSaliente.get(v.sigCruce());
	}
	
	/** 
	 * Añade una carretera entrante.
	 * @param r : Carretera
	*/
	public void addEntra(Road r) {
		entrantes.add(r);
		if (entrantes.size() == 1){
			r.setSemaforo(true);
		}
	}
	
	/** 
	 * Añade una carretera saliente.
	 * @param r : Carretera
	*/
	public void addSale(Road r) {
		salientes.add(r);
		mapSaliente.put(r.getFin(), r);
	}
	
	/** 
	 * Devuelve la cabecera del informe de Junction.
	 * @return Cabecera del informe
	*/
	protected String getReportHeader(){
		return "junction_report";
	}
	
	/** 
	 * Informe de Junction.
	 * @param out : Mapa para salida de datos
	*/
	protected void fillReportDetails(Map<String, String> out){
		String s = "";
		if (!entrantes.isEmpty()) {
			for (int i = 0; i < entrantes.size(); ++i){
				s += "(" + entrantes.get(i).getID() + ",";
				if (entrantes.get(i).getSemaforo()){
					s += "green,[";
				} else {
					s += "red,[";
				}
				if (!entrantes.get(i).getQueue().isEmpty()) {
					for (Vehicle v : entrantes.get(i).getQueue()){
						s += v.getID() + ",";
					}
					s = s.substring(0, s.length() - 1);
				}
				s += "]),";
			}
			s = s.substring(0, s.length() - 1);
		}
		out.put("queues", s);
	}
	
	/** 
	 * Tabla de Junction.
	 * @return Mapa para salida de datos
	*/
	public Map<String, String> describe(){
		Map<String, String> out = new HashMap<>();
		String g = "[", r = g;
		out.put("ID", id);
		if (!entrantes.isEmpty()) {
			for (int i = 0; i < entrantes.size(); ++i){
				String aux = "";
				Boolean b = false;
				aux += "(" + entrantes.get(i).getID() + ",";
				if (entrantes.get(i).getSemaforo()) {
					b = true;
					g += "green,[";
				} else {
					aux += "red,[";
				}
				if (!entrantes.get(i).getQueue().isEmpty()) {
					for (Vehicle v : entrantes.get(i).getQueue()){
						aux += v.getID() + ",";
					}
					aux = aux.substring(0, aux.length() - 1);
				}
				aux += "]),";
				if (b){
					g += aux;
				} else {
					r += aux;
				}
			}
			if (g.endsWith(",")){
				g = g.substring(0, g.length() - 1);
			}
			if (r.endsWith(",")){
				r = r.substring(0, r.length() - 1);
			}
		}
		g += "]";
		r += "]";
		out.put("Green", g);
		out.put("Red", r);
		return out;
	}
	
	/** 
	 * Método avanza para Junction.
	*/
	public void avanza(){
		if (k == -1) {
			k = 0;
		} else if (!entrantes.isEmpty()) {
			if (!entrantes.get(k).getQueue().isEmpty()) {
				Vehicle v = entrantes.get(k).getQueue().getFirst();
				if (!v.fin()) {
					Road r = mapSaliente.get(v.sigCruce());
					v.moverASiguienteCarretera(r);
				} else {
					v.moverASiguienteCarretera(null);
				}
				entrantes.get(k).getQueue().pop();
			}
			entrantes.get(k).setSemaforo(false);
			k++;
			if (k == entrantes.size()){
				k = 0;
			}
			entrantes.get(k).setSemaforo(true);
		}
	}
	
}
