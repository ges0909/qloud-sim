How to deploy?
==============
Navigate to the project folder and issue following commands:
- mvn clean
- mvn package
- docker build -f Dockerfile -t qloud_simulator .
- docker run -p 4567:4567 qloud_simulator