FROM gradle:jdk21-alpine AS build
WORKDIR /app
RUN apk update && apk add git
RUN git clone https://github.com/gabrielvianaz/bandwith-monitor.git .
RUN ./gradlew clean build

FROM openjdk:21
WORKDIR /app
COPY --from=build /app/build/libs/bandwith-monitor.jar bandwith-monitor.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/bandwith-monitor.jar"]
