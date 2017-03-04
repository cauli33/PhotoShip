package com.example.emmaveillat.finalproject;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PinchZoomMenu extends AppCompatActivity {

    Bitmap pictureToUse;

    ImageView myImageView;

    Bitmap picture;

    int bmpWidth, bmpHeight;

    int touchState;
    final int IDLE = 0;
    final int TOUCH = 1;
    final int PINCH = 2;

    float dist0, distCurrent;

    TextView txt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinch_zoom_menu);

        myImageView = (ImageView)findViewById(R.id.imageview);

        pictureToUse = PhotoLoading.scaleImage();

        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        bmpWidth = picture.getWidth();
        bmpHeight = picture.getHeight();

        distCurrent = 1;
        dist0 = 1;
        drawMatrix();

        myImageView.setOnTouchListener(MyOnTouchListener);
        touchState = IDLE;

        txt = (TextView) findViewById(R.id.zoomfactor);
        txt.setText(String.valueOf(distCurrent/dist0));
    }

    private void drawMatrix(){
        float curScale = distCurrent/dist0;
        if (curScale < 0.1){
            curScale = 0.1f;
        }

        Bitmap resizedBitmap;
        int newHeight = (int) (bmpHeight * curScale);
        int newWidth = (int) (bmpWidth * curScale);
        resizedBitmap = Bitmap.createScaledBitmap(picture, newWidth, newHeight, false);
        myImageView.setImageBitmap(resizedBitmap);
    }


    View.OnTouchListener MyOnTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // TODO Auto-generated method stub
            float distx, disty;
            switch(event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:touchState = TOUCH;

                    txt = (TextView) findViewById(R.id.zoomfactor);
                    txt.setText(String.valueOf(distCurrent/dist0));
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:touchState = PINCH;
                    distx = event.getX(0) - event.getX(1);
                    disty = event.getY(0) - event.getY(1);
                    dist0 = (float) Math.sqrt(distx * distx + disty * disty);

                    txt = (TextView) findViewById(R.id.zoomfactor);
                    txt.setText(String.valueOf(distCurrent/dist0));
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (touchState == PINCH) {
                        distx = event.getX(0) - event.getX(1);
                        disty = event.getY(0) - event.getY(1);
                        distCurrent =  (float) Math.sqrt(distx * distx + disty * disty);
                        drawMatrix();

                        txt = (TextView) findViewById(R.id.zoomfactor);
                        txt.setText(String.valueOf(distCurrent/dist0));
                    }
                    break;

                case MotionEvent.ACTION_UP:touchState = IDLE;
                    txt = (TextView) findViewById(R.id.zoomfactor);
                    txt.setText(String.valueOf(distCurrent/dist0));
                    break;

                case MotionEvent.ACTION_POINTER_UP:touchState = TOUCH;
                    txt = (TextView) findViewById(R.id.zoomfactor);
                    txt.setText(String.valueOf(distCurrent/dist0));
                    break;
            }
            return true;
        }

    };
}



