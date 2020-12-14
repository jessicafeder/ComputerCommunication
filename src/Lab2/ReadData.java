package Lab2;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class ReadData {
    String subscribeTopic = "sensor/KYH/JF/DATA";
    String mqttBroker = "tcp://broker.hivemq.com:1883";
    String clientId = "JavaSample2";
    MqttClient mqttClient;

    ReadData() {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient(mqttBroker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + mqttBroker);
            mqttClient.connect(connOpts);
            System.out.println("Connected and listening to topic: " + subscribeTopic);
            mqttClient.subscribe(subscribeTopic, new MqttPostPropertyMessageListener());
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
        new ReadData();
    }
}
