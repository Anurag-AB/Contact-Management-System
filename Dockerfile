FROM eclipse-temurin:21-jre-alpine 
 
## create a nonroot user and group 
RUN addgroup -S spring && adduser -S spring -G spring 
 
## copy the spring jar 
COPY target/*.jar /opt/myApp.jar 
 
## set the nonroot user as the default user 
USER spring:spring 
 
## set the working directory 
WORKDIR /opt 
 
ENTRYPOINT ["java", "-jar", "myApp.jar"] 

EXPOSE 80