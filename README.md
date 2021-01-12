# Online-Examination-System
Final assignment for a Telecommunication Networks course

To launch application from Command Prompt:

First, ensure that java is installed on your PC.

1) Download this project's .zip file and extract to convenient location.

2) Open a command prompt and move to directory where the project's java files are found:
  >cd <directory_where_project_saved>/Online-Examination-System-main/onlineExaminationSystem

3) Compile the .java files:
  >javac -cp ../ oesServer.java oesClient.java clientThread.java Question.java

4) Run the server application by typing:
  >java -cp ../ onlineExaminationSystem/oesServer

5) Fill in forms on oesServer until shown the connection status window.

6) Launch the same number of instances of oesClient as the number of students you specified in oesServer:
  >java -cp ../ onlineExaminationSystem/oesClient
