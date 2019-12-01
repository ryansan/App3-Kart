package com.example.kart.models;

import java.util.Date;

public class Order {
    private int ID;
    private String name;
    private Date date;

    private String timeFrom;
    private String description;
    private String mail;
    private int Room_ID;

    private int hourFrom;
    private int hourTo;

    public Order(){

    }

    public Order(int ID, String n, String s){
        this.ID = ID;
        name = n;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(String timeFrom) {
        this.timeFrom = timeFrom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getRoom_ID() {
        return Room_ID;
    }

    public void setRoom_ID(int room_ID) {
        Room_ID = room_ID;
    }

    public int getHourFrom() {
        return hourFrom;
    }

    public void setHourFrom(int hourFrom) {
        this.hourFrom = hourFrom;
    }

    public int getHourTo() {
        return hourTo;
    }

    public void setHourTo(int hourTo) {
        this.hourTo = hourTo;
    }
}
