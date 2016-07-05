package com.canadiansolar.a50wportablesolargenerator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;


public class Start extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);
/*        // Delay for some time
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                jump();

            }
        }, 3000);*/
    }

    //Jump to main activity
    public void jump_to_main(View view){
        Intent intent =new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    public void jump_to_bt(View view){
        Intent BT_Intent=new Intent(this,BT.class);
        startActivity(BT_Intent);

    }

}
