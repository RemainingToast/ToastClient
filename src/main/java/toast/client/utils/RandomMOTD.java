package toast.client.utils;

import javafx.scene.effect.MotionBlur;

import java.util.ArrayList;
import java.util.Random;

public class RandomMOTD {

    public static ArrayList<String> MOTDS = new ArrayList<String>() {};
    static int size;

    public static void addMOTDS(){
        MOTDS.add("RemainingToast on Top");
        MOTDS.add("Wnuke nuked the client");
        MOTDS.add("Meteor who?");

        size = MOTDS.size();
    }

    public static String randomMOTD(){
        int Random = (int)(Math.random()*size);
        return MOTDS.get(Random);
    }
}
