package scenes;

import com.github.monpoke.vescape.lights.LightsController;
import com.github.monpoke.vescape.midi.MidiController;
import org.quartz.Scheduler;

public interface Scene {

    String getName();

    void play(LightsController lightsController, MidiController midiController, Scheduler scheduler);
}
