package Lab2;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.imageio.IIOException;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.io.FileWriter;

public class LogData {
    String topicOne = "sensor/KYH/JF";
    String topicTwo = "sensor/KYH/JF/DATA";
    DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    String cal = date.format(now) + ": " + "Temperature: " + (int) (Math.random() * 10 +15) + "°C";
    String broker = "tcp://broker.hivemq.com:1883";
    String client = "logClient";
    MqttClient mqttClient;
    MemoryPersistence persistence = new MemoryPersistence();

    LogData() throws IOException {
        try {
            mqttClient = new MqttClient(broker, client, persistence);
            MqttConnectOptions connection = new MqttConnectOptions();
            connection.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            mqttClient.connect(connection);
            System.out.println("Connected and subscribing to: " + topicOne + " and " + topicTwo);
            System.out.println("Writing to topic: " + topicTwo);
            FileWriter log = new FileWriter("logData.txt");
            mqttClient.subscribe(topicOne, (topicTwo, message) -> {
                System.out.println("Adding to LogData.txt: " + message.toString() + " from " + topicOne);
                log.write(cal + " - " + topicOne + " - " + message.toString());
                log.flush();
            });
            mqttClient.subscribe(topicTwo, (topicOne, message) -> {
                System.out.println("Adding to LogData.txt: " + message.toString() + " from " + topicTwo);
                log.write(cal + " - " + topicTwo + " - " + message.toString());
                log.flush();
            });

        } catch (MqttException e) {
            e.printStackTrace();
        } catch (IIOException e) {
            System.out.println("Error");
        }
    }

    public static void main(String[] args) throws IOException {
        new LogData();
    }
}

