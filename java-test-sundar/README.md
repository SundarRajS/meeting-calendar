# Meeting Calendar - Request Scheduler

## Overview
The following repo contains Meeting Request Scheduler

## Guidelines to Run the application.


1. Extract the zip file and get into the code repo folder.

2. Go to the repo folder and run ```mvn clean install```

3. Once the process is completed, there should be the Jar file instead the target folder

4. run the jar file with the command ```java -jar java-test-sundar-0.0.1-SNAPSHOT.jar```. Application will be running in the port 8001.

## Run the application with docker.

1. Go to the repo folder and build the application with the command ```docker build . -t <tagname>```.

2. start the application with docker using ```docker run -p 8001:8001 <tagname>```


## Source Code

Source code for the example is located in /src/main/java/com/meeting/booking. 
tests are located in /src/test/java/com/meeting/booking

## Run the example of Meeting Request Application


1. Go to the application url using http://localhost:8001/meeting-calendar. System reads flat file available in application classpath(file is located inside the src/main/resources)
and exposes the meeting requests in the JSON format.

2. Bookings are displayed in the asc order of booking date.

3. Overlapping meeting request with the already existing records are not booked.

4. Records which are discarded are displayed in the logged in the system console.

5. Test reports are generated inside the jacoco path inside target.

5. Access the API docs using url http://localhost:8001/v3/api-docs/, and swagger UI with http://localhost:8001/swagger-ui/index.html.


