package com.example.emmaveillat.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Homepage extends AppCompatActivity {

    Button go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        go = (Button) findViewById(R.id.go);
        go.setOnClickListener(blistener);
    }


    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {

                case R.id.go:
                    Intent second = new Intent(Homepage.this, PhotoLoading.class);
                    startActivity(second);
                    break;

                default:
                    break;
            }
        }
    };
}
