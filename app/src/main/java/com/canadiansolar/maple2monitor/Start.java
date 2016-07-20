package com.canadiansolar.maple2monitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;


public class Start extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        int uiOptions=View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(uiOptions);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                jump_to_main(null);
            }
        }, 3000);
    }


    public void jump_to_main(View view){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }

}
