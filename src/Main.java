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
		            }
		        });
		  

	}

}
