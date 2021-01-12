package onlineExaminationSystem;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class clientThread extends Thread{

	//the ClientServiceThread class extends the Thread class and has the following parameters
		public int number; //client name
		public Socket connectionSocket; //client connection socket
		ArrayList<clientThread> Clients; //list of all clients connected to the server
		public String passwordEntered;
		public String idEntered;
		public ArrayList<Question> ExamQuestions = new ArrayList<>();
		public int score;
	
	//constructor function 
		public clientThread(int number, Socket connectionSocket, ArrayList<clientThread> Clients, ArrayList<Question> ExamQuestions) {
			
			this.number = number;
			this.connectionSocket = connectionSocket;
			this.Clients = Clients;		
			this.ExamQuestions = ExamQuestions;
		}
		
		//thread's run function
		public void run() {
			
			try {
				
				//create a buffer reader and connect it to the client's connection socket
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
	    		String clientSentence;
	    		DataOutputStream outToClient;
				
	    		//always read messages from client
	    		while (true) {
	    			
	    			clientSentence = inFromClient.readLine();
	    			
	    			//check the start of the message
	    			if (clientSentence.startsWith("-Remove")) { //Remove Client
	    				
	    				for (int i = 0; i < Clients.size(); i++) {
	    					
	    					if (Clients.get(i).number == number) {
	    						Clients.remove(i);	    					
	    					}
	    					
	    				}
	    			} else if (clientSentence.startsWith("-Verify")) { //Remove Client
	    				
	    				String studentResponse[] = clientSentence.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "").split(",");
	    				
	    				for (int i = 0; i < ExamQuestions.size(); i++) {
	    					if(studentResponse[i+1].equals(ExamQuestions.get(i).answer)) {
	    						score++;
	    					}
	    					
	    				}
	    				
	    				outToClient = new DataOutputStream(connectionSocket.getOutputStream());
	    				outToClient.writeBytes("-Results," + score + "\n");
	    				
	    			}
	    		}
				
				
			} catch(Exception ex) {
				
			}	
		}
	
}
