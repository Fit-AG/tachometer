package de.ohg.fitag.tachometer;


import lejos.nxt.*;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Calvin on 24.11.2015.
 */
public class Tachometer {

    public final static float ROTATION_DISTANCE = 37.7f;
    private static LightSensor lightSensor;

    public static void main(String[] args){
        System.out.println("------------------------------");
        System.out.println("SequenceDetection using leJOS");
        System.out.println("------------------------------");

        /**
         * Following code had not been tested in development environment due compatibility issues.
         */

        lightSensor = new LightSensor(SensorPort.S1);
        System.out.println("Press ENTER to calibrate lightlevel LOW");
        Button.ENTER.waitForPressAndRelease();
        lightSensor.calibrateLow();
        System.out.printf("LightSensor calibrated to %d%n", lightSensor.getLow());

        System.out.println("Press ENTER to calibrate lightlevel HIGH");
        Button.ENTER.waitForPressAndRelease();
        lightSensor.calibrateHigh();
        System.out.printf("LightSensor calibrated to %d%n", lightSensor.getHigh());

        SequenceDetector sequenceDetector = new SequenceDetector(new AbstractDetector() {

            private boolean wasTriggered = false;

            @Override
            public boolean isTriggered() {
                //trigger when detected brightness higher than 50 (LightSensor measures between 0 and 100)
                boolean lighted = Tachometer.getLightSensor().readValue() > 50;

                boolean isTriggered = lighted && !this.wasTriggered;

                this.wasTriggered = lighted;

                return isTriggered;
            }
        });

        //format timestamps to a detailed format with hours, minutes, seconds and milliseconds
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

        //main flow
        while(!Button.ENTER.isDown()){
            float sequence = sequenceDetector.detectSequence()/1000.0f;
            //speed is distance through time
            float speed = ROTATION_DISTANCE / sequence;
            float frequency = 1.0f/sequence;
            Date timestamp = new Date();

            //Hard readable but formats for example to: "[22:30:15:306] Frequency: 13.23Hz, Speed: 12.12cm/ms"

            System.out.printf("[%s] Sequence: %.4ss, Frequency:  %.4fHz, Speed: %.4fcm/s%n", dateFormat.format(timestamp),
                    sequence, frequency, speed);
        }
        System.out.println("Event loop cancelled.");
        NXT.exit(0);
    }

    public static LightSensor getLightSensor(){
        return lightSensor;
    }
}
