package es.ucm.fdi.Events;

import org.junit.Assert;
import org.junit.Test;

import es.ucm.fdi.ini.IniSection;

/** 
 * La clase NewVehicleEventBuilderTest se encarga de probar que NewVehicleEventBuilder funciona correctamente.
 * @author Jaime Fernández y Brian Leiva
*/

public class NewVehicleEventBuilderTest {

	/**
	 * Método que prueba el método parse de NewVehicleEventBuilder.
	 */
	
	@Test
	public void parseTest(){
		NewVehicleEventBuilder b = new NewVehicleEventBuilder();
		IniSection sv = new IniSection("new_junction");
		IniSection sj = new IniSection("new_vehicle");
		sj.setValue("time", "2");
		sj.setValue("id", "v5");
		sj.setValue("max_speed", "30");
		sj.setValue("itinerary", "j2,j4,j6");
		sj.setValue("resistance", "5");
		sj.setValue("fault_probability", "0.4");
		sj.setValue("max_fault_duration", "8");
		sj.setValue("seed", "500");
		
		Event e = b.parse(sv);
		Assert.assertEquals("Debería ser null, puesto que sv es una inisection de un cruce", null, e);
		
		e = b.parse(sj);
		String[] iti = {"j2","j4","j6"};
		Event f = new NewVehicleEvent(2, "v5", 30, iti);
		Assert.assertEquals("Comprueba si ha parseado bien el tiempo", f.getTime(), e.getTime());
		Assert.assertEquals("Comprueba si ha construido el tipo de evento correcto", f.getClass(), e.getClass());
		
		sj.setValue("type", "bike");
		
		e = b.parse(sj);
		f = new NewBikeEvent(2, "v5", 30, iti);
		Assert.assertEquals("Comprueba si ha parseado bien el tiempo", f.getTime(), e.getTime());
		Assert.assertEquals("Comprueba si ha construido el tipo de evento correcto", f.getClass(), e.getClass());
		
		sj.setValue("type", "car");
		
		e = b.parse(sj);
		f = new NewCarEvent(2, "v5", 30, iti, 5, 0.4, 8, 500);
		Assert.assertEquals("Comprueba si ha parseado bien el tiempo", f.getTime(), e.getTime());
		Assert.assertEquals("Comprueba si ha construido el tipo de evento correcto", f.getClass(), e.getClass());
	}
	
	/**
	 * Método que prueba el método isValidId de NewVehicleEvent.
	 */
	
	@Test
	public void isValidIdTest(){
		NewVehicleEventBuilder b = new NewVehicleEventBuilder();
		String s = "v5";
		Assert.assertTrue("La ID es válida, luego isValidId da true", b.isValidId(s));
		
		s = "j5";
		Assert.assertTrue("La ID no es válida, luego isValidId da false", !b.isValidId(s));
	}
	
	/**
	 * Método que prueba el método type de NewVehicleEvent.
	 */
	
	@Test
	public void typeTest(){
		NewVehicleEventBuilder b = new NewVehicleEventBuilder();
		String s = "new_vehicle";
		Assert.assertEquals("Devuelve el tipo adecuado", b.type(), s);
	}
	
	/**
	 * Método que prueba el método parseInt de NewVehicleEvent.
	 */
	
	@Test
	public void parseIntTest(){
		NewVehicleEventBuilder b = new NewVehicleEventBuilder();
		IniSection sj = new IniSection("new_vehicle");
		sj.setValue("time", "2");
		Assert.assertEquals("Comprueba que parsea bien el entero", b.parseInt(sj, "time"), 2);
	}
	
	/**
	 * Método que prueba el método parseLong de NewVehicleEvent.
	 */
	
	@Test
	public void parseLongTest(){
		NewVehicleEventBuilder b = new NewVehicleEventBuilder();
		IniSection sj = new IniSection("new_vehicle");
		sj.setValue("time", "2");
		Assert.assertEquals("Comprueba que parsea bien el entero", b.parseLong(sj, "time"), 2);
	}
	
	/**
	 * Método que prueba el método parseDouble de NewVehicleEvent.
	 */
	
	@Test
	public void parseDoubleTest(){
		NewVehicleEventBuilder b = new NewVehicleEventBuilder();
		IniSection sj = new IniSection("new_vehicle");
		sj.setValue("time", "2");
		Assert.assertTrue(b.parseDouble(sj, "time") == 2);
	}
	
	/**
	 * Método que prueba el método parseIdList de NewVehicleEvent.
	 */
	
	@Test
	public void parseIdListTest(){
		NewVehicleEventBuilder b = new NewVehicleEventBuilder();
		IniSection sj = new IniSection("new_vehicle");
		sj.setValue("lista", "v1,v2,v3");
		String[] s = {"v1", "v2", "v3"};
		Assert.assertArrayEquals("Comprueba que parsea bien la lista", b.parseIdList(sj, "lista"), s);
	}
	
}