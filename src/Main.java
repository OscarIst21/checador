import java.awt.EventQueue;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		        EventQueue.invokeLater(new Runnable() {
		            public void run() {
		                try {
		                    Vista frame = new Vista();
		                    frame.setVisible(true);
		                } catch (Exception e) {
		                    e.printStackTrace();
		                }

		                //Periodo, ID, Fecha, entrada1,salida1,entrada2,salida2,
		                //ID,NombreCOmpleto, empleado puesto,  tipo de jornada,
		                //ID, cct no, diaN, horaEntradaR, horaSalidaR, horarioMixto, 
		            }
		        });
		  

	}

}
