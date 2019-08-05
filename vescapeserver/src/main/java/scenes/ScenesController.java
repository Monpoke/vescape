package scenes;

import com.github.monpoke.vescape.lights.Init;
import com.github.monpoke.vescape.lights.LightsController;
import com.github.monpoke.vescape.midi.MidiController;
import org.quartz.Scheduler;
import scenes.stage.Init_01;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ScenesController implements Runnable {

    private static final Logger logger = Logger.getLogger(ScenesController.class.getName());

    private LightsController lightsController = new LightsController();
    private MidiController midiController = new MidiController();
    private Thread midiThread;
    private Thread lightsThread;
    private boolean finished = false;
    private Scheduler scheduler;

    public ScenesController(Scheduler scheduler) {

        this.scheduler = scheduler;
    }

    @Override
    public void run() {
        logger.info("Starting Scenes controller!");

        // Just some inits
        initThreads();
        List<Scene> scenes = initScenes();

        // Lets start...
        startSystem(scenes);

    }

    /**
     * Starts the scene system...
     * @param scenes
     */
    private void startSystem(List<Scene> scenes) {

        int CURRENT_SCENE_ID = 0;

        while(!finished){

            play(scenes.get(CURRENT_SCENE_ID));

        }




    }

    private void play(Scene scene) {

        logger.info("Playing scene...");

        scene.play(lightsController, midiController, scheduler);

    }


    /**
     * Starts some threads...
     */
    private void initThreads() {
        // start lights controller
        lightsThread = new Thread(lightsController);
        lightsThread.start();


        // start midi controller
        midiThread = new Thread(midiController);
        midiThread.start();

        // wait for readyness...
        int i =0;
        while(!lightsController.isReady() || !midiController.isReady()){

            try {
                logger.info("Waiting for readyness of threads...");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }


    /**
     * Start to init scenes...
     */
    private List<Scene>  initScenes() {

        List<Scene> scenes = new ArrayList<>();
        scenes.add(new Init_01());

        return scenes;
    }
}
