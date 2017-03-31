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
 * This class is used to pick a color and some values above and after the selected one with two seekbars.
 * Only the select value interval is displayed, all of the others will be displayed in black and white.
 * @author caulihonore
 */
 public class ChoiceMenu extends AppCompatActivity {

    /**
     * The interval and the hue choosen by the user
     */
    Float hue, gap;

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
    SeekBar hueBar, gapBar;

    /**
     * Texts displayed
     */
    TextView hueText, gapText;

    /**
     * Bitmaps used to be transformed
     */
    Bitmap picture, pictureToUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_menu);

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
        gapText = (TextView) findViewById(R.id.textgap);

        hueBar = (SeekBar) findViewById(R.id.huebar);
        gapBar = (SeekBar) findViewById(R.id.gapbar);
        hueBar.setOnSeekBarChangeListener(seekBarChangeListener);
        gapBar.setOnSeekBarChangeListener(seekBarChangeListener);

        reset = (Button)findViewById(R.id.reset);
        reset.setOnClickListener(new OnClickListener(){
            /**
             * @param arg0
             */
            @Override
            public void onClick(View arg0) {
                //Initializes hue and gap seekbars to 0 (left extremity)
                hueBar.setProgress(0);
                gapBar.setProgress(0);
                picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                imageResult.setImageBitmap(picture);
            }});
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
        if (picture != null) {
            //Gets seekbar values and updates text and image
            int hue = hueBar.getProgress();
            int gap = gapBar.getProgress();

            //Displays the texts
            hueText.setText("Hue: " + String.valueOf(hue));
            gapText.setText("Gap: " + String.valueOf(gap));

            float settingHue = (float) hue;
            float settingGap = (float) gap;
            imageResult.setImageBitmap(updateHSV(picture, settingHue, settingGap));

        }
    }

    /**
     * function which keeps the selected hue (given in a interval) in the Bitmap and makes the other pixels in grey
     * @param src the bitmap the user wants to modify
     * @param settingHue the new value of hue
     * @param settingGap the future interval
     * @return the modified bitmap
     */
    private Bitmap updateHSV(Bitmap src, float settingHue, float settingGap) {

        hue = settingHue;
        gap = settingGap;

        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrc = new int[w * h];
        //Array to stock pixel hsv values
        float[] pixelHSV = new float[3];
        //Array of pixel values from the bitmap
        src.getPixels(mapSrc, 0, w, 0, 0, w, h);

        int index = 0;
        //3 cases: each one changes the way we choose the pixels we change to gray and the one we keep colored
        //Chosen hue value is not too close to 0 or 360
        if ((hue >= gap)&&(hue<=360F - gap)){
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    int pixel = mapSrc[index];

                    Color.colorToHSV(pixel, pixelHSV);
                    float pixelHue = pixelHSV[0];
                    //Get pixels out of the gap defined around hue value in gray
                    if ((pixelHue >= hue + gap) || (pixelHue <= hue - gap)) {
                        int red = Color.red(pixel);
                        int blue = Color.blue(pixel);
                        int green = Color.green(pixel);
                        int gray = (red + blue + green) / 3;
                        mapSrc[index] = Color.rgb(gray, gray, gray);
                    }
                    index++;
                }
            }
        }
        //Chosen hue value is close to 0
        else if (hue < gap){
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    int pixel = mapSrc[index];

                    Color.colorToHSV(pixel, pixelHSV);
                    float pixelHue = pixelHSV[0];

                    if ((pixelHue >= hue + gap) && (pixelHue <= hue + 360F - gap)) {
                        int red = Color.red(pixel);
                        int blue = Color.blue(pixel);
                        int green = Color.green(pixel);
                        int gray = (red + blue + green) / 3;
                        mapSrc[index] = Color.rgb(gray, gray, gray);
                    }
                    index++;
                }
            }
        }
        //Chosen value is close to 360
        else{
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    int pixel = mapSrc[index];
                    Color.colorToHSV(pixel, pixelHSV);
                    float pixelHue = pixelHSV[0];

                    if ((pixelHue >= hue -360F + gap) && (pixelHue <= hue - gap)) {
                        int red = Color.red(pixel);
                        int blue = Color.blue(pixel);
                        int green = Color.green(pixel);
                        int gray = (red + blue + green) / 3;
                        mapSrc[index] = Color.rgb(gray, gray, gray);
                    }
                    index++;
                }
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
                    Bitmap pictureFinal = (updateHSV(picture, hue, gap)).copy(Bitmap.Config.ARGB_8888, true);
                    MediaStore.Images.Media.insertImage(getContentResolver(), pictureFinal, PhotoLoading.imgDecodableString + "_couleur" , "");
                    Intent second = new Intent(ChoiceMenu.this, PhotoLoading.class);
                    startActivity(second);
                    break;
                default:
                    break;
            }
        }
    };

}
