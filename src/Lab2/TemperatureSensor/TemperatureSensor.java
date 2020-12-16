package Lab2.TemperatureSensor;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Timer;
import java.util.TimerTask;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


class TemperatureSensor {
    Timer timer;
    int seconds = 60;
    DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    String topic = "sensor/KYH/JF/DATAONE";
    String content = date.format(now) + ": " + "Temperature: " + (int) (Math.random() * 10 + 15) + "°C";
    int qos = 2;
    String broker = "tcp://broker.hivemq.com:1883";
    String clientId = "JF_Sensor";
    MemoryPersistence persistence = new MemoryPersistence();

    TemperatureSensor() {
        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            System.out.println("Connecting to broker...");
            sampleClient.connect(connOpts);
            System.out.println("Connected to broker: " + broker);
            System.out.println("Publishing message: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            System.out.println("Message published");
            System.out.println("Printing new temperature every 60 seconds...");
            System.out.println("---------------------------------------------------------------");
            sampleClient.subscribe(topic, new MqttPostPropertyMessageListener());
            timer = new Timer();
            timer.schedule(new RemindTask(), seconds * 1000L);

        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    class MqttPostPropertyMessageListener implements IMqttMessageListener {
        @Override
        public void messageArrived(String topic, MqttMessage content) {
            System.out.println(topic + ": " + content.toString());
        }
    }

    class RemindTask extends TimerTask {
        @Override
        public void run() {
            timer.cancel();
            new TemperatureSensor();
        }
    }

    public static void main(String[] args) {
        System.out.println("\nTemperature Generator " + "\u2600" + "\n");
        System.out.println("---------------------------------------------------------------" + "\n");
        new TemperatureSensor();
    }
}

