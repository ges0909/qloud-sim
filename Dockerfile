FROM java:8
EXPOSE 4567
ADD /target/qloud_simulator-1.0.0-jar-with-dependencies.jar qloud_simulator-jar-with-dependencies.jar
ENTRYPOINT ["java","-jar","qloud_simulator-jar-with-dependencies.jar"]
