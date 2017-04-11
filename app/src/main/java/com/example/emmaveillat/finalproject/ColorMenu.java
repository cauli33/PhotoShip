package com.example.emmaveillat.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * This class is used to change hue, saturation and luminosity of the loaded image with three seekbars
 * @author emmaveillat
 */
public class ColorMenu extends AppCompatActivity {

    /**
     * The value from the HSV space
     */
    Float hue, satur, value;

    /**
     * Buttons to save the image in the gallery or to reset it
     */
    Button save, reset;

    /**
     * The bitmap displayed in the menu
     */
    ImageView imageResult;

    /**
     * The seekbars which modify the hue, saturation and value of a bitmap
     */
    SeekBar hueBar, satBar, valBar;

    /**
     * Texts displayed
     */
    TextView hueText, satText, valText;

    /**
     * Bitmaps used to be transformed and displayed
     */
    Bitmap picture, pictureToUse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_menu);

        //Gets picture Bitmap chosen in the gallery
        pictureToUse = PhotoLoading.scaleImage();

        //copies the original bitmap to be mutable
        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        //Objects displayed in the menu
        imageResult = (ImageView) findViewById(R.id.result);
        imageResult.setImageBitmap(picture);

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        hueText = (TextView) findViewById(R.id.texthue);
        satText = (TextView) findViewById(R.id.textsat);
        valText = (TextView) findViewById(R.id.textval);

        hueBar = (SeekBar) findViewById(R.id.huebar);
        satBar = (SeekBar) findViewById(R.id.satbar);
        valBar = (SeekBar) findViewById(R.id.valbar);
        hueBar.setOnSeekBarChangeListener(seekBarChangeListener);
        satBar.setOnSeekBarChangeListener(seekBarChangeListener);
        valBar.setOnSeekBarChangeListener(seekBarChangeListener);

        reset = (Button)findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                //Initializes seekbars to 256 (middle)
                hueBar.setProgress(256);
                satBar.setProgress(256);
                valBar.setProgress(256);
                loadBitmapHSV();
            }});
    }

    /**
     * Listener that changes the value of the HSV space depending on the tracking of the bars
     */
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { loadBitmapHSV(); }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { loadBitmapHSV(); }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            loadBitmapHSV();
        }
    };

    private void loadBitmapHSV() {
        if (picture != null) {
            //Gets seekbar values and updates text and image
            int progressHue = hueBar.getProgress() - 256;
            int progressSat = satBar.getProgress() - 256;
            int progressVal = valBar.getProgress() - 256;

            float hue = (float) progressHue * 360 / 256;
            float sat = (float) progressSat / 256;
            float val = (float) progressVal / 256;

            hueText.setText("Hue: " + String.valueOf(hue));
            satText.setText("Saturation: " + String.valueOf(sat));
            valText.setText("Value: " + String.valueOf(val));

            imageResult.setImageBitmap(updateHSV(picture, hue, sat, val));

        }
    }

    /**
     * function that changes HSV space of the Bitmap
     * @param src the original Bitmap
     * @param settingHue the new hue
     * @param settingSat the new saturation
     * @param settingVal the new value
     * @return the modified bitmap
     */
    private Bitmap updateHSV(Bitmap src, float settingHue, float settingSat, float settingVal) {
        //Changes pixels hsv values depending on seekbars
        hue = settingHue;
        satur = settingSat;
        value = settingVal;

        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrcColor = new int[w * h];
        int[] mapDestColor = new int[w * h];

        float[] pixelHSV = new float[3];

        src.getPixels(mapSrcColor, 0, w, 0, 0, w, h);

        int index = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                //If values are too low or too up, changes to min or max
                Color.colorToHSV(mapSrcColor[index], pixelHSV);

                pixelHSV[0] = pixelHSV[0] + settingHue;
                if (pixelHSV[0] < 0.0f) {
                    pixelHSV[0] += 360.0f;
                } else if (pixelHSV[0] > 360.0f) {
                    pixelHSV[0] -= 360.0f;
                }

                pixelHSV[1] = pixelHSV[1] + settingSat;
                if (pixelHSV[1] < 0.0f) {
                    pixelHSV[1] += 1.0f;
                } else if (pixelHSV[1] > 1.0f) {
                    pixelHSV[1] -= 1.0f;
                }

                pixelHSV[2] = pixelHSV[2] + settingVal;
                if (pixelHSV[2] < 0.0f) {
                    pixelHSV[2] += 1.0f;
                } else if (pixelHSV[2] > 1.0f) {
                    pixelHSV[2] -= 1.0f;
                }

                mapDestColor[index] = Color.HSVToColor(pixelHSV);

                index++;
            }
        }

        return Bitmap.createBitmap(mapDestColor, w, h, Bitmap.Config.ARGB_8888);

    }

    /**
     * Listener for some buttons like "save"
     */
    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {
                //Saves image in the gallery
                case R.id.save:
                    Bitmap pictureFinal = (updateHSV(picture, hue, satur, value)).copy(Bitmap.Config.ARGB_8888, true);
                    MediaStore.Images.Media.insertImage(getContentResolver(), pictureFinal, PhotoLoading.imgDecodableString + "_couleur" , "");
                    Intent second = new Intent(ColorMenu.this, PhotoLoading.class);
                    startActivity(second);
                    break;
                default:
                    break;
            }
        }
    };


}
