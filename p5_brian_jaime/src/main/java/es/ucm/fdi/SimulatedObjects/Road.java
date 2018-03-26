package es.ucm.fdi.SimulatedObjects;

import java.util.ArrayDeque;
import java.util.Map;

import es.ucm.fdi.util.MultiTreeMap;

/** 
 * La clase Road representa una carretera del simulador.
 * @author Jaime Fernández y Brian Leiva
*/

public class Road extends SimObject {
	protected int longitud, maxVel;
	protected MultiTreeMap<Integer, Vehicle> vehiculos;
	private boolean semaforo;
	private ArrayDeque<Vehicle> cola;
	private Junction ini, fin;
	
	/** 
	 * Constructor de la clase Road.
	 * @param ident : Identificador
	 * @param l : Longitud de la carretera
	 * @param maxV : Velocidad máxima permitida en la carretera
	 * @param junction_i: Cruce inicial
	 * @param junction_f: Cruce final
	*/
	public Road(String ident, int l, int maxV, Junction junction_i, Junction junction_f){
		super(ident);
		longitud = l;
		maxVel = maxV;
		ini = junction_i;
		fin = junction_f;
		cola = new ArrayDeque<>();
		vehiculos = new MultiTreeMap<>((a, b) -> a - b);
	}
	
	/** 
	 * Método acedente para cola.
	 * @return Cola de vehículos que han llegado al final de la carretera
	*/
	public ArrayDeque<Vehicle> getQueue(){
		return cola;
	}
	
	/** 
	 * Método get para longitud.
	 * @return Longitud de la carretera
	*/
	public int getLong(){
		return longitud;
	}
	
	/** 
	 * Método get para ini.
	 * @return Cruce inicial
	*/
	public Junction getIni(){
		return ini;
	}
	
	/** 
	 * Método get para fin.
	 * @return Cruce final
	*/
	public Junction getFin(){
		return fin;
	}
	
	/** 
	 * Método get para semaforo.
	 * @return Semáforo del cruce final
	*/
	public boolean getSemaforo(){
		return semaforo;
	}
	
	/** 
	 * Método set para semaforo.
	 * @param b: Nuevo estado del semáforo (true: verde, false: rojo)
	*/
	public void setSemaforo (boolean b){
		semaforo = b;
	}
	
	/** 
	 * Añade un vehículo a la carretera.
	 * @param v: Vehículo
	*/
	public void entraVehiculo (Vehicle v){
		vehiculos.putValue(0, v);
	}
	
	/** 
	 * Elimina un vehículo de la carretera.
	 * @param v: Vehículo
	*/
	public void saleVehiculo (Vehicle v){
		vehiculos.removeValue(longitud, v);
	}
	
	/** 
	 * Devuelve la cabecera del informe de Road.
	 * @return Cabecera del informe
	*/
	protected String getReportHeader(){
		return "road_report";
	}
	
	/** 
	 * Informe de Road.
	 * @param out : Mapa para salida de datos
	*/
	protected void fillReportDetails(Map<String, String> out){
		String s = "";
		if (!vehiculos.isEmpty()) {
			for (int i = longitud; i >= 0; --i){
				if (vehiculos.containsKey(i)){
					for(Vehicle v: vehiculos.get(i))
						s += "(" + v.getID() + "," + v.getPos() + "),";
				}
			}
			s = s.substring(0, s.length() - 1);
		}
		out.put("state", s);
	}
	
	/** 
	 * Método avanza para Road.
	*/
	public void avanza (){ // ***
		int velBase = Math.min(maxVel, maxVel / Math.max((int) vehiculos.sizeOfValues(), 1) + 1);
		int factorRed = 1;
		MultiTreeMap<Integer, Vehicle> map = new MultiTreeMap<>((a, b) -> a - b);
		if (vehiculos.containsKey(longitud)){
			for(Vehicle v: vehiculos.get(longitud)){
				map.putValue(longitud, v);
			}
		}
		for (int i = longitud - 1; i >= 0; --i){
			if (vehiculos.containsKey(i)){
				for(Vehicle v: vehiculos.get(i)){
					if (v.getAveria()) {
						factorRed = 2;
						v.setVelocidadActual(0);
					}
					else v.setVelocidadActual(velBase / factorRed);
					v.avanza();
					if (v.getPos() == longitud) {
						v.setVelocidadActual(0);
						cola.add(v);
					}
					map.putValue(v.getPos(), v);
				}
			}
		}
		vehiculos = map;
	}
}
