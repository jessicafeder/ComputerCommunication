package Lab2;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class SubPublishTopic {

    String topicToSubscribe = "sensor/KYH/JF/DATA";
    String broker = "tcp://broker.hivemq.com:1883";
    String client = "subClient";
    MqttClient mqttClient;


    SubPublishTopic() {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(broker, client, persistence);
            MqttConnectOptions connection = new MqttConnectOptions();
            connection.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            mqttClient.connect(connection);
            System.out.println("Connected to topic: " + topicToSubscribe);
            mqttClient.subscribe(topicToSubscribe, new MqttPostPropertyMessageListener());

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    class MqttPostPropertyMessageListener implements IMqttMessageListener {
        @Override
        public void messageArrived(String topic, MqttMessage content) {
            String receivedContent = topic + ": " + content.toString();
            System.out.println(receivedContent);

        }
    }
    public static void main(String[] args) {
        new SubPublishTopic();
    }
}
