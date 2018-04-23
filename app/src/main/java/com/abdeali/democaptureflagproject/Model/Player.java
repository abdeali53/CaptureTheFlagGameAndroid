package com.abdeali.democaptureflagproject.Model;

/**
 * Created by Abdeali on 16-04-2018.
 */

public class Player {
    public String name;
    public double latitude;
    public double longitude;
    public String teamName;
    public Boolean isCaught;
    public Player(String name, double latitude, double longitude, String teamName, boolean isCaught) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.teamName = teamName;
        this.isCaught = isCaught;
    }
    Player(){

    }
}
