package Lab2.CheckData;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class CheckData {

    String topicOne = "sensor/KYH/JF/DATAONE";
    String topicTwo = "sensor/KYH/JF/DATATWO";
    String broker = "tcp://broker.hivemq.com:1883";
    String client = "JF_Listener";
    MqttClient mqttClient;

    CheckData() {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(broker, client, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            System.out.println("Connected to topic: " + topicOne);
            mqttClient.subscribe(topicOne, new MqttPostPropertyMessageListener());

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    class MqttPostPropertyMessageListener implements IMqttMessageListener {
        @Override
        public void messageArrived(String topic, MqttMessage content) {
            String receivedContent = content.toString();
            System.out.println(receivedContent);
            CheckReceivedContent(receivedContent);
        }
    }

    void CheckReceivedContent(String receivedContent) {
        try {
            int x = Integer.parseInt(receivedContent.substring(0, 2));
            String control;
            if (x >= 22) {
                control = "Ctrl -";
                MqttMessage msg = new MqttMessage(control.getBytes());
                mqttClient.publish(topicTwo, msg);
            } else {
                control = "Ctrl +";
                MqttMessage msg = new MqttMessage(control.getBytes());
                mqttClient.publish(topicTwo, msg);
            }
            System.out.println(control);
        } catch (Exception e) {
            System.err.println("Error");
        }
    }

    public static void main(String[] args) {
        new CheckData();
    }
}
