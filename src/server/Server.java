package server;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import server.*;

import javax.swing.*;

class Server {

    public static String getResponse(BufferedReader in) throws Exception{
        StringBuilder builder = new StringBuilder();
        char character = (char)in.read();

        while (character != '\0') {
            builder.append(character);
            character = (char)in.read();
        }

        return builder.toString();
    }

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
            BufferedWriter outToClient =
                    new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream()));

            outToClient.write("+" + serverName + " SFTP Service\0");
            outToClient.flush();

            Accounts acc = new Accounts();
            acc.loadAccounts();
            active = true;
            int failedAttempts = 0;
            String transferType = "B";
            String currentDir = "/";
            ArrayList<String> dirStructure = new ArrayList<String>();

            while (active) {
                clientMessage = getResponse(inFromClient);
                try {
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
                        if (acc.isLoggedIn()) {
                            switch (clientMessage) {
                                case "A":
                                    responseMessage = "+Using Ascii mode";
                                    transferType = "A";
                                    break;
                                case "B":
                                    responseMessage = "+Using Binary mode";
                                    transferType = "B";
                                    break;
                                case "C":
                                    responseMessage = "+Using Continuous mode";
                                    transferType = "C";
                                    break;
                                default:
                                    responseMessage = "-Type not valid";
                                    break;
                            }
                        } else {
                            responseMessage = "-Not logged in, please log in";
                            failedAttempts++;
                        }
                        break;
                    case "LIST":
                            if (acc.isLoggedIn()) {
                                if (acc.inAccount()) {
                                    String format;
                                    try {
                                        format = clientMessage.substring(0,1);
                                    } catch (IndexOutOfBoundsException e) {
                                        format = "F";
                                    }
                                    try {
                                        responseMessage = "+" + clientMessage + "\r\n";
                                        responseMessage = responseMessage + FileAccess.getFileList(Paths.get("host", acc.getAccount(), clientMessage.substring(2)).toString(), format);
                                    } catch (IndexOutOfBoundsException e) {
                                        responseMessage = "+" + currentDir + "\r\n";
                                        responseMessage = responseMessage + FileAccess.getFileList(Paths.get("host", acc.getAccount(), currentDir).toString(), format);
                                    }
                                } else {
                                    responseMessage = "-Invalid account, try again";
                                }
                            } else {
                                responseMessage = "-Not logged in, please log in";
                                failedAttempts++;
                            }
                        break;
                    case "CDIR":
                        if (acc.isLoggedIn()) {
                            if (acc.inAccount()) {
                                try {
                                    String newDir;
                                    if (currentDir.equals("/")) {
                                        newDir = clientMessage;
                                    } else {
                                        newDir = Paths.get(currentDir, clientMessage).toString();
                                    }
                                    if (clientMessage.equals(".")) {
                                        newDir = currentDir;
                                    }
                                    if (clientMessage.equals("..")) {
                                        if (dirStructure.size() != 0) {
                                            newDir = "";
                                            for (int i = 0; i < dirStructure.size() - 1; i++) {
                                                newDir = Paths.get(newDir, dirStructure.get(i)).toString();
                                            }
                                        }
                                    }
                                    if (FileAccess.checkDirectoryExists(Paths.get("host", acc.getAccount(), newDir).toString())) {
                                        if (clientMessage.equals("..")) {
                                            dirStructure.remove(dirStructure.size()-1);
                                        } else {
                                            dirStructure.add(clientMessage);
                                        }
                                        currentDir = newDir; // FIX: need to consider all the path options supplied and how to work with them
                                        responseMessage = "!Changed working dir to " + clientMessage;
                                    } else {
                                        responseMessage = "-Can't connect to directory because: directory doesn't exist";
                                    }
                                } catch (Exception e) {
                                    responseMessage = "-Invalid command, try again";
                                }
                            } else {
                                responseMessage = "-Invalid account, try again";
                            }
                        } else {
                            responseMessage = "-Not logged in, please log in";
                            failedAttempts++;
                        }
                        break;
                    case "KILL":
                        if (acc.isLoggedIn()) {
                            if (acc.inAccount()) {
                                try {
                                    String path;
                                    if (currentDir.equals("/")) {
                                        path = Paths.get("host", acc.getAccount(), clientMessage).toString();
                                    } else {
                                        path = Paths.get("host", acc.getAccount(), currentDir, clientMessage).toString();
                                    }
                                    if (FileAccess.deleteFile(path)) {
                                        responseMessage = "+" + clientMessage + " deleted";
                                    } else {
                                        responseMessage = "-Not deleted because file locked";
                                    }
                                } catch (IndexOutOfBoundsException e) {
                                    responseMessage = "-Invalid command, try again";
                                }
                            } else {
                                responseMessage = "-Invalid account, try again";
                            }
                        } else {
                            responseMessage = "-Not logged in, please log in";
                            failedAttempts++;
                        }
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
                } catch (Exception e) {
                    responseMessage = "-Unknown Server Error";
                }

                responseMessage = responseMessage + "\0";
                outToClient.write(responseMessage);
                outToClient.flush();
            }

            connectionSocket.close();
        }
    }
}