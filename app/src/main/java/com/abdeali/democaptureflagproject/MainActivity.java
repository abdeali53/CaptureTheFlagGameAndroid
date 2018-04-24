package com.abdeali.democaptureflagproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.abdeali.democaptureflagproject.Model.Player;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Location location;
    double latitude;
    double lonngitude;
    static List<Player> teamAPlayers =  new ArrayList<>();
    static List<Player> teamBPlayers =  new ArrayList<>();
    private static  final long MIN_DISTANCE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATE = 100;
    protected LocationManager locationManager;
    private LocationListener locationListener;

    private EditText etName;
    private Spinner spinnerTeam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etName = findViewById(  R.id.etName);
        spinnerTeam = findViewById(R.id.spinnerTeam);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // ask for permission

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }else{
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }


    }


    public void stopUsingGPS(){
        if(locationManager != null){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                return;
            }
            locationManager.removeUpdates(locationListener);
        }
    }

    public void btnTrackMe_Click(View view){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Player").push();
        if(location != null && !etName.getText().toString().isEmpty()){
            Player player = new Player(etName.getText().toString(),location.getLatitude(),location.getLongitude(),String.valueOf(spinnerTeam.getSelectedItem()),false);
            databaseReference.setValue(player);
            Intent intent = new Intent(getBaseContext(), StartGameActivity.class);
            intent.putExtra("PlayerReferenceId", databaseReference.getKey());
            intent.putExtra("PlayerName", etName.getText().toString());
            intent.putExtra("TeamName", String.valueOf(spinnerTeam.getSelectedItem()));
            intent.putExtra("PlayerLatitude", location.getLatitude());
            intent.putExtra("PlayerLongitude", location.getLongitude());
            startActivity(intent);
        }
        else{
            Log.d(TAG, "btnTrackMe_Click: ===========cannot insert===========");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)


                this.location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                this.location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
    }

    public void btn_ViewGame(View view){
        Intent intent = new Intent(getBaseContext(), AdminLoginActivity.class);
        startActivity(intent);
    }

}
