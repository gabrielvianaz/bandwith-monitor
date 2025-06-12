FROM gradle:jdk21-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM openjdk:21
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/bandwith-monitor.jar /app/bandwith-monitor.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/bandwith-monitor.jar"]
