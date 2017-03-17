package com.example.emmaveillat.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/** A COMMENTER
 * This class is used to pick a color and some values above and after the selected one with two seekbars.
 * Only the select value interval is displayed, all of the others will be displayed in black and white.
 * @author caulihonore
 */
public class RotateMenu extends AppCompatActivity {

    /**
     * The interval and the hue choosen by the user
     */

    /**
     * Buttons to save the image in the galery and to reset the image
     */
    Button rotateleft, rotateright, fliplr, flipud;

    ImageButton save, reset;
    /**
     * The image displayed in the menu
     */
    ImageView img;

    /**
     * The seekbars to select the hue and the interval of values
     */

    /**
     * Texts displayed
     */

    /**
     * Bitmaps used to be transformed
     */
    Bitmap picture, pictureToUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate_menu);

        // Gets picture Bitmap chosen in the gallery
        pictureToUse = PhotoLoading.scaleImage();

        //copies the picture to make it mutable
        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        //Objects displayed in the activity

        img = (ImageView) findViewById(R.id.picture);
        img.setImageBitmap(picture);

        save = (ImageButton) findViewById(R.id.save);
        save.setOnClickListener(blistener);


        reset = (ImageButton)findViewById(R.id.reset);
        reset.setOnClickListener(blistener);

        rotateleft = (Button)findViewById(R.id.left);
        rotateleft.setOnClickListener(blistener);

        rotateright = (Button)findViewById(R.id.right);
        rotateright.setOnClickListener(blistener);

        fliplr = (Button)findViewById(R.id.fliplr);
        fliplr.setOnClickListener(blistener);

        flipud = (Button)findViewById(R.id.flipud);
        flipud.setOnClickListener(blistener);
    }


    /**
     * function which keeps the selected hue (given in a interval) in the Bitmap and makes the other pixels in grey
     * @param src the bitmap the user wants to modify
     * @return the modified bitmap
     */
    private Bitmap leftrotation(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrc = new int[w * h];
        src.getPixels(mapSrc, 0, w, 0, 0, w, h);
        int[] mapDest = new int[w * h];

        int index = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0 ; x < w ; ++x) {
                mapDest[index] = mapSrc[(w - x - 1) * w + y];
                index++;
            }
        }
        return Bitmap.createBitmap(mapSrc, h, w, Config.ARGB_8888);
    }

    private Bitmap rightrotation(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrc = new int[w * h];
        src.getPixels(mapSrc, 0, w, 0, 0, w, h);
        int[] mapDest = new int[w * h];

        int index = 0;
        for (int y = 0; y < w; ++y) {
            for (int x = 0 ; x < h ; ++x) {
                mapDest[index] = mapSrc[x * (h + 1) - y];
                index++;
            }
        }
        return Bitmap.createBitmap(mapSrc, h, w, Config.ARGB_8888);
    }

    private Bitmap flipleftright(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrc = new int[w * h];
        src.getPixels(mapSrc, 0, w, 0, 0, w, h);
        int[] mapDest = new int[w * h];

        int index = 0;
        for (int y = 0; y < w; ++y) {
            for (int x = 0 ; x < h ; ++x) {
                mapDest[index] = mapSrc[y * (w + 1) - x];
                index++;
            }
        }
        return Bitmap.createBitmap(mapSrc, w, h, Config.ARGB_8888);
    }

    private Bitmap flipupdown(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrc = new int[w * h];
        src.getPixels(mapSrc, 0, w, 0, 0, w, h);
        int[] mapDest = new int[w * h];

        int index = 0;
        for (int y = 0; y < w; ++y) {
            for (int x = 0 ; x < h ; ++x) {
                mapDest[index] = mapSrc[(h - y) * w + x];
                index++;
            }
        }
        return Bitmap.createBitmap(mapSrc, w, h, Config.ARGB_8888);
    }
    /**
     * Defines some buttons like "save"
     */
    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {
                //Saves image in the gallery
                case R.id.save:
                    Bitmap pictureFinal = picture.copy(Bitmap.Config.ARGB_8888, true);
                    MediaStore.Images.Media.insertImage(getContentResolver(), pictureFinal, PhotoLoading.imgDecodableString + "_rotate" , "");
                    Intent second = new Intent(RotateMenu.this, PhotoLoading.class);
                    startActivity(second);
                    break;
                case R.id.reset:
                    picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    img.setImageBitmap(picture);
                    break;
                case R.id.left:
                    picture = leftrotation(picture);
                    img.setImageBitmap(picture);
                    break;
                case R.id.right:
                    picture = rightrotation(picture);
                    img.setImageBitmap(picture);
                    break;
                case R.id.fliplr:
                    picture = flipleftright(picture);
                    img.setImageBitmap(picture);
                    break;
                case R.id.flipud:
                    picture = flipupdown(picture);
                    img.setImageBitmap(picture);
                    break;
                default:
                    break;
            }
        }
    };

}
