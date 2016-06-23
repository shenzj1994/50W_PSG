package com.canadiansolar.a50wportablesolargenerator;


import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.gms.common.api.GoogleApiClient;

import static com.canadiansolar.a50wportablesolargenerator.R.id.*;
import android.util.Log;



public class MainActivity extends AppCompatActivity {

    TextView V_Txt;
    TextView C_Txt;
    TextView P_Txt;
    double voltage,current,power;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("50W PSG Monitor");

        ProgressBar vprogress=(ProgressBar)findViewById(progressBar2);
        V_Txt = (TextView) findViewById(tv_v_value);
        C_Txt = (TextView)findViewById(tv_c_value);
        P_Txt = (TextView)findViewById(tv_p_value);


    }

    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        voltage = 0;
        current = 0;
        power=voltage*current;

        V_Txt.setText(voltage+" V");
        C_Txt.setText(current+" A");
        P_Txt.setText(power+" W");
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
}



