package com.canadiansolar.a50wportablesolargenerator;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    /** Called when the user clicks the 'About us' button */
    public void OpenWebBrowser(View view){
        Intent intent=new Intent(this,Web.class);
        startActivity(intent);
    }


}
