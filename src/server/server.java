package server;

import java.io.*;
import java.net.*;

class server {

    public static void main(String argv[]) throws Exception {
        String clientMessage;
        String responseMessage;
        String clientCommand;
        String serverName = "MyServer";
        boolean active = true;
        ServerSocket welcomeSocket = new ServerSocket(6789);

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

            outToClient.writeBytes("+" + serverName + " SFTP Service\n");

            while (active) {
                clientMessage = inFromClient.readLine();
                clientCommand = clientMessage.substring(0, 4);
                try {
                    clientMessage = clientMessage.substring(5);
                } catch (IndexOutOfBoundsException e) {
                    clientMessage = "\0";
                }

                switch (clientCommand) {
                    case "USER":
                        responseMessage = "-";
                        break;
                    case "ACCT":
                        responseMessage = "-";
                        break;
                    case "PASS":
                        responseMessage = "-";
                        break;
                    case "TYPE":
                        responseMessage = "-";
                        break;
                    case "LIST":
                        responseMessage = "-";
                        break;
                    case "CDIR":
                        responseMessage = "-";
                        break;
                    case "KILL":
                        responseMessage = "-";
                        break;
                    case "NAME":
                        responseMessage = "-";
                        break;
                    case "DONE":
                        responseMessage = "+" + serverName + " closing connection";
                        active = false;
                        break;
                    case "RETR":
                        responseMessage = "-";
                        break;
                    case "STOR":
                        responseMessage = "-";
                        break;
                    default:
                        responseMessage = "-Unknown Command";
                        break;
                }

                responseMessage = responseMessage + "\n";
                outToClient.writeBytes(responseMessage);
            }
        }
    }
}