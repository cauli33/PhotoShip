package com.example.emmaveillat.finalproject;

import android.graphics.Bitmap;
import android.graphics.Color;
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
public class FingerMenu extends AppCompatActivity {

    /**
     * original Bitmap
     */
    Bitmap pictureToUse;

    /**
     * The bitmap displayed in the menu
     */
    ImageView img;
    int topParam, leftParam, rightParam, botParam;
    int x,y;

    /**
     * The modified bitmap
     */
    Bitmap picture, graypicture;

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
    int gap;

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
        setContentView(R.layout.activity_finger_menu);

        img = (ImageView)findViewById(R.id.imageview);

        pictureToUse = PhotoLoading.scaleImage();

        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
        bmpWidth = picture.getWidth();
        bmpHeight = picture.getHeight();
        topParam =  img.getPaddingTop();
        leftParam =  img.getPaddingLeft();
        rightParam = leftParam + bmpWidth;
        botParam = topParam + bmpHeight;

        graypicture = toGray(picture);

        gap = Math.min(bmpWidth,bmpHeight)/10;

        //initialization of the distances
        distCurrent = 1;
        dist0 = 1;
        drawMatrix();

        img.setOnTouchListener(MyOnTouchListener);
        touchState = IDLE;
    }

    public Bitmap toGray(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixels = new int[height * width];
        Bitmap bmpGray = bmp.copy(Bitmap.Config.ARGB_8888, true); /* Je copie le bitmap en entrée (ce sera le bitmap initial) et le fait modifiable */
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < width * height; i++) {   /* boucles sur tout le tableau de pixels (bitmap initial) */
                /* Je récupère les valeurs RGB du pixel dans le bitmap initial */
            int pixel = pixels[i];
            int red = Color.red(pixel);
            int blue = Color.blue(pixel);
            int green = Color.green(pixel);
                /* Je fais la moyenne de ces 3 valeurs et donne au pixel du bitmap de sortie le niveau de gris associé */
            int gray = (int) (0.299F*red + 0.587F*green + 0.114F*blue);
            pixels[i] = Color.rgb(gray, gray, gray);
        }
        bmpGray.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmpGray;
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
        img.setImageBitmap(resizedBitmap);
    }

    private void replace(Bitmap oldbmp, Bitmap newbmp, int x, int y){
        int oldmap[] = new int[bmpWidth*bmpHeight];
        int newmap[] = new int[bmpWidth*bmpHeight];
        oldbmp.getPixels(oldmap, 0, bmpWidth, 0, 0, bmpWidth, bmpHeight);
        newbmp.getPixels(newmap, 0, bmpWidth, 0, 0, bmpWidth, bmpHeight);
        int i = Math.max(0, y - gap);
        int startj = Math.max(0, x - gap);

        /*while (i <= y){
            int j = startj;
            double i2 = Math.pow(i,2);
            while (Math.sqrt(i2 + Math.pow(j,2)) > gap){j++;}
            for (int k = j; k <= 2*x - j; k++){
                oldmap[i * bmpWidth + k] = newmap[i * bmpWidth + k];
                oldmap[(2*y - i) * bmpWidth + k] = newmap[(2*y - i) * bmpWidth + k];
            }
            i++;
        }*/
    }


    View.OnTouchListener MyOnTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // TODO Auto-generated method stub
            switch(event.getAction() & MotionEvent.ACTION_MASK) {

                //if a finger touches the screen, the factor of zoom is displayed
                case MotionEvent.ACTION_DOWN:touchState = TOUCH;
                    x = (int) event.getX();
                    y = (int) event.getY();
                    if ((x>leftParam)&&(x<rightParam)&&(y>topParam)&&(y<botParam)) {
                        replace(picture, graypicture, x, y);
                        img.setImageBitmap(picture);
                    }
                    break;


                // if the fingers move, the distances change and the factor of zoom is displayed
                case MotionEvent.ACTION_MOVE:touchState = TOUCH;
                    x = (int) event.getX();
                    y = (int) event.getY();
                    if ((x>leftParam)&&(x<rightParam)&&(y>topParam)&&(y<botParam)) {
                        replace(picture, graypicture, x, y);
                        img.setImageBitmap(picture);
                    }
                    break;


                //if one finger touches the screen, the factor of zoom is displayed
                case MotionEvent.ACTION_POINTER_UP:touchState = TOUCH;
                    x = (int) event.getX();
                    y = (int) event.getY();
                    if ((x>leftParam)&&(x<rightParam)&&(y>topParam)&&(y<botParam)) {
                        replace(picture, graypicture, x, y);
                        img.setImageBitmap(picture);
                    }
                    break;
            }
            return true;
        }

    };
}