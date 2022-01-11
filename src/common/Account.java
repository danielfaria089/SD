package common;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Lock;

public class Account {

    private String username;
    private char[] password;
    private boolean admin;//true=admin  false==client
    private Set<String> bookings;
    private Set<String> notifications;

    public Lock l = new ReentrantLock();

    public Account(String u, char[] p, boolean b){
        this.username = u;
        this.password = p.clone();
        this.admin = b;
        this.bookings = new TreeSet<>();
        this.notifications = new TreeSet<>();
    }

    public Account(Account account){
        this.username=account.username;
        this.password=account.password;
        this.admin= account.admin;
        this.bookings =account.getBookingsIds();
        this.notifications = account.getNotifications(false);
    }

    public String getUsername() {
        return username;
    }

    public char[] getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void addBooking(String bookingID){
        bookings.add(bookingID);
    }

    public void removeBooking(String id){
        bookings.remove(id);
    }

    public Set<String> getBookingsIds(){
        return new TreeSet<>(bookings);
    }

    public Account clone(){
        return new Account(this);
    }

    public void adicionarNotificacao(String notf){
        notifications.add(notf);
    }

    public Set<String> getNotifications(boolean clear){
        Set<String> res = new TreeSet<>(notifications);
        if(clear)
            notifications.clear();
        return res;
    }
}
