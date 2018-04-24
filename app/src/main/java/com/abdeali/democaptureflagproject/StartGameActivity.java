package com.abdeali.democaptureflagproject;

import android.*;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.abdeali.democaptureflagproject.Constant.LatLngConstant;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import java.util.List;

public class StartGameActivity extends AppCompatActivity {

    private static final String TAG = "StartGameActivity";
    private TextView tvLatitude, tvLongitude, tvTeamName, tvGameStatus, tvFlagStatus;
    private Button btnCapture;

    private String PlayerReferenceId;
    private String TeamName;
    private float  previousDistanceBetweeenFlag = 0f , cuurentDistanceBetweeenFlag = 0f;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //Flag A === CAD

    LatLng FlagLatLng;

    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        tvLatitude = findViewById(R.id.tvPlayerLat);
        tvLongitude = findViewById(R.id.tvPlayerLong);
        tvGameStatus = findViewById(R.id.tvGameStatus);
        tvTeamName = findViewById(R.id.tvTeamName);
        tvFlagStatus = findViewById(R.id.tvFlagStatus);
        btnCapture = findViewById(R.id.btnCapture);

        PlayerReferenceId = getIntent().getStringExtra("PlayerReferenceId");
        TeamName = getIntent().getStringExtra("TeamName");
        tvTeamName.setText("Team: " + TeamName);
        tvLatitude.setText(Double.toString(getIntent().getDoubleExtra("PlayerLatitude",0)));
        tvLongitude.setText(Double.toString(getIntent().getDoubleExtra("PlayerLongitude",0)));
        btnCapture.setVisibility(View.INVISIBLE);
        Log.d(TAG, "onCreate: ======================PlayerReferenceId======================== " + PlayerReferenceId);
        FlagLatLng = getIntent().getStringExtra("TeamName").equals("Canada") ? LatLngConstant.getFlagALatLong() : LatLngConstant.getFlagBLatLong();


        // Listen Location with Reference to Flag

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 9002);

        }
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    boolean isCaught = false;
                    calculateDistanceBetweenFlag(location);
                    LatLng playerLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    isCaught = isPlayerCaught(playerLatLng, LatLngConstant.getGameFieldLatLngs());
                    if (isCaught) {
                        tvGameStatus.setText("You are caught and transfer to prison.");
                        stopUsingGPS();
                    }
                    Log.d(TAG, "onLocationChanged: ============================Change Detected=============");
                    tvLatitude.setText("Lat " + location.getLatitude());
                    tvLongitude.setText("Long " + location.getLongitude());
                    DatabaseReference updatePlayerLocation = databaseReference.child("Player").child(PlayerReferenceId);

                    updatePlayerLocation.child("latitude").setValue(location.getLatitude());
                    updatePlayerLocation.child("longitude").setValue(location.getLongitude());
                    updatePlayerLocation.child("isCaught").setValue(isCaught);

                    // to check from value uodate from firebase


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

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        valueListnerOnGame();
        manualFirebasseUpdate();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, locationListener);
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, locationListener);
        }
    }

    public boolean isPlayerCaught(LatLng userLocation, List<LatLng> polyPointsList) {
        return !PolyUtil.containsLocation(userLocation, polyPointsList, false);

    }

    public void stopUsingGPS() {
//        if (locationManager != null) {
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            locationManager.removeUpdates(locationListener);
//        }
    }

    public void calculateDistanceBetweenFlag(Location playerLocation) {
        Location flag = new Location("Flag");
        flag.setLatitude(FlagLatLng.latitude);
        flag.setLongitude(FlagLatLng.longitude);
        cuurentDistanceBetweeenFlag = playerLocation.distanceTo(flag);

//        if (previousDistanceBetweeenFlag == 0) {
//            previousDistanceBetweeenFlag = cuurentDistanceBetweeenFlag;
//        }

        if (previousDistanceBetweeenFlag < cuurentDistanceBetweeenFlag) {
            tvFlagStatus.setText("Your Flag Distance :" + cuurentDistanceBetweeenFlag + " m");
        } else {
            tvFlagStatus.setText("Your Flag Distance :" + cuurentDistanceBetweeenFlag + "m");
        }
        if (cuurentDistanceBetweeenFlag < 5) {
            btnCapture.setVisibility(View.VISIBLE);
            btnCapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvGameStatus.setText("Your Team Win!!!!! Hurray!!!!!!!!");
                    databaseReference.child("Game").child("winner").setValue(TeamName);
                    Log.d(TAG, "onClick: ============================Player Won======================");
                }
            });
        }
        else{
            btnCapture.setVisibility(View.INVISIBLE);
        }
        previousDistanceBetweeenFlag = cuurentDistanceBetweeenFlag;

    }

    public void manualFirebasseUpdate(){
        DatabaseReference updatePlayerLocation = databaseReference.child("Player").child(PlayerReferenceId);
        updatePlayerLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isCaught = false;

                LatLng playerLatLng = new LatLng(dataSnapshot.child("latitude").getValue(Double.class),
                        dataSnapshot.child("longitude").getValue(Double.class));
                Location playerLocation = new Location("");
                playerLocation.setLatitude(playerLatLng.latitude);
                playerLocation.setLongitude(playerLatLng.longitude);

                calculateDistanceBetweenFlag(playerLocation);
                isCaught = isPlayerCaught(playerLatLng, LatLngConstant.getGameFieldLatLngs());
                if (isCaught) {
                    tvGameStatus.setText("You are caught and transfer to prison.");
//                                stopUsingGPS();
                }
                Log.d(TAG, "onLocationChanged: ============================Change Detected=============");
                tvLatitude.setText("Lat " + playerLocation.getLatitude());
                tvLongitude.setText("Long " + playerLocation.getLongitude());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void valueListnerOnGame(){
        databaseReference.child("Game").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("isStart").getValue(Boolean.class)){
                    tvGameStatus.setText(
                            dataSnapshot.child("winner").getValue(String.class).isEmpty() ? "Game is in Progress" : "Team "
                                    + dataSnapshot.child("winner").getValue(String.class) +" won."
                    );
                }
                else{
                    tvGameStatus.setText("Please wait Admin to Start Game");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    ///Game Status

}
