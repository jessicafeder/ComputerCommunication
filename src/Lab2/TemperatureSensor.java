package Lab2;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


class TemperatureSensor {
    Timer timer;
    int seconds = 10;
    DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    String topic = "sensor/KYH/JF";
    String content = date.format(now) + ": " + "Temperature: " + (int) (Math.random() * 10 +15) + "°C";
    int qos = 2;
    String broker = "tcp://broker.hivemq.com:1883";
    String clientId = "JavaSample";
    MemoryPersistence persistence = new MemoryPersistence();

    TemperatureSensor() {
        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: " + broker);
            sampleClient.connect(connOpts);
            System.out.println("Connected");
            System.out.println("Publishing message: " + content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            System.out.println("Message published");
            sampleClient.subscribe(topic, new MqttPostPropertyMessageListener());
            timer = new Timer();
            timer.schedule(new RemindTask(), seconds*1000L);



        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    class MqttPostPropertyMessageListener implements IMqttMessageListener {
        @Override
        public void messageArrived(String var1, MqttMessage var2) throws Exception {
            System.out.println(var1 + ": " + var2.toString());
        }
    }


    class RemindTask extends TimerTask {
        @Override
        public void run() {
            System.out.println("Printing new temperature every 60 seconds...");
            timer.cancel();
            new TemperatureSensor();
            }
        }

    public static void main(String[] args) {
        new TemperatureSensor();

    }
}

