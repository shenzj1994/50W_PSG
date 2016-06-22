package com.canadiansolar.a50wportablesolargenerator;


import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import static com.canadiansolar.a50wportablesolargenerator.R.id.*;
import android.util.Log;



public class MainActivity
        extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    TextView V_Txt;
    TextView BT_Txt;
    TextView mLatitudeText;
    TextView mLongitudeText;

    String v;
    GoogleApiClient mGoogleApiClient;

    Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("50W PSG Monitor");

        mLatitudeText=(TextView) findViewById(R.id.location);



        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        Log.d("TAG","connecting finish");
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        v = "18.0";
        v = v.concat(" V");
        V_Txt = (TextView) findViewById(tv_v_value);
        V_Txt.setText(v);

        BT_Txt = (TextView) findViewById(tv_bt_name);
        BT_Txt.setText("Here is the BT name");


    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Called when the user clicks the 'About us' button
     */
    public void OpenWebBrowser(View view) {
        Intent intent = new Intent(this, Web.class);
        startActivity(intent);
    }

    public void change_voltage() {

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d("TAG","onConnected start");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));

        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}



