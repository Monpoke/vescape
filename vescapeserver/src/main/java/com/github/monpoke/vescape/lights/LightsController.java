package com.github.monpoke.vescape.lights;

import io.github.zeroone3010.yahueapi.Hue;

import java.util.logging.Logger;

public class LightsController implements Runnable {
    private static final Logger logger = Logger.getLogger(LightsController.class.getName());
    private Hue hue;

    private boolean isReady = false;

    @Override
    public void run() {
        Init init = new Init();
        hue = init.init();
        isReady=true;
    }

    public Hue getHue() {
        return hue;
    }


    public boolean isReady() {
        return isReady;
    }
}
