package es.ucm.fdi.AdvancedObjects;

import java.util.List;
import java.util.Map;
import java.util.Random;

import es.ucm.fdi.SimulatedObjects.Junction;
import es.ucm.fdi.SimulatedObjects.Vehicle;

/** 
 * La clase Car representa un coche del simulador.
 * @author Jaime Fernández y Brian Leiva
*/

public class Car extends Vehicle{
	
	private int resistenciaKm, duracionMaximaAveria, dist;
	private double probabilidadDeAveria;
	private Random numAleatorio;
	
	/** 
	 * Constructor de la clase Car.
	 * @param ident : Identificador
	 * @param vmax : Velocidad máxima
	 * @param it : Itinerario
	 * @param resistance : Resistencia
	 * @param fault_probability : Probabilidad de que se averíe
	 * @param max_fault_duration : Tiempo máximo de avería
	 * @param semilla : Seed aleatoria para decidir si se avería y cuánto durará la avería
	*/
	public Car(String ident, int vmax, List<Junction> it, int resistance, double fault_probability, int max_fault_duration, long semilla) {
		super(ident, vmax, it);
		resistenciaKm = resistance;
		probabilidadDeAveria = fault_probability;
		duracionMaximaAveria = max_fault_duration;
		numAleatorio = new Random(semilla);
		dist = 0;
	}
	
	/**
	 * Informe de Car
	 * @param out : Mapa con los datos de Car
	*/
	protected void fillReportDetails(Map<String, String> out){
		out.put("type", "car");
		super.fillReportDetails(out);
	}
	
	/**
	 * Método que comprueba si se avería y hace avanzar el coche
	*/
	public void avanza(){
		int n = distTotal;
		if (!super.getAveria() && dist > resistenciaKm && numAleatorio.nextDouble() < probabilidadDeAveria) {
			super.setTiempoAveria(numAleatorio.nextInt(duracionMaximaAveria) + 1);
			super.setVelocidadActual(0);
			dist = 0;
		}
		super.avanza();
		dist += distTotal - n;
	}

}
