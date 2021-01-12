package onlineExaminationSystem;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.*;

// Student: John Turpin
// ID: 29539123

// Server class for Online Examination System (oes)
public class oesServer {

	//UI Elements
	static JFrame frame;
	static JLabel passwordLabel;
	static JTextField passwordTextField;
	static JButton setPasswordButton;
	
	static JLabel numStudentsLabel;
	static JTextField numStudentsTextField;
	static JButton setNumStudentsButton;
	
	static JLabel idLabel;
	static JTextField idTextField;
	static JButton addIdButton;
	
	static JLabel studentListStatusLabel;
	
	static JLabel numQuestionsLabel;
	static JTextField numQuestionsTextField;
	static JButton setNumQuestionsButton;
	
	//Question Elements
	static JLabel questionLabel, option1Label, option2Label, option3Label, option4Label, answerLabel;
	static JTextField questionTextField, option1TextField, option2TextField, option3TextField, option4TextField;
	static JComboBox<String> answerComboBox;
	static JButton addQuestionButton;
	static JLabel questionStatusLabel;
	
	//start server button
	static JButton startServerButton;
	
	static JLabel connectionStatusLabel;
	
	///// non UI elements
	static String password;
	static int numStudents;
	static ArrayList<String> StudentsIDs = new ArrayList<String>();
	static int numQuestions;
	static ArrayList<Question> ExamQuestions = new ArrayList<Question>();
	static ArrayList<String> loggedInList = new ArrayList<String>(); //keeps track of IDs of students that are connected to avoid same ID connected more than once
	
	//server stuff
	public static ArrayList<clientThread> Clients = new ArrayList<clientThread>();
	static int clientCount = 0;
	static ServerSocket welcomeSocket;
	
	public static void main(String[] args) {
		
		initializeFrame();
		
		setPasswordButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					
					if(!passwordTextField.getText().equals("")) {
						
						//set password
						password = passwordTextField.getText();
						
						//make UI Updates
						passwordLabel.setVisible(false);
						passwordTextField.setVisible(false);
						setPasswordButton.setVisible(false);
						
						numStudentsLabel.setVisible(true);
						numStudentsTextField.setVisible(true);
						setNumStudentsButton.setVisible(true);
						
						
					}
					else {
						//throw exception and make popup appear if password is field is left empty
						throw new Exception("Password field cannot be left empty");
					}
				} catch (Exception e1) {
					
					String msg[] = e1.toString().split(":");
					exceptionFrame(msg[1]);
					e1.printStackTrace();
				}
				
			}
		});
		
		setNumStudentsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					// throw if field is non-integer value or less than 1
					if(!(Math.floor(Double.parseDouble(numStudentsTextField.getText())) != Integer.parseInt(numStudentsTextField.getText()) ||
					   Integer.parseInt(numStudentsTextField.getText()) < 1)) {
						
						numStudents = Integer.parseInt(numStudentsTextField.getText());
						
						//update UI
						numStudentsLabel.setEnabled(false);
						numStudentsTextField.setEditable(false);
						setNumStudentsButton.setEnabled(false);
						
						idLabel.setVisible(true);
						idTextField.setVisible(true);
						addIdButton.setVisible(true);
						
						studentListStatusLabel.setText("0 Students added to the exam, " + numStudents + " remaining");
						studentListStatusLabel.setVisible(true);
						
						
					}
					else {
						
						throw new NumberFormatException("Value must be a non-zero positive integer");
					}
				} catch (NumberFormatException e1) {
					
					String msg[] = e1.toString().split(":");
					exceptionFrame(msg[1]);
					e1.printStackTrace();
				}
				
			}
		});
		
		addIdButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					// check if field is empty
					if(!idTextField.getText().equals("")) {
						if(StudentsIDs.size() < numStudents) {
							
							//add student ID to list
							StudentsIDs.add(idTextField.getText());
							idTextField.setText("");
							
							int size = StudentsIDs.size();
							int difference = numStudents - size;
							studentListStatusLabel.setText(size + " Students added to the exam, " + difference + " remaining");
							
							if(StudentsIDs.size() == numStudents) {
								//update UI Elements
								numStudentsLabel.setVisible(false);
								numStudentsTextField.setVisible(false);
								setNumStudentsButton.setVisible(false);
								
								idLabel.setVisible(false);
								idTextField.setVisible(false);
								addIdButton.setVisible(false);
								
								studentListStatusLabel.setVisible(false);
								
								//add new UI elements
								numQuestionsLabel.setVisible(true);
								numQuestionsTextField.setVisible(true);
								setNumQuestionsButton.setVisible(true);
							}
						
						}
						
					}
					else {
						throw new Exception("Field cannot be left empty");
					}
				} catch (Exception e1) {
					
					String msg[] = e1.toString().split(":");
					exceptionFrame(msg[1]);
					e1.printStackTrace();
				}
				
			}
		});
		
		setNumQuestionsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					if(!numQuestionsTextField.getText().equals("")) {
						//set the number of questions
						numQuestions = Integer.parseInt(numQuestionsTextField.getText());
						
						//update UI
						numQuestionsLabel.setEnabled(false);
						numQuestionsTextField.setEditable(false);
						setNumQuestionsButton.setEnabled(false);
						
						questionLabel.setVisible(true);
						option1Label.setVisible(true);
						option2Label.setVisible(true);
						option3Label.setVisible(true);
						option4Label.setVisible(true);
						answerLabel.setVisible(true);
						questionTextField.setVisible(true);
						option1TextField.setVisible(true);
						option2TextField.setVisible(true); 
						option3TextField.setVisible(true);
						option4TextField.setVisible(true);
						answerComboBox.setVisible(true);
						addQuestionButton.setVisible(true);
						
						questionStatusLabel.setVisible(true);
						questionStatusLabel.setText("No questions added to the exam, " + numQuestions + " remaining");
					}
					else {
						throw new NumberFormatException("Enter positive integer Value");
					}
				} catch (NumberFormatException e1) {
					
					String msg[] = e1.toString().split(":");
					exceptionFrame(msg[1]);
					e1.printStackTrace();
				}
				
			}
		});
		
		addQuestionButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(ExamQuestions.size() < numQuestions) {
					
					try {
						if(!(questionTextField.getText().equals("") || option1TextField.getText().equals("") || option2TextField.getText().equals("") ||
						   option3TextField.getText().equals("") || option4TextField.getText().equals(""))) {
						
							//add new question to exam bank
							ExamQuestions.add(new Question(questionTextField.getText(), option1TextField.getText(), option2TextField.getText(),
												option3TextField.getText(), option4TextField.getText(), answerComboBox.getSelectedItem().toString()));
							
							//clear UI fields
							questionTextField.setText("");
							option1TextField.setText("");
							option2TextField.setText("");
							option3TextField.setText("");
							option4TextField.setText("");
							answerComboBox.setSelectedIndex(3);
							
							// update status label
							questionStatusLabel.setText("Question " + ExamQuestions.size() + " added to the exam, " + (numQuestions-ExamQuestions.size()) + " remaining");
							
							if(ExamQuestions.size() == numQuestions) {
							
								// set all UI invisibile
								numQuestionsLabel.setVisible(false);
								numQuestionsTextField.setVisible(false);
								setNumQuestionsButton.setVisible(false);
								
								questionLabel.setVisible(false);
								option1Label.setVisible(false);
								option2Label.setVisible(false);
								option3Label.setVisible(false);
								option4Label.setVisible(false);
								answerLabel.setVisible(false);
								questionTextField.setVisible(false);
								option1TextField.setVisible(false);
								option2TextField.setVisible(false); 
								option3TextField.setVisible(false);
								option4TextField.setVisible(false);
								answerComboBox.setVisible(false);
								addQuestionButton.setVisible(false);
								
								questionStatusLabel.setVisible(false);
								
								//make "start server" button appear
								startServerButton.setVisible(true);
								
							}
							
						}
						else {
							throw new Exception("Fields cannot be left empty");
						}
					} catch (Exception e1) {
						
						String msg[] = e1.toString().split(":");
						exceptionFrame(msg[1]);
						e1.printStackTrace();
					}
						
				}
				
			}
		});
		
		// start the server
		startServerButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//update UI
				startServerButton.setVisible(false);
				connectionStatusLabel.setVisible(true);
				
				
				try {
					welcomeSocket = new ServerSocket(6789);
				} catch (IOException e1) {
				
					e1.printStackTrace();
				}
				
				//thread to always listen for new connections from clients
		 	   	new Thread (new Runnable(){ @Override
		 	   		public void run() {

		 	   		Socket connectionSocket;
			 	   	DataOutputStream outToClient;
			
			 	   	while (!welcomeSocket.isClosed()) {

			 	   		try {
	
			 	   			//when a new client connect, accept this connection and assign it to a new connection socket
			 	   			connectionSocket = welcomeSocket.accept();
	
			 	   			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			 	   			String clientSentence = inFromClient.readLine();
			 	   			
			 	   			// credentials[1] = password entered, credentials[2] = ID number
			 	   			String credentials[] = clientSentence.split(",");
			 	   			
			 	   			//check that id entered by student does not match ID of already logged in in student
			 	   			if(loggedInList.size() != 0) {
			 	   				for(int i = 0; i < loggedInList.size(); i++) {
			 	   					if(credentials[2].equals(loggedInList.get(i))) {
			 	   					//tell student wrong ID entered
						 	   			outToClient = new DataOutputStream(connectionSocket.getOutputStream());
										outToClient.writeBytes("-IDAlreadyExists\n");
			 	   					}
			 	   				}
			 	   			}
			 	   			// server cannot accept more students than specified
			 	   			if(clientCount == numStudents) {
			 	   				outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			 	   				outToClient.writeBytes("-ServerFull\n");
			 	   			}
			 	   			// if password entered by student matches, then check to see if their user id matches any expected by server
			 	   			else if(password.equals(credentials[1])){
			 	   				for(int i = 0; i < numStudents; i++) {
			 	   					if(StudentsIDs.get(i).equals(credentials[2])) {
			 	   						
			 	   						//create a new output stream and send the message "You are connected" to the client
						 	   			outToClient = new DataOutputStream(connectionSocket.getOutputStream());
										outToClient.writeBytes("-ServerActive," + ExamQuestions + "\n");

										ObjectOutputStream examToClient = new ObjectOutputStream(connectionSocket.getOutputStream());
										examToClient.writeObject(ExamQuestions);
										
										clientCount++;
										
										//add ID to list of students that have logged in
										loggedInList.add(credentials[2]);
										
							 	   		//add the new client to the client's array
						 	   			Clients.add(new clientThread(clientCount, connectionSocket, Clients, ExamQuestions));
						 	   			//start the new client's thread
						 	   			Clients.get(Clients.size() - 1).start();
						 	   			break;
			 	   					}
			 	   					else if(i == numStudents-1){ //student id entered by student does not match any on server
			 	   						//tell student wrong ID entered
						 	   			outToClient = new DataOutputStream(connectionSocket.getOutputStream());
										outToClient.writeBytes("-BadID\n");
			 	   					}
			 	   				}
			 	   			}		 	   			
			 	   			else { //incorrect password entered by student
			 	   				outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			 	   				outToClient.writeBytes("-BadPassword\n");
			 	   			}
							
	
			 	   		}
			 	   		catch (Exception ex) {
			 	   			ex.printStackTrace();
			 	   		}
			                  
			 	   	} 	 
		 	   	
		 	   	}}).start();
				
				//thread to always get the count of connected clients and update the label and send to clients
		 	 	new Thread (new Runnable(){ @Override
		 		   		public void run() {
		 	 		  
	 		  			while (true) {
	 		  				
	 		  				if (Clients.size() > 0) //if there are one or more clients print their number
	 		 	   			{
	 		 	   				if (Clients.size() == 1)
	 		 	   					connectionStatusLabel.setText("1 Student Connected");
	 		 	   				else
	 		 	   					connectionStatusLabel.setText(Clients.size() + " Students Connected");
	 		 	   				 	   				
	 		 	   				connectionStatusLabel.setForeground(Color.blue);
	 		 	   			}
	 		 	   			else { //if there are no clients connected, print "No Clients Connected"
	 		 	   				
	 		 	   				connectionStatusLabel.setText("No Students Connected");
	 			   				connectionStatusLabel.setForeground(Color.red);
	 		 	   				
	 		 	   			}
	 		  			
	 		  				try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								
								e.printStackTrace();
							}
	 		  				
	 		  			}
	 		  			
		 	 	}}).start();
			}
		});

	}

	public static void initializeFrame() {
		
		frame = new JFrame ("Examination Server");
		frame.setLayout(null);
		frame.setBounds(100, 100, 420, 380);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		passwordLabel = new JLabel("Server Password");
		passwordLabel.setBounds(20, 20, 100, 20);
		frame.getContentPane().add(passwordLabel);
		
		passwordTextField = new JTextField();
		passwordTextField.setBounds(140, 20, 100, 20);
		frame.getContentPane().add(passwordTextField);
		
		setPasswordButton = new JButton("Set");
		setPasswordButton.setBounds(260, 20, 80,20);
		frame.getContentPane().add(setPasswordButton);
		
		// after set click y = 20
		numStudentsLabel = new JLabel("Number of students");
		numStudentsLabel.setBounds(20, 20, 120, 20);
		frame.getContentPane().add(numStudentsLabel);
		
		numStudentsTextField = new JTextField();
		numStudentsTextField.setBounds(160, 20, 100, 20);
		frame.getContentPane().add(numStudentsTextField);
		
		setNumStudentsButton = new JButton("Set");
		setNumStudentsButton.setBounds(280, 20, 80, 20);
		frame.getContentPane().add(setNumStudentsButton);
		
		//after 2nd set click y = 60
		idLabel = new JLabel("Student ID:");
		idLabel.setBounds(20, 60, 100, 20);
		frame.getContentPane().add(idLabel);
		
		idTextField = new JTextField();
		idTextField.setBounds(140, 60, 100, 20);
		frame.getContentPane().add(idTextField);
		
		addIdButton = new JButton("Add Student ID");
		addIdButton.setBounds(260, 60, 120, 20);
		frame.getContentPane().add(addIdButton);
		
		// y = 100
		studentListStatusLabel = new JLabel();
		studentListStatusLabel.setBounds(20, 100, 300, 20);
		frame.getContentPane().add(studentListStatusLabel);
		
		//after entering all student IDs y = 20
		numQuestionsLabel = new JLabel("Number of Questions:");
		numQuestionsLabel.setBounds(20, 20, 140, 20);
		frame.getContentPane().add(numQuestionsLabel);
		
		numQuestionsTextField = new JTextField();
		numQuestionsTextField.setBounds(180, 20, 100, 20);
		frame.getContentPane().add(numQuestionsTextField);
		
		setNumQuestionsButton = new JButton("Set");
		setNumQuestionsButton.setBounds(300, 20, 100, 20);
		frame.getContentPane().add(setNumQuestionsButton);
		
		//UI elements for question form
		questionLabel = new JLabel("Question:");
		questionLabel.setBounds(20, 60, 80, 20);
		frame.getContentPane().add(questionLabel);
		
		option1Label = new JLabel("Option 1:");
		option1Label.setBounds(20, 100, 80, 20);
		frame.getContentPane().add(option1Label);
		
		option2Label = new JLabel("Option 2:");
		option2Label.setBounds(20, 140, 80, 20);
		frame.getContentPane().add(option2Label);
		
		option3Label = new JLabel("Option 3:");
		option3Label.setBounds(20, 180, 80, 20);
		frame.getContentPane().add(option3Label);
		
		option4Label = new JLabel("Option 4:");
		option4Label.setBounds(20, 220, 80, 20);
		frame.getContentPane().add(option4Label);
		
		answerLabel = new JLabel("Answer: ");
		answerLabel.setBounds(20, 260, 80, 20);
		frame.getContentPane().add(answerLabel);
		
		questionTextField = new JTextField();
		questionTextField.setBounds(120, 60, 240, 20);
		frame.getContentPane().add(questionTextField);
		
		option1TextField = new JTextField();
		option1TextField.setBounds(120, 100, 240, 20);
		frame.getContentPane().add(option1TextField);
		
		option2TextField = new JTextField();
		option2TextField.setBounds(120, 140, 240, 20);
		frame.getContentPane().add(option2TextField);
		
		option3TextField = new JTextField();
		option3TextField.setBounds(120, 180, 240, 20);
		frame.getContentPane().add(option3TextField);
		
		option4TextField = new JTextField();
		option4TextField.setBounds(120, 220, 240, 20);
		frame.getContentPane().add(option4TextField);
		
		//Combo Box
		String[] choices = {"1", "2", "3", "4"};
		
		answerComboBox = new JComboBox<String>(choices);
		answerComboBox.setSelectedIndex(3);
		//answerComboBox.addActionListener(this);
		answerComboBox.setBounds(120, 260, 50, 20);
		frame.getContentPane().add(answerComboBox);
		
		addQuestionButton= new JButton("Add Question");
		addQuestionButton.setBounds(270, 260, 120, 20);
		frame.getContentPane().add(addQuestionButton);
		
		questionStatusLabel = new JLabel();
		questionStatusLabel.setBounds(20, 300, 300, 20);
		frame.getContentPane().add(questionStatusLabel);
		
		//server button
		startServerButton = new JButton("Start Server");
		startServerButton.setBounds(120, 165, 120, 50);
		frame.getContentPane().add(startServerButton);
		
		connectionStatusLabel = new JLabel();
		connectionStatusLabel.setBounds(120, 165, 300, 20);
		frame.getContentPane().add(connectionStatusLabel);
		
		//// set default visibilities
		frame.setVisible(true);
		passwordLabel.setVisible(true);
		passwordTextField.setVisible(true);
		setPasswordButton.setVisible(true);
		
		numStudentsLabel.setVisible(false);
		numStudentsTextField.setVisible(false);
		setNumStudentsButton.setVisible(false);
		
		idLabel.setVisible(false);
		idTextField.setVisible(false);
		addIdButton.setVisible(false);
		studentListStatusLabel.setVisible(false);
		
		numQuestionsLabel.setVisible(false);
		numQuestionsTextField.setVisible(false);
		setNumQuestionsButton.setVisible(false);
		
		
		//question form
		questionLabel.setVisible(false);
		option1Label.setVisible(false);
		option2Label.setVisible(false);
		option3Label.setVisible(false);
		option4Label.setVisible(false);
		answerLabel.setVisible(false);
		
		questionTextField.setVisible(false);
		option1TextField.setVisible(false);
		option2TextField.setVisible(false); 
		option3TextField.setVisible(false);
		option4TextField.setVisible(false);
		
		answerComboBox.setVisible(false);
		addQuestionButton.setVisible(false);
		questionStatusLabel.setVisible(false);
		
		//start server
		startServerButton.setVisible(false);
		
		connectionStatusLabel.setVisible(false);
		
	}
	
	//create popup if user made a mistake entering values
	static void exceptionFrame(String msg) {
		
		JFrame emptyPasswordFrame = new JFrame("Error");
		emptyPasswordFrame.setLayout(null);
		emptyPasswordFrame.setBounds(110, 120, 360, 100);
		
		JLabel errorMsg = new JLabel(msg);
		errorMsg.setBounds(20, 20, 320, 20);
		emptyPasswordFrame.getContentPane().add(errorMsg);
		
		emptyPasswordFrame.setVisible(true);
		errorMsg.setVisible(true);
	}
	
}
