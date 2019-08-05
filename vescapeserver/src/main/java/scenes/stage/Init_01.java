package scenes.stage;

import io.github.zeroone3010.yahueapi.Light;
import io.github.zeroone3010.yahueapi.Room;
import io.github.zeroone3010.yahueapi.State;
import org.quartz.*;
import org.quartz.core.jmx.SimpleTriggerSupport;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;
import su.litvak.chromecast.api.v2.*;

import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class Init_01 extends BasicScene {
    private static final Logger logger = Logger.getLogger(Init_01.class.getName());
    private Scheduler scheduler;
    private Room zonePC;
    private Room salon;

    public Init_01() {
        super(Init_01.class.getName());
    }

    @Override
    protected void playScene() {

        try {


            createChromecast();


            Thread.sleep(1000 * 60 * 10);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void createChromecast() throws IOException, GeneralSecurityException {
        logger.info("Creating chromecast...");

        org.slf4j.Logger logger = LoggerFactory.getLogger("su.litvak.chromecast.api.v2");


        try {
            ChromeCast chromeCast = new ChromeCast("192.168.1.13");
            chromeCast.connect();

            Status status = chromeCast.getStatus();

            String APP_ID = "D4E213C3";

            // Run application if it's not already running
            if (chromeCast.isAppAvailable(APP_ID) && !status.isAppRunning(APP_ID)) {
                Application app = chromeCast.launchApp(APP_ID);
            }

            startCast(chromeCast);


            chromeCast.registerListener(new Warning(chromeCast));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

    }

    /**
     * Warning timecode...
     */
    class Warning implements ChromeCastSpontaneousEventListener {
        private ChromeCast chromeCast;

        public Warning(ChromeCast chromeCast) {

            this.chromeCast = chromeCast;
        }

        @Override
        public void spontaneousEventReceived(ChromeCastSpontaneousEvent event) {
            Object data = event.getData();
            MediaStatus dat = (MediaStatus) data;
            System.out.println(dat.playerState.toString());
            switch (dat.playerState) {
                case PLAYING:
                    // starting...
                    break;

                case IDLE:
                    // finished...
                    try {
                        chromeCast.unregisterListener(this);
                        chromeCast.load("http://central.pierrebourgeois.fr/black.png");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;


            }
        }

    }


    private void startCast(ChromeCast chromeCast) {
        // play media URL with additional parameters, such as media title and thumbnail image
        try {
            chromeCast.load("",           // Media title
                    "images/BigBuckBunny.jpg",  // URL to thumbnail based on media URL
                    "http://central.pierrebourgeois.fr/creepy.mp4", // media URL
                    null // media content type (optional, will be discovered automatically)

            );

            createAmbientLights();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private void createAmbientLights() throws SchedulerException {

        zonePC = getLightsController().getHue().getZoneByName("TV").get();
        salon = getLightsController().getHue().getRoomByName("Salon").get();
        salon.getLights().forEach(light -> {
            light.turnOff();
        });

        getLightsController().getHue().setCaching(true);


        JobDetail jobDetail = JobBuilder.newJob(AmbiantLights.class)
                .usingJobData("eventId", 1)
                .build();


        System.out.println("Trying to create job...");
        SimpleTrigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1")
                .startAt(new Date(Calendar.getInstance().getTimeInMillis() + 5000))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(2).repeatForever())
                .build();

        Date date = getScheduler().scheduleJob(jobDetail, trigger);
        getScheduler().start();
        System.out.println(date);


    }

    private class AmbiantLights implements Job {


        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

            System.out.println("Executing ambiant lights...");
            System.out.println(jobExecutionContext.getMergedJobDataMap().getInt("eventId"));

            Collection<Light> lights = zonePC.getLights();
            lights.forEach(light -> {
                light.turnOn();
                light.setState(State.builder().color(new Color(255, 0, 0)).keepCurrentState());
            });


            lights.forEach(light -> {
                light.setBrightness(80);
            });


            Collection<Light> salonLights = salon.getLights();
            salonLights.forEach(light -> {
                light.turnOn();
                light.setState(State.builder().color(new Color(255, 255, 255)).keepCurrentState());
            });
        }
    }

}
