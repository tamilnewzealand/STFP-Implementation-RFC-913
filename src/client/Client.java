package client;

import java.io.*;
import java.net.*;
import java.nio.file.Paths;

class Client {

    public static void processResponse(BufferedReader in) throws Exception {
        StringBuilder builder = new StringBuilder();
        char character = (char)in.read();

        while (character != '\0') {
            builder.append(character);
            character = (char)in.read();
    }

        System.out.printf(builder.toString() + "%n");
    }

    public static void saveFile(BufferedReader inFromServer, BufferedReader inFromUser, String transferType) throws Exception {
        System.out.println("Please specify name to save file to:");
        String fileName = inFromUser.readLine();
        fileName = Paths.get("downloads", fileName).toString();
        File file = new File(fileName);
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        if (transferType.equals("A")) {
            char character = (char)inFromServer.read();

            while (character != '\0') {
                StringBuilder builder = new StringBuilder();
                while (character != '\r') {
                    builder.append(character);
                    character = (char)inFromServer.read();
                }
                character = (char)inFromServer.read();
                character = (char)inFromServer.read();
                bw.write(builder.toString());
            }
            bw.close();
        }
        return;
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
        boolean fileReceive = false;
        String transferType = "B";
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        while(active) {
            outgoingMessage = inFromUser.readLine();
            try {
                if (outgoingMessage.substring(0,4).equals("DONE")) active = false;
                if (outgoingMessage.substring(0,4).equals("SEND")) fileReceive = true;
                if (outgoingMessage.substring(0,4).equals("TYPE")) transferType = outgoingMessage.substring(5);
            } catch (IndexOutOfBoundsException e) {
                active = true;
                fileReceive = false;
            }
            outgoingMessage = outgoingMessage + "\0";
            outToServer.write(outgoingMessage);
            outToServer.flush();

            if (fileReceive) saveFile(inFromServer, inFromUser, transferType);
            else processResponse(inFromServer);
        }

        // disconnect from server
        clientSocket.close();
    }
}
