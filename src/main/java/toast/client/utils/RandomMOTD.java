package toast.client.utils;

import java.util.ArrayList;

public class RandomMOTD {

    public static ArrayList<String> MOTDList = new ArrayList<String>() {
    };
    static int size;

    public static void addMOTDS() {

        MOTDList.add("RemainingToast on Top");
        MOTDList.add("Wnuke nuked the client");
        MOTDList.add("Meteor who?");
        MOTDList.add("Toast > Meteor");
        MOTDList.add("Backdoored, your coordinates are 21412 73 42142");
        MOTDList.add("Fleebs did math again");
        MOTDList.add("Qther is probably out of bounds");
        MOTDList.add("Dewy doing dewy stuff");
        MOTDList.add("RemainingToast actually does code");
        MOTDList.add("Axo is the anime cum god");

        size = MOTDList.size();
    }

    public static String randomMOTD() {
        int Random = (int) (Math.random() * size);
        return MOTDList.get(Random);
    }
}
