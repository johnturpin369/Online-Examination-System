package onlineExaminationSystem;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.*;

public class oesClient {

	// UI elements
	static JFrame frame;
	static JLabel idLabel, passwordLabel;
	static JTextField idTextField, passwordTextField;
	static JButton loginButton;
	
	// after login UI
	static JLabel connectionStatusLabel, questionLabel;
	static JButton disconnectButton;
	static ButtonGroup buttonGroup;
	static JRadioButton button1, button2, button3, button4;
	static JButton submitButton;
	static JLabel questionStatusLabel;
	
	static JLabel finalScoreLabel;
	
	
	
	// non UI elements
	static String studentID;
	static String password;
	static Socket clientSocket;
	static ArrayList<Question> ExamQuestions = new ArrayList<Question>();
	static ArrayList<String> studentAnswers = new ArrayList<String>();
	static boolean disconnect;
	
	
	public static void main(String[] args) {
		
		initializeFrame();
		
		loginButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					if(!(idTextField.getText().equals("") || passwordTextField.getText().equals(""))) {
						
						studentID = idTextField.getText();
						password = passwordTextField.getText();
						
						//connect to server
						//create a new socket to connect with the server application 
	    				clientSocket = new Socket ("localhost", 6789);		    				
		    			
		    			//call function StartThread
		    			StartThread();
						
		    			// send user's login credentials to server
		    			DataOutputStream outToServer = new DataOutputStream (clientSocket.getOutputStream());
		    			String sendingSentence = "-requestLogin," + password + "," + studentID + "\n";
		    			outToServer.writeBytes(sendingSentence);
		    			
					}
					else {
						throw new Exception("Fields cannot be empty");
					}
				} catch (Exception e1) {
					String msg[] = e1.toString().split(":");
					exceptionFrame(msg[1]);
					e1.printStackTrace();
				}
				
			}
		});
		
		//Disconnect on frame close
	    frame.addWindowListener(new WindowAdapter() {
	    	public void windowClosing(WindowEvent we) {
	    		  
	    		try {
	
	    			//create an output stream and send a Remove message to disconnect from the server 
    				DataOutputStream outToServer = new DataOutputStream (clientSocket.getOutputStream());		    				
  	    			outToServer.writeBytes("-Remove\n");
  	    			
  	    			//close the client's socket
    				clientSocket.close();
    				
    				
    				System.exit(0);
    				
	    		  } catch (Exception ex) {
		    			System.out.println(ex.toString());
		    		}
	    		
	    	    
	    	  }
	    });   
	    	   
    }
		


	static void initializeFrame() {
		
		frame = new JFrame("Examination Client");
		frame.setLayout(null);
		frame.setBounds(100, 100, 400, 380);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		idLabel = new JLabel("ID:");
		idLabel.setBounds(40, 40, 100, 20);
		frame.getContentPane().add(idLabel);
		
		passwordLabel = new JLabel("Password:");
		passwordLabel.setBounds(40, 80, 100, 20);
		frame.getContentPane().add(passwordLabel);
		
		idTextField = new JTextField();
		idTextField.setBounds(160, 40, 200, 20);
		frame.getContentPane().add(idTextField);

		passwordTextField = new JTextField();
		passwordTextField.setBounds(160, 80, 200, 20);
		frame.getContentPane().add(passwordTextField);
		
		loginButton = new JButton("Login");
		loginButton.setBounds(160, 110, 100,20);
		frame.getContentPane().add(loginButton);
		
		//after login UI
		
		connectionStatusLabel = new JLabel("Connected");
		connectionStatusLabel.setBounds(40, 40, 80, 20);
		connectionStatusLabel.setForeground(Color.blue);
		frame.getContentPane().add(connectionStatusLabel);
		
		disconnectButton = new JButton("Disconnect");
		disconnectButton.setBounds(200, 40, 100, 20);
		frame.getContentPane().add(disconnectButton);
		
		questionLabel = new JLabel();
		questionLabel.setBounds(40, 70, 400, 20);
		frame.getContentPane().add(questionLabel);
		
		button1 = new JRadioButton();
		button1.setBounds(40, 100, 300, 20);
		frame.getContentPane().add(button1);
		
		button2 = new JRadioButton();
		button2.setBounds(40, 130, 300, 20);
		frame.getContentPane().add(button2);
		
		button3 = new JRadioButton();
		button3.setBounds(40, 160, 300, 20);
		frame.getContentPane().add(button3);
		
		button4 = new JRadioButton();
		button4.setBounds(40, 190, 300, 20);
		frame.getContentPane().add(button4);
		
		buttonGroup = new ButtonGroup();
		buttonGroup.add(button1);
		buttonGroup.add(button2);
		buttonGroup.add(button3);
		buttonGroup.add(button4);
		
		submitButton = new JButton("Submit Answer");
		submitButton.setBounds(40, 230, 150, 20);
		frame.getContentPane().add(submitButton);
		
		questionStatusLabel = new JLabel();
		questionStatusLabel.setBounds(220, 230, 180, 20);
		frame.getContentPane().add(questionStatusLabel);
		
		finalScoreLabel = new JLabel();
		finalScoreLabel.setBounds(40, 100, 250, 20);
		frame.getContentPane().add(finalScoreLabel);
		
		// visibility
		frame.setVisible(true);
		idLabel.setVisible(true);
		passwordLabel.setVisible(true);
		idTextField.setVisible(true);
		passwordTextField.setVisible(true);
		loginButton.setVisible(true);
		
		questionLabel.setVisible(false);
		connectionStatusLabel.setVisible(false);
		disconnectButton.setVisible(false);
		button1.setVisible(false);
		button2.setVisible(false);
		button3.setVisible(false);
		button4.setVisible(false);
		submitButton.setVisible(false);
		questionStatusLabel.setVisible(false);
		finalScoreLabel.setVisible(false);
		
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
	
	private static void StartThread() {
			
			new Thread (new Runnable(){ @Override
	 	   		public void run() {
				
				
				
				try {
	
					//create a buffer reader and connect it to the socket's input stream
					BufferedReader inFromServer = new BufferedReader (new InputStreamReader(clientSocket.getInputStream()));
	
					
					String receivedSentence;
					
					//always read received messages and append them to the textArea
					while (true) {
											
						receivedSentence = inFromServer.readLine();	
						
						// check that server is active before allowing clients to connect
						if (receivedSentence.startsWith("-ServerActive")) {
							
							ObjectInputStream examQuestions = new ObjectInputStream(clientSocket.getInputStream());
							ExamQuestions = (ArrayList<Question>) examQuestions.readObject();
							
							idLabel.setVisible(false);
							passwordLabel.setVisible(false);
							idTextField.setVisible(false);
							passwordTextField.setVisible(false);
							loginButton.setVisible(false);
							
							
							connectionStatusLabel.setVisible(true);
							disconnectButton.setVisible(true);
							
							questionLabel.setText("Question 1: " + ExamQuestions.get(0).question);
							questionLabel.setVisible(true);
							
							button1.setText(ExamQuestions.get(0).option1);
							button2.setText(ExamQuestions.get(0).option2);
							button3.setText(ExamQuestions.get(0).option3);
							button4.setText(ExamQuestions.get(0).option4);
							button1.setVisible(true);
							button2.setVisible(true);
							button3.setVisible(true);
							button4.setVisible(true);
							
							submitButton.setVisible(true);
							questionStatusLabel.setText("Question 1 of " + ExamQuestions.size());
							questionStatusLabel.setVisible(true);
							
						}
						else if(receivedSentence.startsWith("-BadPassword")) {
							exceptionFrame("Incorrect Password");
						}
						else if(receivedSentence.startsWith("-BadID")) {
							exceptionFrame("ID number is not recognized");
						}
						else if(receivedSentence.startsWith("-ServerFull")) {
							exceptionFrame("Server cannot accept any more connections");
						}
						else if(receivedSentence.startsWith("-IDAlreadyExists")) {
							exceptionFrame("User with ID is already connected");
						}
						else if(receivedSentence.startsWith("-Results")) {
							String result[] = receivedSentence.split(",");
							
							connectionStatusLabel.setVisible(false);
							disconnectButton.setVisible(false);
							
							questionLabel.setVisible(false);
							
							
							button1.setVisible(false);
							button2.setVisible(false);
							button3.setVisible(false);
							button4.setVisible(false);
							
							submitButton.setVisible(false);
							questionStatusLabel.setVisible(false);
							
							finalScoreLabel.setText("You result is " + result[1] + " out of " + ExamQuestions.size());
							finalScoreLabel.setVisible(true);
							
							//after getting results if disconnectButton was clicked, then remove student from server
							if(disconnect) {
								
								DataOutputStream outToServer = new DataOutputStream (clientSocket.getOutputStream());		    				
				    			outToServer.writeBytes("-Remove\n");
				    			
				    			//close the client's socket
			    				clientSocket.close();
							}
							
						}
					
						submitButton.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								
								
									if(studentAnswers.size() < ExamQuestions.size()) {
										
										// add student's response to list of answers
										if(button1.isSelected()) {
											studentAnswers.add("1");
											nextQuestion();
										} else if(button2.isSelected()) {
											studentAnswers.add("2");
											nextQuestion();
										} else if(button3.isSelected()) {
											studentAnswers.add("3");
											nextQuestion();
										} else if(button4.isSelected()) {
											studentAnswers.add("4");
											nextQuestion();
										} else {
											exceptionFrame("Must select an answer");
										}
										
										if(studentAnswers.size() == ExamQuestions.size()) {
											try {
												DataOutputStream outToServer = new DataOutputStream (clientSocket.getOutputStream());
												
												String sendingSentence = "-Verify," + studentAnswers + "\n";
												outToServer.writeBytes(sendingSentence);
											} catch (IOException e1) {
												
												e1.printStackTrace();
											}
										}
									}
									
								
							}
						});
						
						// get exam results from server, then disconnect from server 
						disconnectButton.addActionListener(new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								
								//flag to disconnect from server after results have been received from server
								disconnect = true;
								
								//get results by sending answers given before disconnect, 
								//send "dummy" value for unanswered questions that does not match correct answers 
							
								ArrayList<String> incompleteAnswers = new ArrayList<String>();
								int unansweredQuestions = ExamQuestions.size() - studentAnswers.size();
								
								for(int i = 0; i < studentAnswers.size(); i++) {
									incompleteAnswers.add(studentAnswers.get(i));
								}
								// remainder of unanswered question filled with "x"
								for(int i = 0; i < unansweredQuestions; i++) {
									incompleteAnswers.add("x");
								}
								try {
									DataOutputStream outToServer = new DataOutputStream (clientSocket.getOutputStream());
									
									String sendingSentence = "-Verify," + incompleteAnswers + "\n";
									outToServer.writeBytes(sendingSentence);
								} catch (IOException e1) {
									
									e1.printStackTrace();
								}	
								
						}});
						
					}
					
				}
				catch(Exception ex) {
					
				}
		
			}}).start();
			
		}
	
		// argument is number of questions answered so far
		static void nextQuestion() {
			
			int currentQuestion = 1 + studentAnswers.size();
			
			if(studentAnswers.size() != ExamQuestions.size()) {
				questionLabel.setText("Question " + currentQuestion + ": " + ExamQuestions.get(studentAnswers.size()).question);
				button1.setText(ExamQuestions.get(studentAnswers.size()).option1);
				button2.setText(ExamQuestions.get(studentAnswers.size()).option2);
				button3.setText(ExamQuestions.get(studentAnswers.size()).option3);
				button4.setText(ExamQuestions.get(studentAnswers.size()).option4);
				
				questionStatusLabel.setText("Question " + currentQuestion + " of " + ExamQuestions.size());
				
				buttonGroup.clearSelection();
			}
		}

		
	}

	