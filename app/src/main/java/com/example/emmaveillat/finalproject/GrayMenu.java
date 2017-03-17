package com.example.emmaveillat.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * This class transforms the bitmap colors on levels of grey.
 * @author emmaveillat
 */
public class GrayMenu extends AppCompatActivity {

    /**
     * The bitmap modified
     */
    static Bitmap picture;

    /**
     * The original bitmap
     */
    Bitmap pictureToUse;

    /**
     * Buttons to save, reset or applies the grey filter
     */
    Button save, gris, reset;

    Toolbar bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gray_menu);
        // Gets picture Bitmap chosen in the gallery
        pictureToUse = PhotoLoading.scaleImage();

        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        ImageView img = (ImageView) findViewById(R.id.picture);
        img.setImageBitmap(picture);

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        gris = (Button) findViewById(R.id.grey);
        gris.setOnClickListener(blistener);

        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(blistener);

        //bar = (Toolbar) findViewById(R.id.tb);
        //setSupportActionBar(bar);

    }

    /**
     * function which transforms colors of a bitmap on levels of grey
     * @param bmp the original bitmap
     */
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

    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {

                //Undoes changes by getting the original picture back
                case R.id.reset:
                    picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    ImageView img2 = (ImageView) findViewById(R.id.picture);
                    img2.setImageBitmap(picture);
                    break;

                //Saves image in the gallery
                case R.id.save:
                    MediaStore.Images.Media.insertImage(getContentResolver(), picture, PhotoLoading.imgDecodableString + "_gris" , "");
                    Intent second = new Intent(GrayMenu.this, PhotoLoading.class);
                    startActivity(second);
                    break;

                //Changes the image to gray levels
                case R.id.grey:
                    picture=toGray(picture);
                    ImageView img = (ImageView) findViewById(R.id.picture);
                    img.setImageBitmap(picture);
                    break;

                default:
                    break;
            }
        }
    };

/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }*/
}
