package scenes.stage;

import com.github.monpoke.vescape.lights.LightsController;
import com.github.monpoke.vescape.midi.MidiController;
import org.quartz.Scheduler;
import scenes.Scene;

import java.util.logging.Logger;

public abstract class BasicScene implements Scene {
    private static final Logger logger = Logger.getLogger(BasicScene.class.getName());

    private String name;
    private LightsController lightsController;
    private MidiController midiController;
    private Scheduler scheduler;
    private boolean finished = false;

    protected BasicScene(String name){
        this.name=name;
    }

    @Override
    public String getName() {
        return null;
    }


    @Override
    public void play(LightsController lightsController, MidiController midiController, Scheduler scheduler) {

        this.lightsController = lightsController;
        this.midiController = midiController;
        this.scheduler = scheduler;

        while(!finished){
            playScene();
        }
    }

    protected abstract void playScene();

    public void stop(){
        this.finished=true;
    }

    protected Scheduler getScheduler() {
        return scheduler;
    }

    protected LightsController getLightsController() {
        return lightsController;
    }

    protected MidiController getMidiController() {
        return midiController;
    }
}
