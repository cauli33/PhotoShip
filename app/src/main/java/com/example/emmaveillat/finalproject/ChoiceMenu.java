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

    Float hue, gap;

    Button save, reset;

    ImageView imageResult;

    SeekBar hueBar, gapBar;

    TextView hueText, gapText;

    Bitmap picture, pictureToUse;


    /**
     * @param savedInstanceState
     *  This Bundle is used to save the state of the instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_menu);

        pictureToUse = PhotoLoading.scaleImage();

        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

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
                hueBar.setProgress(0);
                gapBar.setProgress(0);
                loadBitmapHSV();
            }});
    }


    OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
        /**
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { loadBitmapHSV(); }

        /**
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { loadBitmapHSV(); }

        /**
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            loadBitmapHSV();
        }
    };


    private void loadBitmapHSV() {
        if (picture != null) {

            int hue = hueBar.getProgress();
            int gap = gapBar.getProgress();

            hueText.setText("Hue: " + String.valueOf(hue));
            gapText.setText("Gap: " + String.valueOf(gap));

            float settingHue = (float) hue;
            float settingGap = (float) gap;
            imageResult.setImageBitmap(updateHSV(picture, settingHue, settingGap));

        }
    }

    /**
     * @param src
     *  This is the original bitmap
     * @param settingHue
     *  This is the hue value
     * @param settingGap
     *  This define the values above and after the selected one
     * @return the new bitmap
     */
    private Bitmap updateHSV(Bitmap src, float settingHue, float settingGap) {

        hue = settingHue;
        gap = settingGap;

        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrc = new int[w * h];

        float[] pixelHSV = new float[3];

        src.getPixels(mapSrc, 0, w, 0, 0, w, h);

        int index = 0;
        if ((hue >= gap)&&(hue<=360F - gap)){
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    int pixel = mapSrc[index];

                    Color.colorToHSV(pixel, pixelHSV);
                    float pixelHue = pixelHSV[0];

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


    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {
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
