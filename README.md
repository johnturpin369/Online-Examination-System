# Online-Examination-System
Final assignment for a Telecommunication Networks course

To launch application from Command Prompt:

-Download the .zip file and extract to convenient location.
-Open a command prompt and move to directory where .java and .class fire are found.
-Compile the .java files:
  >javac -cp ../ oesServer.java oesClient.java clientThread.java Question.java
-Run the server application by typing:
  > java -cp ../ onlineExaminationSystem/oesServer
  
-proceed to fill in forms on oesServer until you have entered number of students.
-once you know how many students, launch the corresponding number of instances of oesClient:
  > java -cp ../ onlineExaminationSystem/oesClient
