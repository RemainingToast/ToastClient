package toast.client.utils;

import java.util.ArrayList;

public class RandomMOTD {

    public static ArrayList<String> MOTDS = new ArrayList<String>() {};
    static int size;

    public static void addMOTDS(){
        MOTDS.add("RemainingToast on Top");
        MOTDS.add("Wnuke nuked the client");
        MOTDS.add("Meteor who?");
        MOTDS.add("Toast > Meteor");
        MOTDS.add("Backdoored, your coordinates are 21412 73 42142");
        MOTDS.add("Fleebs did math again");
        MOTDS.add("Qther is probably out of bounds");
        MOTDS.add("Dewy doing dewy stuff");

        size = MOTDS.size();
    }

    public static String randomMOTD(){
        int Random = (int)(Math.random()*size);
        return MOTDS.get(Random);
    }
}
