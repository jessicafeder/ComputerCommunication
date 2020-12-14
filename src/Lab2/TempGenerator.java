package Lab2;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

class TempGenerator {
    String topic = "sensor/KYH/JF";
    String content = "temp " + (int) (Math.random() * 10 +15) ;
    int qos = 2;
    String broker = "tcp://broker.hivemq.com:1883";
    String clientId = "JavaSample";
    int secondsTimer = 60;
    long delay = secondsTimer * 1000L;
    MemoryPersistence persistence = new MemoryPersistence();
    Timer timer;

    TempGenerator() {

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
            timer.schedule(new TimerDo(), delay);

        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }

    class MqttPostPropertyMessageListener implements IMqttMessageListener {
        @Override
        public void messageArrived(String var1, MqttMessage var2) throws Exception {
            System.out.println(var1 + ": " + var2.toString());
        }
    }

    class TimerDo extends TimerTask {
        public void run() {
            try {
                String temp = getStringDegree() + "°C";
                MqttMessage message = new MqttMessage(temp.getBytes(StandardCharsets.UTF_8));
                message.setQos(2);
                MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
                sampleClient.publish(topic, message);
                System.out.println("Sent temperature: " + temp);
            } catch (MqttException e) {
                e.printStackTrace();
            }
            timer.cancel();
            timer = new Timer();
            timer.schedule(new TimerDo(), delay);
            System.out.println("Created new timer");
        }
    }

    String getStringDegree() {
        int degree = (int) (Math.random() * 10) + 15;
        return String.valueOf(degree);
    }

    public static void main(String[] args) {
        new TempGenerator();
    }
}
