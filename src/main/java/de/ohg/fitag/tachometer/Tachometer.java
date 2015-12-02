package de.ohg.fitag.tachometer;


import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
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

            private boolean triggered = false;

            @Override
            public boolean isTriggered() {
                //trigger when detected brightness higher than 50 (LightSensor measures between 0 and 100)
                boolean lighten = Tachometer.getLightSensor().readValue() > 50;

                boolean ret = lighten && !triggered;

                this.triggered = lighten;

                return ret;
            }
        });

        //format timestamps to a detailed format with hours, minutes, seconds and milliseconds
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

        //main flow
        while(!Button.ENTER.isDown()){
            //speed is time through distance
            float speed = sequenceDetector.detectSequence() / ROTATION_DISTANCE;
            Date timestamp = new Date();

            //Hard readable but formats for example to: "[22:30:15:306] Speed: 12.12cm/ms"
            System.out.printf("[%s] Speed: %.2fkm/h%n", dateFormat.format(timestamp), speed*3,6);
            //System.out.println("Speed: " + speed + " cm/ms");
        }
        System.out.println("Event loop cancelled.");
    }

    public static LightSensor getLightSensor(){
        return lightSensor;
    }
}
