# Stage that builds the application, a prerequisite for the running stage
FROM openjdk:17-jdk-slim

# Set password for root
RUN echo 'root:D0ck3r!"' | chpasswd

# Install some network utilities for tracing problems
RUN apt-get update -qq && apt-get install -qq --no-install-recommends inetutils-ping telnet curl netcat nodejs

# Add a non-root user for running the app
RUN useradd -m myuser
WORKDIR /usr/app
RUN chown myuser:myuser /usr/app

# Copy the compiled app to the image
USER myuser
COPY --chown=myuser:myuser target/*.jar ./app.jar

# Expose the app port
EXPOSE 8080

# Execute the app
ENTRYPOINT ["java", "-jar", "./app.jar"]
