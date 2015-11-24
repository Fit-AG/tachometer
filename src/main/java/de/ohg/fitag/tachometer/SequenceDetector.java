package de.ohg.fitag.tachometer;

import lejos.nxt.ADSensorPort;

/**
 * Created by Calvin on 24.11.2015.
 *
 * A SequenceDetector that be used to detect sequences and measure the elapsed time
 */
public class SequenceDetector {

    private AbstractDetector detector;

    public SequenceDetector(AbstractDetector detector){
        this.detector = detector;
    }

    /**
     * Detect a sequence and count the time until the detector is triggered
     * @return long Time until triggered
     */
    public long detectSequence(){
        long startTime = System.currentTimeMillis();
        //sleep until triggered
        while(!detector.isTriggered()) ;

        return (System.currentTimeMillis() - startTime);
    }
}

/**
 * Abstract interface that can be used to register different sources as SequenceDetectors
 */
 interface AbstractDetector {

    /**
     * Method to fire a sequence when returning true
     * @return boolean
     */
     boolean isTriggered();

}