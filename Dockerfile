FROM openjdk:17
ADD /target/career_center_attestation-0.0.1-SNAPSHOT.jar backend.jar
ENTRYPOINT "java", "-jar", "backend.jar"
