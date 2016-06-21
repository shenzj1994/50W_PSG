package com.canadiansolar.a50wportablesolargenerator;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static com.canadiansolar.a50wportablesolargenerator.R.id.*;


public class MainActivity extends AppCompatActivity {
    TextView V_Txt;
    TextView BT_Txt;
    String v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("50W Portable Solar Generator Monitor");

        v="18.0";
        v=v.concat(" V");
        V_Txt = (TextView)findViewById(tv_v_value);
        V_Txt.setText(v);

        BT_Txt = (TextView)findViewById(tv_bt_name);
        BT_Txt.setText("Here is the BT name");

    }
    /** Called when the user clicks the 'About us' button */
    public void OpenWebBrowser(View view){
        Intent intent=new Intent(this,Web.class);
        startActivity(intent);
    }

    public void change_voltage(){

    }


}
