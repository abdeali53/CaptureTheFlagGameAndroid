package com.abdeali.democaptureflagproject.Constant;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Abdeali on 23-04-2018.
 */

public class LatLngConstant {
    private static ArrayList<LatLng> latLngs = new ArrayList<LatLng>();
    private static ArrayList<LatLng> prisonALatLong = new ArrayList<>();
    private static ArrayList<LatLng> prisonBLatLong = new ArrayList<>();
    private static ArrayList<LatLng> divideLatLong = new ArrayList<>();
    //Flag A === Canada
    private static LatLng FlagALatLong = new LatLng(43.774269,-79.335246);
    //Flag B === IND
    private static LatLng FlagBLatLong = new LatLng(43.773521,-79.334919);
    public static ArrayList<LatLng> getGameFieldLatLngs(){
        latLngs.add(new LatLng(43.773367, -79.334998));
        latLngs.add(new LatLng(43.773410, -79.334821));
        latLngs.add(new LatLng(43.774328, -79.335229));
        latLngs.add(new LatLng(43.774290, -79.335379));
        return latLngs;
    }
    public static ArrayList<LatLng> getprisonALatLong(){
        prisonALatLong.add(new LatLng(43.773367, -79.334998));
        prisonALatLong.add(new LatLng(43.773410, -79.334821));
        prisonALatLong.add(new LatLng(43.774328, -79.335229));
        prisonALatLong.add(new LatLng(43.774290, -79.335379));
        return prisonALatLong;
    }

    public static ArrayList<LatLng> getprisonBLatLong(){
        prisonBLatLong.add(new LatLng(43.773367, -79.334998));
        prisonBLatLong.add(new LatLng(43.773410, -79.334821));
        prisonBLatLong.add(new LatLng(43.774328, -79.335229));
        prisonBLatLong.add(new LatLng(43.774290, -79.335379));
        return prisonBLatLong;
    }

    public static ArrayList<LatLng> getdivideLatLong(){
        divideLatLong.add(new LatLng(43.773367, -79.334998));
        divideLatLong.add(new LatLng(43.773410, -79.334821));

        return divideLatLong;
    }

    public static LatLng getFlagALatLong(){
        return FlagALatLong;
    }

    public static LatLng getFlagBLatLong(){
        return FlagBLatLong;
    }

}
