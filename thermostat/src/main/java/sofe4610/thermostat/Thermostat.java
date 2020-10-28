package sofe4610.thermostat;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Thermostat implements IMqttMessageListener {

    private final IMqttClient client;
    private final long startTime;
    private long lastMeasurementTime;
    private double baseTemp;
    private double setpoint;

    public Thermostat(final IMqttClient client, final double initialTemp, final double initialSetpoint) {
        this.client = client;
        this.baseTemp = initialTemp;
        this.setpoint = initialSetpoint;
        this.startTime = System.currentTimeMillis();
        this.lastMeasurementTime = this.startTime;
    }

    public Thermostat(final IMqttClient client) {
        this(client, 72, 72);
    }

    /**
     * Publishes the temperature
     */
    public void publishTemperature() {
        if (client.isConnected()) {
            final byte[] buffer = Double.toString(readTemperature()).getBytes();
            final MqttMessage tempMsg = new MqttMessage(buffer);
            // default QOS is 1, no need to change

            try {
                client.publish("home/thermostat/temp", tempMsg);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Generate the next temperature reading. Temp approaches setpoint at a rate of 0.25 Deg / s
     *
     * @return Temperature in Farenheit
     */
    public double readTemperature() {
        final double timeSinceStart = (System.currentTimeMillis() - startTime) / 1000d;
        final double timeSinceLastMeasurement = (System.currentTimeMillis() - lastMeasurementTime) / 1000d;
        final double tempDiff = setpoint - baseTemp;

        if (tempDiff < 0) {
            baseTemp = Math.max(setpoint, baseTemp - 0.25 * timeSinceLastMeasurement);
        } else if (tempDiff > 0) {
            baseTemp = Math.min(setpoint, baseTemp + 0.25 * timeSinceLastMeasurement);
        }

        lastMeasurementTime = System.currentTimeMillis();
        return baseTemp + 0.5 * Math.sin(0.25 * timeSinceStart);
    }

    @Override
    public void messageArrived(final String topic, final MqttMessage message) {
        setpoint = Double.parseDouble(message.toString());
    }
}
