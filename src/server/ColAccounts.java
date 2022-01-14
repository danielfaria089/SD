package server;

import common.Account;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ColAccounts {
    private Map<String, Account> accounts;
    private Map<String, Account> admins;

    private ReadWriteLock l = new ReentrantReadWriteLock();
    private Lock l_r = l.readLock();
    public Lock l_w = l.writeLock();

    public ColAccounts(){
        this.accounts = new HashMap<>();
        this.admins = new HashMap<>();
    }

    public ColAccounts(String filename) throws IOException {
        this.accounts = new HashMap<>();
        this.admins = new HashMap<>();
        readAccounts(filename);
    }

    private void readAccounts(String filename) throws IOException {
        BufferedReader reader = new BufferedReader((new FileReader(filename)));
        String line;
        while ((line = reader.readLine())!=null){
            String[] strings = line.split(";");
            boolean b = Boolean.parseBoolean(strings[2]);
            Account a = new Account(strings[0],strings[1].toCharArray(),b);
            for(int i = 3; i<strings.length; i++){
                a.addBooking(strings[i]);
            }
            if(b) admins.put(a.getUsername(),a);
            else accounts.put(a.getUsername(),a);
        }
    }

    public void writeAccounts(String filename) throws IOException {
        PrintWriter writer = new PrintWriter((new FileWriter(filename)));
        for(Account a : accounts.values()){
            List<String> strings = new ArrayList<>();
            for(String s : a.getBookingsIds()){
                strings.add(s);
                strings.add(";");
            }
            writer.println(a.getUsername() + ";" +
                    Arrays.toString(a.getPassword()) + ";" +
                    a.isAdmin() + ";" +
                    strings) ;
        }
        writer.flush();
        writer.close();
    }

    public List<Account> getAllAccounts(){
        List<Account> acc = new ArrayList<>();
        for(Account a : accounts.values()){
            acc.add(a.clone());
        }
        for(Account a : admins.values()){
            acc.add(a.clone());
        }
        return acc;
    }

    public List<Account> getAllClients(){
        List<Account> acc = new ArrayList<>();
        for(Account a : accounts.values()){
            acc.add(a.clone());
        }
        return acc;
    }

    public List<Account> getAllAdmins(){
        List<Account> acc = new ArrayList<>();
        for(Account a : admins.values()){
            acc.add(a.clone());
        }
        return acc;
    }
}
