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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.abdeali.democaptureflagproject.Constant.LatLngConstant;
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
    DatabaseReference databaseReference;
    ChildEventListener childEventListener;
    ValueEventListener valueEventListener;

    ArrayList<Marker> markers = new ArrayList<>();
    private GoogleApiClient googleApiClient;
    protected LocationManager locationManager;
    private LocationListener locationListener;
    public GoogleMap googleMap;
    private String PlayerReferenceId;

    private TextView tvGameStatus;
    private Button btnStartGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_me);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        tvGameStatus = findViewById(R.id.tvGameStatus);
        btnStartGame = findViewById(R.id.btnStartGame);
        initMap();
        getUpdateOnMap();
        valueListnerOnGame();
    }


    public void getUpdateOnMap(){
//        = null;
        if(valueEventListener  == null){
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    List<Player> players =  new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        Player player = snapshot.getValue(Player.class);
                        players.add(player);
                        Log.d(TAG, "Name: "+player.teamName);
                    }
                    setPlayerMaker(players);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        databaseReference.child("Player").addValueEventListener(valueEventListener);
    }

    public void valueListnerOnGame(){
        databaseReference.child("Game").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("isStart").getValue(Boolean.class)){
                    btnStartGame.setVisibility(View.INVISIBLE);
                    btnStartGame.setBackgroundColor(Color.GREEN);
                    btnStartGame.setEnabled(true);
                    tvGameStatus.setText(
                            dataSnapshot.child("winner").getValue(String.class).isEmpty() ? "Game is in Progress" : "Team "
                                    + dataSnapshot.child("winner").getValue(String.class) +" won."
                    );
                }
                else{
                    btnStartGame.setEnabled(false);
                    tvGameStatus.setText("Game Not Started.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        this.googleMap.addMarker(new MarkerOptions().position(LatLngConstant.getFlagALatLong()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_flag_a)));
        this.googleMap.addMarker(new MarkerOptions().position(LatLngConstant.getFlagBLatLong()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_flag_b)));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(43.773535, -79.334875),19.5f));
        Polygon polygon = this.googleMap.addPolygon(new PolygonOptions()
                .addAll(LatLngConstant.getGameFieldLatLngs())
                .strokeColor(Color.RED)
                .fillColor(Color.parseColor("#51000000")).strokeWidth(2));
        //polygon to Divide

        Polygon polygonPrisonA = this.googleMap.addPolygon(new PolygonOptions()
                .addAll(LatLngConstant.getprisonALatLong())
                .strokeColor(Color.parseColor("#9CDD87"))
                .strokeWidth(2));
        Polyline polygondivide = this.googleMap.addPolyline(new PolylineOptions().addAll(LatLngConstant.getdivideLatLong()).color(Color.GREEN).width(2f));

//        EAA0E1

        Polygon polygonPrisonB = this.googleMap.addPolygon(new PolygonOptions()
                .addAll(LatLngConstant.getprisonBLatLong())
                .strokeColor(Color.parseColor("#EAA0E1"))
                .strokeWidth(2));
    }

    //Mark All Player on Map
    public void setPlayerMaker(List<Player> players){
        List<Player> teamAPlayers = new ArrayList<>();
        List<Player> teamBPlayers = new ArrayList<>();

        if(players.size() != 0) {
            if(markers.size() != 0){
                for (Marker maker: markers) {
                    maker.remove();
                }
            }
            for (Player player : players) {

                LatLng latLng = new LatLng(player.latitude,player.longitude);
                if(player.teamName.equals("Canada")){
                    teamAPlayers.add(player);
                }
                else{
                    teamBPlayers.add(player);
                }
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
            if(teamAPlayers.size() != 0 && teamBPlayers.size() != 0){
                if(teamAPlayers.size() == teamBPlayers.size()){
                    btnStartGame.setVisibility(View.VISIBLE);
                    tvGameStatus.setText("Pressed Start Button to Start The Game.");
                    btnStartGame.setBackgroundColor(Color.GREEN);
                    btnStartGame.setEnabled(true);
                    btnStartGame.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            databaseReference.child("Game").child("isStart").setValue(true);
                            tvGameStatus.setText("Game is in Progress");
                            btnStartGame.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                else{
                    btnStartGame.setVisibility(View.VISIBLE);
                    btnStartGame.setBackgroundColor(Color.RED);
                    btnStartGame.setEnabled(false);
                    tvGameStatus.setText("Game Not Started. Need Equal Player in each team.");
                    databaseReference.child("Game").child("isStart").setValue(false);
                }
            }
            else{
                btnStartGame.setVisibility(View.INVISIBLE);
                btnStartGame.setBackgroundColor(Color.RED);
                btnStartGame.setEnabled(false);
                tvGameStatus.setText("Game Not Started. Waiting For Player to Join");
                databaseReference.child("Game").child("isStart").setValue(false);
            }
        }
    }
//    public void setWinner(List<Player> players){
//
//        List<Player> prisonAPlayers = new ArrayList<>();
//        List<Player> prisonBPlayers = new ArrayList<>();
//        for (Player player:
//            players ) {
//            if(player.isCaught.equals(true) && player.teamName.equals("Canada")){
//                prisonAPlayers.add(player);
//            }
//            else{
//                prisonBPlayers.add(player);
//            }
//            if(prisonAPlayers.size() == 5){
//
//            }
//            if(prisonBPlayers.size() == 5){
//
//            }
//        }
//    }
//
//    public boolean isPlayerCaught(LatLng userLocation, List<LatLng> polyPointsList){
//        return !PolyUtil.containsLocation(userLocation, polyPointsList,false);
//
//    }
}
