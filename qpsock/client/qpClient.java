import java.io.*;
import java.net.*;
import java.util.*;


public class qpClient {


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


    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
             System.out.println("Usage: java qpClient <qpServerHostname> <dict>");
             return;
        }

            // get a datagram socket
        DatagramSocket socket = new DatagramSocket();

            // send request
        byte[] sendbuf = new byte[256], recvbuf = new byte[256];
        InetAddress address = InetAddress.getByName(args[0]);
        DatagramPacket packet = new DatagramPacket(sendbuf, sendbuf.length, address, 4020);
        socket.send(packet);
    
            // get response
        packet = new DatagramPacket(recvbuf, recvbuf.length);
        socket.receive(packet);

	    // main work loop
	String msg = new String(packet.getData());
	String alg = msg.substring(0,msg.indexOf(' ')).trim();
	String target = msg.substring(msg.indexOf(' '),msg.lastIndexOf(' ')).trim();
        String receivedRule = msg.substring(msg.lastIndexOf(' '), msg.length()).trim();
	String result = null;
	qpAlgorithm qpalg = (qpAlgorithm) createObject("qpnativecrypt");
	Task t = new Task(target, qpalg);
	
	System.out.println("Processing new search by rule on target ["+alg+":"+target+"]");
        System.out.println("Rule to use: [" + receivedRule+"]");
	Search search = new Search(args[1], t);
	while(receivedRule != null){
		result = search.byRule(receivedRule);
		if(result != null){
			System.out.println("***** SUCCESS *****");
			//SUCCESS, send message back telling it
			result = "KEY:"+result;
			sendbuf = result.getBytes();
		        packet = new DatagramPacket(sendbuf, sendbuf.length, address, 4020);
		        socket.send(packet);
			break;
		}
		else{//unsuccessfull, send next request
			System.out.println("***** NOT FOUND ***** process next rule");
			receivedRule = "RULE:"+receivedRule;
			sendbuf = receivedRule.getBytes();
		}
	        packet = new DatagramPacket(sendbuf, sendbuf.length, address, 4020);
	        socket.send(packet);
		// receive next rule
		recvbuf = new byte[256];
	        packet = new DatagramPacket(recvbuf, recvbuf.length);
	        socket.receive(packet);
		msg = new String(packet.getData());
		System.out.println("packet -> "+msg);
		alg = msg.substring(0,msg.indexOf(' ')).trim();
		target = msg.substring(msg.indexOf(' '),msg.lastIndexOf(' ')).trim();
	        receivedRule = msg.substring(msg.lastIndexOf(' '), msg.length()).trim();
	}
    
        socket.close();
    }
}
