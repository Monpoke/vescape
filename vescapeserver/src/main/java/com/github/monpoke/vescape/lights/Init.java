package com.github.monpoke.vescape.lights;

import io.github.zeroone3010.yahueapi.Hue;

import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class Init {

    private static final Logger logger = Logger.getLogger(Init.class.getName());


    public Hue init(){

        try {
            Hue hue = getHue("192.168.1.36", "VEscape");

            logger.info("Lights system Connected...");

            return hue;


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



    private static Hue getHue(String bridgeIp, String appName) throws Exception {

        Hue hue;

        File file = new File(".", "token.txt");

        if (!file.isFile()) {

            final java.util.concurrent.CompletableFuture<String> apiKey = Hue.hueBridgeConnectionBuilder(bridgeIp).initializeApiConnection(appName);
            // Push the button on your Hue Bridge to resolve the apiKey future:
            String key = apiKey.get();

            file.createNewFile();

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(key);

            bufferedWriter.close();
        }

        BufferedReader r = new BufferedReader(new FileReader(file));

        try {
            // read data
            String apiKey = r.readLine();
            if (!apiKey.isEmpty()) {
                hue = new Hue(bridgeIp, apiKey);
            } else {
                throw new Exception("TokenEmpty...");
            }
        } finally {
            r.close();
        }


        return hue;
    }

}
