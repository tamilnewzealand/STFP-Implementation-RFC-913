package server;

import java.io.*;
import java.net.*;
import server.*;

class Server {

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

            Accounts acc = new Accounts();
            acc.loadAccounts();
            int failedAttempts = 0;

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
                        if (acc.isLoggedIn()) {
                            responseMessage = "!" + acc.getID() + " logged in";
                        }
                        else if (acc.setID(clientMessage)) {
                            responseMessage = "+User-id valid, send account and password";
                        } else {
                            responseMessage = "-Invalid user-id, try again";
                            failedAttempts++;
                        }
                        break;
                    case "ACCT":
                        if (acc.setAccount(clientMessage)) {
                            if (acc.isLoggedIn()) {
                                responseMessage = "! Account valid, logged-in";
                            } else {
                                responseMessage = "+Account valid, send password";
                            }
                        } else {
                            responseMessage = "-Invalid account, try again";
                            failedAttempts++;
                        }
                        break;
                    case "PASS":
                        if (acc.setPassword(clientMessage)) {
                            if (acc.inAccount()) {
                                responseMessage = "! Logged in";
                            } else {
                                responseMessage = "+Send account";
                            }
                        } else {
                            responseMessage = "-Wrong password, try again";
                            failedAttempts++;
                        }
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

                if (failedAttempts > 3) {
                    responseMessage = "-Too many failed attempts, closing connection";
                    active = false;
                }

                responseMessage = responseMessage + "\n";
                outToClient.writeBytes(responseMessage);
            }
        }
    }
}