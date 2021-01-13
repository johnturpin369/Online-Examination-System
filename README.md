# Online-Examination-System
This is the final assignment for a Telecommunication Networks course. The goal of this assignment was to create a client/server application whereby students (clients) connect to and complete an examination hosted by a server. At the end of the exam, the students are given a final score. To run the program ensure that Java is installed on your PC. Begin by launching the server application (oesServer) and creating a questionnaire for the students. Then launch as many instances of the client application as the number of students specified in server application. 

To launch the application from the Command Prompt:

1) Download this project's .zip file and extract to a convenient location.

2) Open a command prompt and move to a directory where the project's java files are found:
  >cd <directory_where_project_saved>/Online-Examination-System-main/onlineExaminationSystem

3) Compile the java files:
  >javac -cp ../ oesServer.java oesClient.java clientThread.java Question.java

4) Launch the server application:
  >java -cp ../ onlineExaminationSystem/oesServer

5) Follow all instruction on oesServer until shown the connection status window.

6) Launch the same number of instances of oesClient as the number of students specified in oesServer:
  >java -cp ../ onlineExaminationSystem/oesClient
