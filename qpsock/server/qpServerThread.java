import java.io.*;
import java.net.*;
import java.util.*;

public class qpServerThread extends Thread {

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean morerules = true;
    protected Task t;

    public qpServerThread() throws IOException {
	this("qpServerThread");
    }

    public qpServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(4020);

        try {
            in = new BufferedReader(new FileReader("rules.qp"));
        } catch (FileNotFoundException e) {
            System.err.println("Could not open rules file.");
        }
    }


   static Object createObject(String className) {
      Object object = null;
      try {
          Class classDefinition = Class.forName(className);
          object = classDefinition.newInstance();
      } catch (InstantiationException e) {
          System.out.println(e);
      } catch (IllegalAccessException e) {
          System.out.println(e);
      } catch (ClassNotFoundException e) {
          System.out.println(e);
      }
      return object;
   }


    public void run() {
	Rules ruleHandler = new Rules("rules.qp");
	qpAlgorithm qpalg = (qpAlgorithm) createObject("qpjcrypt");
	t = new Task("$1$ZlWnj5c7$V0UxAYlqIQwogh8V0x/y5/", qpalg);
	System.out.println("starting search for ["+t.getCurrentTarget()+"]");
	int init = 0;
	String rule;
	double inicio = 0, fim;
        while (morerules) {
            try {
                byte[] buf = new byte[256];

                    // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
		if (init == 0){
			inicio = System.currentTimeMillis();
			init = 1;
		}
		String msg = new String(packet.getData());
		if (msg.startsWith("KEY:")){
			System.out.println("***** CRACKED ***** The key is ["+msg+"]");
			morerules = false;
		}
		else{
                    // figure out response
                	String dString = new String(t.getCurrentTarget());
			rule = ruleHandler.getNextRule();
			if(rule!=null){
	        	        dString = "jcrypt "+dString+' '+rule; //datagrama no formato "algoritmo hash regra"
        	        	buf = dString.getBytes();
				System.out.println("sending rule "+new String(buf)+" dest: "+packet.getAddress());
				// send the response to the client at "address" and "port"
	        	        InetAddress address = packet.getAddress();
        	        	int port = packet.getPort();
	                	packet = new DatagramPacket(buf, buf.length, address, port);
		                socket.send(packet);
			}
			else
				morerules = false;
		}
            } catch (IOException e) {
                e.printStackTrace();
		morerules = false;
            }
        }
	fim = System.currentTimeMillis();
	System.out.println("Tempo decorrido: "+(fim-inicio)+" milisegundos");
        socket.close();
    }

}
