package client;

import java.io.*;
import java.net.*;

class Client {

    private static DataOutputStream outToServer;
    private static BufferedReader inFromServer;

    public static String sendReceive(String message) throws Exception {
        message = message + "\n";
        outToServer.writeBytes(message);
        return inFromServer.readLine();
    }

    public static void main(String argv[]) throws Exception {
        // establish connection to server
        Socket clientSocket = new Socket("localhost", 6789);
        outToServer = new DataOutputStream(clientSocket.getOutputStream());
        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println(inFromServer.readLine());

        String sentence;
        String modifiedSentence;
        boolean active = true;
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        while(active) {
            sentence = inFromUser.readLine();
            if (sentence.substring(0,4).equals("DONE")) active = false;
            modifiedSentence = sendReceive(sentence);
            System.out.println(modifiedSentence);
        }

        // disconnect from server
        clientSocket.close();
    }
}
