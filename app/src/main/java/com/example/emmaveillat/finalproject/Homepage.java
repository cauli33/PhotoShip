package com.example.emmaveillat.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

//Code de Lost in Bielefeld sur stackoverflow
public class Homepage extends Activity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_homepage);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent photoLoading = new Intent(Homepage.this,PhotoLoading.class);
                Homepage.this.startActivity(photoLoading);
                Homepage.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}