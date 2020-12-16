package Lab2.LogData;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Date;

public class LogData {
    String topicOne = "sensor/KYH/JF/DATAONE";
    String topicTwo = "sensor/KYH/JF/DATATWO";
    String broker = "tcp://broker.hivemq.com:1883";
    String client = "JF_Client";
    MqttClient mqttClient;


    LogData() {
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            mqttClient = new MqttClient(broker, client, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            System.out.println("Connected and subscribing to: " + topicOne + " and " + topicTwo);
            mqttClient.subscribe(topicOne, new MqttPostPropertyMessageListener());
            mqttClient.subscribe(topicTwo, new MqttPostPropertyMessageListener());

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    class MqttPostPropertyMessageListener implements IMqttMessageListener {
        @Override
        public void messageArrived(String topic, MqttMessage content) throws Exception {
            Date currentDate = new Date();
            String receivedContent = content.toString();
            System.out.println(currentDate + ": " + receivedContent);
            LogToFile lf = new LogToFile();
            lf.Log(receivedContent);
        }
    }

    class LogToFile {
        void Log(String data) throws IOException {
            Date currentDate = new Date();
            File logFile = new File("src/Lab2/LogFiles/logData.txt");
            FileWriter fw = new FileWriter(logFile, true);
            fw.write(currentDate + ": " + data + "\n");
            fw.close();
        }
    }

    public static void main(String[] args) {
        new LogData();
    }
}

