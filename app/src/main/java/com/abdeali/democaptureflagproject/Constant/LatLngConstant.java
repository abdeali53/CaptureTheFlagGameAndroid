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
        latLngs.add(new LatLng(43.772879, -79.337818));
        latLngs.add(new LatLng(43.773410, -79.334821));
        latLngs.add(new LatLng(43.774328, -79.335229));
        latLngs.add(new LatLng(43.773729, -79.338131));
        return latLngs;
    }

    public static ArrayList<LatLng> getprisonALatLong(){
        prisonALatLong.add(new LatLng(43.774192,-79.335084));
        prisonALatLong.add(new LatLng(43.774222,-79.334977));
        prisonALatLong.add(new LatLng(43.774119,-79.334945));
        prisonALatLong.add(new LatLng(43.774100,-79.335052));
        return prisonALatLong;
    }

    public static ArrayList<LatLng> getprisonBLatLong(){
        prisonBLatLong.add(new LatLng(43.773848,-79.334956));
        prisonBLatLong.add(new LatLng(43.773889,-79.334862));
        prisonBLatLong.add(new LatLng(43.773793,-79.334824));
        prisonBLatLong.add(new LatLng(43.773766,-79.334924));
        return prisonBLatLong;
    }

    public static ArrayList<LatLng> getdivideLatLong(){
        divideLatLong.add(new LatLng(43.773331,-79.337992));
        divideLatLong.add(new LatLng(43.773945,-79.335005));

        return divideLatLong;
    }
    public static LatLng getFlagALatLong(){
        return FlagALatLong;
    }

    public static LatLng getFlagBLatLong(){
        return FlagBLatLong;
    }

}
