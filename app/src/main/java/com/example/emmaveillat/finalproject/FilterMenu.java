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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


/**
 * This class is used to pick a color and apply a color filter
 * @author caulihonore
 */
public class FilterMenu extends AppCompatActivity {

    /**
     * The interval and the hue choosen by the user
     */
    Float hue;

    /**
     * Buttons to save the image in the galery and to reset the image
     */
    Button save, reset;

    /**
     * The image displayed in the menu
     */
    ImageView imageResult;

    /**
     * The seekbars to select the hue and the interval of values
     */
    SeekBar hueBar;

    /**
     * Texts displayed
     */
    TextView hueText;

    /**
     * Bitmaps used to be transformed
     */
    Bitmap picture, pictureToUse;

    boolean go = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_menu);

        // Gets picture Bitmap chosen in the gallery
        pictureToUse = PhotoLoading.scaleImage();

        //copies the picture to make it mutable
        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        //Objects displayed in the activity
        imageResult = (ImageView) findViewById(R.id.result);
        imageResult.setImageBitmap(picture);

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        hueText = (TextView) findViewById(R.id.texthue);

        hueBar = (SeekBar) findViewById(R.id.huebar);
        hueBar.setOnSeekBarChangeListener(seekBarChangeListener);

        reset = (Button)findViewById(R.id.reset);
        reset.setOnClickListener(blistener);
    }

    /**
     * Listener that modifies seekbars if they are tracking or not
     */
    OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { loadBitmapHSV(); }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { loadBitmapHSV(); }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            loadBitmapHSV();
        }
    };

    /**
     * function which load the new Bitmap with its new hue depending on the selected interval
     */
    private void loadBitmapHSV() {
        if ((picture != null)&&(go)) {
            go = false;
            //Gets seekbar values and updates text and image
            int hue = hueBar.getProgress();

            //Displays the texts
            hueText.setText("Hue: " + String.valueOf(hue));

            float settingHue = (float) hue;
            go = updateHSV(picture, settingHue);
            imageResult.setImageBitmap(picture);

        }
    }

    /**
     * function which applicates a color filter of the selected hue
     * @param src the bitmap the user wants to modify
     * @param hue the new value of hue
     * @return the modified bitmap
     */
    private boolean updateHSV(Bitmap src, float hue) {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrc = new int[w * h];
        //Array to stock pixel hsv values
        float[] pixelHSV = new float[3];
        //Array of pixel values from the bitmap
        src.getPixels(mapSrc, 0, w, 0, 0, w, h);

        for (int i = 0; i < h*w; ++i) {
            int pixel = mapSrc[i];
            Color.colorToHSV(pixel, pixelHSV);
            pixelHSV[0] = hue;
            mapSrc[i] = Color.HSVToColor(pixelHSV);
        }
        src.setPixels(mapSrc, 0, w, 0, 0, w, h);
        return true;
    }

    /**
     * Defines some buttons like "save"
     */
    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {
                ////Undoes changes by getting the original picture back and sets hue seekbar to 0
                case R.id.reset:
                    hueBar.setProgress(0);
                    picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    imageResult.setImageBitmap(picture);
                    break;
                //Saves image in the gallery
                case R.id.save:
                    Bitmap pictureFinal = picture.copy(Bitmap.Config.ARGB_8888, true);
                    updateHSV(pictureFinal, hue);
                    MediaStore.Images.Media.insertImage(getContentResolver(), pictureFinal, PhotoLoading.imgDecodableString + "_couleur" , "");
                    Intent second = new Intent(FilterMenu.this, PhotoLoading.class);
                    startActivity(second);
                    break;
                default:
                    break;
            }
        }
    };

}