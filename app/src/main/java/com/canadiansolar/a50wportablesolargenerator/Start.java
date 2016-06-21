package com.canadiansolar.a50wportablesolargenerator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;


public class Start extends Activity {
    int delay=3000; //delay in ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Delay for some time
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Jump to main activity
                jump();
            }
        }, delay);

    }

    //Jump to main activity
    public void jump(){
        Intent intent =new Intent(this,MainActivity.class);
        startActivity(intent);
    }

}
