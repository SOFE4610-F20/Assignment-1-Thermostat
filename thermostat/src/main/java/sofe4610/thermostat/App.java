package sofe4610.thermostat;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {

    public static void main(String[] args) throws MqttException {
        if (args.length < 1) {
            System.err.println("please provide broker server address; (such as localhost)");
            return;
        }

        final String publisherId = UUID.randomUUID().toString();
        final IMqttClient mqttClient = new MqttClient("tcp://" + args[0] + ":1883", publisherId);

        final MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);
        mqttClient.connect(options);

        final Thermostat thermostat = new Thermostat(mqttClient);
        mqttClient.subscribe("home/thermostat/setpoint", thermostat);

        final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(thermostat::publishTemperature, 0, 1, TimeUnit.SECONDS);
    }
}
