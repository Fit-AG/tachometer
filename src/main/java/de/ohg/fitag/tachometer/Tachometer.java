package de.ohg.fitag.tachometer;


import lejos.nxt.Button;
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

    public final static float ROTATION_DISTANCE = 3.0f;

    public static void main(String[] args){
        System.out.println("------------------------------");
        System.out.println("SequenceDetection using leJOS");
        System.out.println("------------------------------");

        /**
         * Following code had not been tested in development environment due compatibility issues.
         */

        //establish nxt connection
        NXTConnector conn = new NXTConnector();
        //log errors
        conn.addLogListener(new NXTCommLogListener() {
            public void logEvent(String message) {
                System.out.println(message);
            }

            public void logEvent(Throwable throwable) {
                System.err.println(throwable.getMessage());
            }
        });
        conn.setDebug(true);

        if (!conn.connectTo()) {
            System.err.println("Failed to connect");
            System.exit(1);
        }

        LightSensor lightSensor = new LightSensor(SensorPort.S1);

        SequenceDetector sequenceDetector = new SequenceDetector(new AbstractDetector() {
            @Override
            public boolean isTriggered() {
                //trigger when detected brightness higher than 50 (LightSensor measures between 0 and 100)
                return lightSensor.readValue() > 50;
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
            System.out.printf("[%s] Speed: %.2fcm/ms%n", dateFormat.format(timestamp), speed);
        }
    }

}
