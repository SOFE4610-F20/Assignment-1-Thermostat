package sofe4610.controller;

import org.eclipse.paho.client.mqttv3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class App {

    public static final String HELP_MESSAGE = "Type \"read\" to read the current temperature. " +
            "Type a number to change the setpoint.";

    public static void main(String[] args) throws MqttException, IOException {
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

        final AtomicReference<Double> tempReading = new AtomicReference<>(null);

        mqttClient.subscribe("home/thermostat/temp", (topic, message) -> {
            tempReading.set(Double.parseDouble(message.toString()));
        });

        System.out.println(HELP_MESSAGE);

        final BufferedReader bufferReader = new BufferedReader(new InputStreamReader(System.in));
        String line;

        while ((line = bufferReader.readLine()) != null) {
            line = line.trim();

            if ("read".equalsIgnoreCase(line.trim())) {
                Double temp = tempReading.get();

                if (temp == null) {
                    System.out.println("No temperature readings available!");
                } else {
                    System.out.printf("Temperature: %s Deg F%n", temp);
                }
            } else if (!line.isEmpty()) {
                try {
                    final byte[] buffer = Double.toString(Double.parseDouble(line.trim())).getBytes();
                    final MqttMessage setpointMsg = new MqttMessage(buffer);
                    mqttClient.publish("home/thermostat/setpoint", setpointMsg);
                } catch (final NumberFormatException e) {
                    System.out.printf("\"%s\" is not a number!%n", line);
                    System.out.println(HELP_MESSAGE);
                }
            }
        }
    }
}
