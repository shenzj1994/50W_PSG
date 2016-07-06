package com.canadiansolar.a50wportablesolargenerator;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import static com.canadiansolar.a50wportablesolargenerator.R.id.*;
import android.util.Log;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

    TextView V_Txt;
    TextView C_Txt;
    TextView P_Txt;
    ProgressBar vprogress;
    double voltage,current,power;
    BluetoothAdapter BA;
    static String BTMname="HC-05";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Monitor");
        vprogress=(ProgressBar)findViewById(progressBar2);
        V_Txt = (TextView) findViewById(tv_v_value);
        C_Txt = (TextView)findViewById(tv_c_value);
        P_Txt = (TextView)findViewById(tv_p_value);
        BA = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        voltage = 18;
        current = 2;
        power=voltage*current;

        V_Txt.setText(voltage+" V");
        C_Txt.setText(current+" A");
        P_Txt.setText(power+" W");

        vprogress.setIndeterminate(false);
        vprogress.setMax(50);
        Double D1 = new Double(power);
        int i1 = D1.intValue();
        vprogress.setProgress(i1);
    }

    /**
     * Called when the user clicks the 'About us' button
     */
    public void OpenWebBrowser(View view) {
        Intent intent = new Intent(this, Web.class);
        startActivity(intent);
    }

    public void test(View view){
        Log.d("BT", "test:Now will try to turn on BT ");
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Log.d("BT","test:BT has been turned on");
        }
        else
        {
            Log.d("BT","test:BT Already ON");
        }


    }
}



