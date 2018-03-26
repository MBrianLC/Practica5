package es.ucm.fdi.AdvancedObjects;

import java.util.Map;

import es.ucm.fdi.SimulatedObjects.Junction;
import es.ucm.fdi.SimulatedObjects.Road;
import es.ucm.fdi.SimulatedObjects.Vehicle;

/** 
 * La clase MostCrowded representa un cruce congestionado del simulador.
 * @author Jaime Fernández y Brian Leiva
*/

public class MostCrowded extends Junction {
	private int intervaloDeTiempo;
	private int unidadesDeTiempoUsadas;
	
	/** 
	 * Constructor de la clase MostCrowded.
	 * @param ident : Identificador
	*/
	public MostCrowded(String ident) {
		super(ident);
		intervaloDeTiempo = 0;
		unidadesDeTiempoUsadas = 0;
		k = -1;
	}
	
	/** 
	 * Método get para intervaloDeTiempo.
	 * @return El intervalo de tiempo actual
	*/
	public int getIntervaloDeTiempo() {
		return intervaloDeTiempo;
	}
	
	/** 
	 * Informe de MostCrowded.
	 * @param out : Mapa para salida de datos
	*/
	protected void fillReportDetails(Map<String, String> out){
		String s = "";
		if (!entrantes.isEmpty()) {
			for (int i = 0; i < entrantes.size(); ++i){
				s += "(" + entrantes.get(i).getID() + ",";
				if (entrantes.get(i).getSemaforo()) s += "green:" + (intervaloDeTiempo - unidadesDeTiempoUsadas) + ",[";
				else s += "red,[";
				if (!entrantes.get(i).getQueue().isEmpty()) {
					for (Vehicle v : entrantes.get(i).getQueue())
						s += v.getID() + ",";
					s = s.substring(0, s.length() - 1);
				}
				s += "]),";
			}
			s = s.substring(0, s.length() - 1);
		}
		out.put("queues", s);
		out.put("type", "mc");
	}
	
	/** 
	 * Método avanza para MostCrowded.
	*/
	public void avanza(){
		if (k == -1 ) {
			k = 0;
			entrantes.get(k).setSemaforo(true);
			intervaloDeTiempo = Math.max((int) entrantes.get(k).getQueue().size() / 2, 1);
			unidadesDeTiempoUsadas = 0;
		}
		else if (!entrantes.isEmpty()) {
			if (!entrantes.get(k).getQueue().isEmpty()) {
				Vehicle v = entrantes.get(k).getQueue().getFirst();
				if (!v.fin()) {
					Road r = mapSaliente.get(v.sigCruce());
					v.moverASiguienteCarretera(r);
				}
				else v.moverASiguienteCarretera(null);
				entrantes.get(k).getQueue().pop();
			}
			++unidadesDeTiempoUsadas;
			if (unidadesDeTiempoUsadas == intervaloDeTiempo) {
				entrantes.get(k).setSemaforo(false);
				int l = k;
				k++;
				if (k == entrantes.size()) k = 0;
				for(int i = l + 1; i < entrantes.size(); i++){
					if (entrantes.get(i).getQueue().size() > entrantes.get(k).getQueue().size()) k = i;
				}
				for(int i = 0; i < l; i++){
					if (entrantes.get(i).getQueue().size() > entrantes.get(k).getQueue().size()) k = i;
				}
				entrantes.get(k).setSemaforo(true);
				intervaloDeTiempo = Math.max((int) entrantes.get(k).getQueue().size() / 2, 1);
				unidadesDeTiempoUsadas = 0;
			}
		}
	}
	
}
