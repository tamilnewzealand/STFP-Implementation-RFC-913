package client;

import java.io.*;
import java.net.*;

class Client {

    public static void processResponse(BufferedReader in) throws Exception{
        StringBuilder builder = new StringBuilder();
        char character = (char)in.read();

        while (character != '\0') {
            builder.append(character);
            character = (char)in.read();
    }

        System.out.printf(builder.toString() + "%n");
    }

    public static void main(String argv[]) throws Exception {
        // establish connection to server
        Socket clientSocket = new Socket("localhost", 6789);
        BufferedWriter outToServer =
                new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        BufferedReader inFromServer =
                new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        processResponse(inFromServer);

        String outgoingMessage;
        boolean active = true;
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        while(active) {
            outgoingMessage = inFromUser.readLine();
            try {
                if (outgoingMessage.substring(0,4).equals("DONE")) active = false;
            } catch (IndexOutOfBoundsException e) {
                active = true;
            }
            outgoingMessage = outgoingMessage + "\0";
            outToServer.write(outgoingMessage);
            outToServer.flush();

            processResponse(inFromServer);
        }

        // disconnect from server
        clientSocket.close();
    }
}
