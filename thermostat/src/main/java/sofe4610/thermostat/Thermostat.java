package sofe4610.thermostat;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Thermostat implements IMqttMessageListener {

    private final IMqttClient client;
    private double lastTemperature;
    private double setpoint;

    public Thermostat(final IMqttClient client, final double initialTemp, final double initialSetpoint) {
        this.client = client;
        this.lastTemperature = initialTemp;
        this.setpoint = initialSetpoint;
    }

    public Thermostat(final IMqttClient client) {
        this(client, 72, 72);
    }

    /**
     * Publishes the temperature
     */
    public void publishTemperature() {
        // todo; publish temperature to temperature topic
    }

    /**
     * Get the next temperature reading
     *
     * @return Temperature in Farenheit
     */
    public double readTemperature() {
        // TODO; generate temperature reading according to equation
        return -1;
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        // process mqtt message to change setpoint
    }
}
