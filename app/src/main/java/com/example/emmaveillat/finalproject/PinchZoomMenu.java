package com.example.emmaveillat.finalproject;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This class helps the user to zoom the bitmap with his fingers.
 * @author emmaveillat
 */
public class PinchZoomMenu extends AppCompatActivity {

    /**
     * original Bitmap
     */
    Bitmap pictureToUse;

    /**
     * The bitmap displayed in the menu
     */
    ImageView myImageView;

    /**
     * The modified bitmap
     */
    Bitmap picture;

    /**
     * the dimensions of the bitmap
     */
    int bmpWidth, bmpHeight;

    /**
     * statments of the fingers on the screen
     */
    int touchState;
    final int IDLE = 0;
    final int TOUCH = 1;
    final int PINCH = 2;

    /**
     * distances from the fingers
     */
    float dist0, distCurrent;

    /**
     * Text displayed
     */
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

        //initialization of the distances
        distCurrent = 1;
        dist0 = 1;
        drawMatrix();

        myImageView.setOnTouchListener(MyOnTouchListener);
        touchState = IDLE;

        txt = (TextView) findViewById(R.id.zoomfactor);
        txt.setText(String.valueOf(distCurrent/dist0));
    }

    /**
     * function which draw a matrix depending on the distances and creates the new zoomed bitmap
     */
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

                //if a finger touches the screen, the factor of zoom is displayed
                case MotionEvent.ACTION_DOWN:touchState = TOUCH;

                    txt = (TextView) findViewById(R.id.zoomfactor);
                    txt.setText(String.valueOf(distCurrent/dist0));
                    break;

                //if the fingers pinch the bitmap, the distances change and the factor of zoom is displayed
                case MotionEvent.ACTION_POINTER_DOWN:touchState = PINCH;
                    distx = event.getX(0) - event.getX(1);
                    disty = event.getY(0) - event.getY(1);
                    dist0 = (float) Math.sqrt(distx * distx + disty * disty);

                    txt = (TextView) findViewById(R.id.zoomfactor);
                    txt.setText(String.valueOf(distCurrent/dist0));
                    break;

                // if the fingers move, the distances change and the factor of zoom is displayed
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

                //if the fingers stop zooming the bitmap, the factor of zoom is displayed
                case MotionEvent.ACTION_UP:touchState = IDLE;
                    txt = (TextView) findViewById(R.id.zoomfactor);
                    txt.setText(String.valueOf(distCurrent/dist0));
                    break;

                //if one finger touches the screen, the factor of zoom is displayed
                case MotionEvent.ACTION_POINTER_UP:touchState = TOUCH;
                    txt = (TextView) findViewById(R.id.zoomfactor);
                    txt.setText(String.valueOf(distCurrent/dist0));
                    break;
            }
            return true;
        }

    };
}



