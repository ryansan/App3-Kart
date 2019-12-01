package com.example.kart.models;

import java.util.ArrayList;

public class Room {

    private int ID;
    private String name;
    private String description;
    private int floor;
    private int Building_ID;

    private ArrayList<Order> orders = new ArrayList<>();

    public Room(){
    }


    public Room(int id, String name){
        this.ID = id;
        this.name = name;
        orders = new ArrayList<>();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getBuilding_ID() {
        return Building_ID;
    }

    public void setBuilding_ID(int building_ID) {
        Building_ID = building_ID;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }
}
