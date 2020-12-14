package Lab2;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class ClientData {
    String subscribeTopic = "sensor/KYH/JF";
    String writeTopic = "sensor/KYH/JF/DATA";
    String mqttBroker = "tcp://broker.hivemq.com:1883";
    String clientId = "JavaSample3";
    MqttClient mqttClient;

    ClientData() {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(mqttBroker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + mqttBroker);
            mqttClient.connect(connOpts);
            System.out.println("Connected and listening to topic: " + subscribeTopic);
            System.out.println("Writes to topic: " + writeTopic);
            mqttClient.subscribe(subscribeTopic, new MqttPostPropertyMessageListener());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    class MqttPostPropertyMessageListener implements IMqttMessageListener {
        @Override
        public void messageArrived(String topic, MqttMessage content) throws MqttException, IOException {
            String text = content.toString();
            saveToLog( "temperature, " + text);
            int x = Integer.parseInt(text.substring(0,2));
            System.out.println(text);
            String s = "ctrl, ";
            if (x >= 22) {
                s = s + "-";
            } else {
                s = s + "+";
            }
            saveToLog(s);
            MqttMessage msg = new MqttMessage(s.getBytes(StandardCharsets.UTF_8));
            msg.setQos(2);
            mqttClient.publish(writeTopic, msg);
        }
    }

    void saveToLog(String content) throws IOException {
        Date date = new Date();
        FileWriter fw = new FileWriter("src/Lab2/logData.txt", true);
        fw.write(date + ", " + content + "\n");
        fw.close();
    }


    public static void main(String[] args) {
        new ClientData();
    }

}
