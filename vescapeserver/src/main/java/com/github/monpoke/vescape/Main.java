package com.github.monpoke.vescape;

import com.github.monpoke.vescape.lights.LightsController;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;
import scenes.ScenesController;

import java.io.IOException;

public class Main {

    private static Scheduler scheduler;

    public static void main(String[] args) throws SchedulerException {


        // Grab the Scheduler instance from the Factory
        scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();


        Thread scenesController = new Thread(new ScenesController(scheduler));
        scenesController.start();


        System.out.println("Press any key to leave...");
        try {
            int read = System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        scheduler.shutdown();
        scenesController.interrupt();

    }
}
