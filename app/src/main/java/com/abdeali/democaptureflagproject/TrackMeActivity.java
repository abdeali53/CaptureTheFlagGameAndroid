package com.abdeali.democaptureflagproject;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.abdeali.democaptureflagproject.Model.Player;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;


public class TrackMeActivity extends AppCompatActivity implements OnMapReadyCallback{
    private static final String TAG = "TrackMeActivity";
    //Firebase Related Object
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ChildEventListener childEventListener;
    ValueEventListener valueEventListener;
// Flag A
    LatLng FlagALatLong = new LatLng(43.774269,-79.335246);
    //Flag B
    LatLng FlagBLatLong = new LatLng(43.773521,-79.334919);
    //Google MAp
    ArrayList<Marker> markers = new ArrayList<>();
    private GoogleApiClient googleApiClient;
    protected LocationManager locationManager;
    private LocationListener locationListener;
    public GoogleMap googleMap;
    private String PlayerReferenceId;

    private ArrayList<LatLng> latLngs = new ArrayList<LatLng>();
    private ArrayList<LatLng> prisonALatLong = new ArrayList<>();
    private ArrayList<LatLng> prisonBLatLong = new ArrayList<>();
    private ArrayList<LatLng> divideLatLong = new ArrayList<>();

//        {add(new LatLng(43.773911, -79.335212));}
//        add(new LatLng(43.773911, -79.335212))
//        new LatLng(43.773957, -79.335044);
//        new LatLng(43.773535, -79.334875);
//        new LatLng(43.773521, -79.335065);
//    };
    private TextView tvLatitude,tvLongitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_me);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        //Field LatLong
        latLngs.add(new LatLng(43.773367, -79.334998));
        latLngs.add(new LatLng(43.773410, -79.334821));
        latLngs.add(new LatLng(43.774328, -79.335229));
        latLngs.add(new LatLng(43.774290, -79.335379));

        //Divide Team LatLong
        divideLatLong.add(new LatLng(43.773879,-79.335226));
        divideLatLong.add(new LatLng(43.773945,-79.335005));
        //Team A Prison LatLong
        prisonALatLong.add(new LatLng(43.774192,-79.335084));
        prisonALatLong.add(new LatLng(43.774222,-79.334977));
        prisonALatLong.add(new LatLng(43.774119,-79.334945));
        prisonALatLong.add(new LatLng(43.774100,-79.335052));
        //43.774100 , -79.335052
        //Team B Prison LatLong
        prisonBLatLong.add(new LatLng(43.773848,-79.334956));
        prisonBLatLong.add(new LatLng(43.773889,-79.334862));
        prisonBLatLong.add(new LatLng(43.773793,-79.334824));
        prisonBLatLong.add(new LatLng(43.773766,-79.334924));

        initMap();
        //Get Register Player Reference Id
      tvLatitude.setText(Double.toString(getIntent().getDoubleExtra("PlayerLatitude",0)));
        tvLongitude.setText(Double.toString(getIntent().getDoubleExtra("PlayerLongitude",0)));
//        tvLongitude.setText(getIntent().getDoubleExtra("PlayerLongitude"));
        PlayerReferenceId = getIntent().getStringExtra("PlayerReferenceId");
        Log.d(TAG, "onCreate: ======================PlayerReferenceId======================== " + PlayerReferenceId);
        Log.d(TAG, "onCreate: "+ PlayerReferenceId);
        // Firebase Database Object Initialization
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        //User Location Detection
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},9002);

        }
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location != null)
                {
                    boolean isCaught = false;
                    isCaught = isPlayerCaught(new LatLng(location.getLatitude(),location.getLongitude()),latLngs);
                    Log.d(TAG, "onLocationChanged: ============================Change Detected=============");
                    tvLatitude.setText("Lat " + location.getLatitude());
                    tvLongitude.setText("Long " + location.getLongitude());
                    DatabaseReference updatePlayerLocation = databaseReference.child("Player").child(PlayerReferenceId);

                    updatePlayerLocation.child("latitude").setValue(location.getLatitude());
                    updatePlayerLocation.child("longitude").setValue(location.getLongitude());
                    updatePlayerLocation.child("isCaught").setValue(isCaught);

                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (Build.VERSION.SDK_INT < 23) {

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, locationListener);

        } else {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // ask for permission

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);


            } else {

                // we have permission!

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, locationListener);

            }

        }
        getUpdateOnMap();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, locationListener);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, locationListener);
        }
    }

    public void getUpdateOnMap(){
//        = null;
        if(valueEventListener  == null){
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Player> players =  new ArrayList<>();
//                    Log.d(TAG, "onChildAdded: ==================PLayer Update============= "+ dataSnapshot.getValue().toString());
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Log.d(TAG, "onDataChange: ==================Player Object To String========"+snapshot.getValue().toString());
                        Player player = snapshot.getValue(Player.class);
                        players.add(player);
                        Log.d(TAG, "Name: "+player.teamName);
//                        Log.d(TAG, "Latitude: "+player.latitude);
//                        Log.d(TAG, "Longitude: "+player.longitude);
//                        Log.d(TAG, "Team Name: "+player.teamName);
                    }
                    setPlayerMaker(players);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            databaseReference.child("Player").addValueEventListener(valueEventListener);

        }
    }

    private void initMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_LONG).show();
        this.googleMap = googleMap;
        this.googleMap.setMaxZoomPreference(20f);
        this.googleMap.addMarker(new MarkerOptions().position(FlagALatLong).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_flag_a)));
        this.googleMap.addMarker(new MarkerOptions().position(FlagBLatLong).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_flag_b)));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.773535, -79.334875),19.5f));
        Polygon polygon = this.googleMap.addPolygon(new PolygonOptions()
                .addAll(latLngs)
                .strokeColor(Color.RED)
                .fillColor(Color.parseColor("#51000000")).strokeWidth(2));
        //polygon to Divide

        Polygon polygonPrisonA = this.googleMap.addPolygon(new PolygonOptions()
                .addAll(prisonALatLong)
                .strokeColor(Color.parseColor("#9CDD87"))
                .strokeWidth(2));
        Polyline polygondivide = this.googleMap.addPolyline(new PolylineOptions().addAll(divideLatLong).color(Color.GREEN).width(2f));

//        EAA0E1

        Polygon polygonPrisonB = this.googleMap.addPolygon(new PolygonOptions()
                .addAll(prisonBLatLong)
                .strokeColor(Color.parseColor("#EAA0E1"))
                .strokeWidth(2));
    }

    //Mark All Player on Map
    public void setPlayerMaker(List<Player> players){
        if(players.size() != 0) {
//            googleMap.clear();
            if(markers.size() != 0){
                for (Marker maker: markers) {
                    maker.remove();
                }
            }
            for (Player player : players) {

                LatLng latLng = new LatLng(player.latitude,player.longitude);
                if(player.isCaught){
                    LatLng prisonLatLong;
                    if(player.teamName.equals("Canada")){
                        prisonLatLong = new LatLng(43.773870,-79.334855);
                    }
                    else{
                          prisonLatLong = new LatLng(43.774180, -79.335064);
                    }
                    markers.add(googleMap.addMarker(new MarkerOptions()
                            .position(prisonLatLong)
                            .title(player.name)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(player.teamName.equals("Canada") ? R.drawable.ic_action_name
                                            : R.drawable.ic_action_name1))));
                }
                else {
                    markers.add(googleMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(player.name)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(player.teamName.equals("Canada") ? R.drawable.ic_action_name
                                            : R.drawable.ic_action_name1))));
                }
            }
        }
    }
    public void setWinner(List<Player> players){

        List<Player> prisonAPlayers = new ArrayList<>();
        List<Player> prisonBPlayers = new ArrayList<>();
        for (Player player:
            players ) {
            if(player.isCaught.equals(true) && player.teamName.equals("Canada")){
                prisonAPlayers.add(player);
            }
            else{
                prisonBPlayers.add(player);
            }
            if(prisonAPlayers.size() == 5){

            }
            if(prisonBPlayers.size() == 5){

            }
        }
    }

    public boolean isPlayerCaught(LatLng userLocation, List<LatLng> polyPointsList){
        return !PolyUtil.containsLocation(userLocation, polyPointsList,false);

    }
}
