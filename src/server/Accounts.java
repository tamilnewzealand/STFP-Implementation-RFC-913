package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Accounts {
    private ArrayList<String[]> data = new ArrayList<String[]>();
    private String userID = "";
    private String userAccount = "";
    private String userPassword = "";
    private boolean loggedIn = false;

    public boolean setID(String ID) {
        if (!userID.equals("")) {
            loggedIn = false;
            userPassword = "";
            userAccount = "";
        }

        try {
            userID = data.get(Integer.parseInt(ID))[0];
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public boolean setAccount(String account) {
        if (userID.equals("")) {
            return false;
        }

        String[] accounts = data.get(Integer.parseInt(userID))[2].split(",");
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].equals(account)) {
                userAccount = account;
                return true;
            }
        }
        return false;
    }

    public boolean setPassword(String password) {
        if (userID.equals("")) {
            return false;
        }

        if (password.equals(data.get(Integer.parseInt(userID))[1])) {
            loggedIn = true;
            return true;
        }
        else return false;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getID() {
        return userID;
    }

    public boolean inAccount() {
        if (userAccount.equals("")) {
            return false;
        }
        return true;
    }

    public String getAccount() {
        return userAccount;
    }

    public void loadAccounts() throws IOException {
        File f = new File("accounts.tsv");
        BufferedReader b = new BufferedReader(new FileReader(f));
        String readLine = "";
        String[] lines;
        while ((readLine = b.readLine()) != null) {
            lines = readLine.split("\t");
            data.add(lines);
        }
    }

}

