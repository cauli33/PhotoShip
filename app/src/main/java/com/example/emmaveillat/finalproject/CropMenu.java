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
public class CropMenu extends AppCompatActivity {

    /**
     * The interval and the hue choosen by the user
     */
    int[] toCrop = {0,0,0,0};

    int udlr;

    /**
     * Buttons to save the image in the galery and to reset the image
     */
    Button up, down, left, right;

    ImageButton save, reset, crop;
    /**
     * The image displayed in the menu
     */
    ImageView img;

    /**
     * The seekbars to select the hue and the interval of values
     */
    SeekBar cropBar;

    /**
     * Texts displayed
     */
    TextView cropText;

    /**
     * Bitmaps used to be transformed
     */
    Bitmap picture, pictureToUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_menu);

        // Gets picture Bitmap chosen in the gallery
        pictureToUse = PhotoLoading.scaleImage();

        //copies the picture to make it mutable
        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        //Objects displayed in the activity
        udlr = 0;

        img = (ImageView) findViewById(R.id.pictureCroped);
        img.setImageBitmap(picture);

        save = (ImageButton) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        cropText = (TextView) findViewById(R.id.textcrop);

        cropBar = (SeekBar) findViewById(R.id.cropbar);
        cropBar.setOnSeekBarChangeListener(seekBarChangeListener);

        reset = (ImageButton)findViewById(R.id.reset);
        reset.setOnClickListener(blistener);

        up = (Button)findViewById(R.id.up);
        up.setOnClickListener(blistener);

        down = (Button)findViewById(R.id.down);
        down.setOnClickListener(blistener);

        left = (Button)findViewById(R.id.left);
        left.setOnClickListener(blistener);

        right = (Button)findViewById(R.id.right);
        right.setOnClickListener(blistener);

        crop = (ImageButton)findViewById(R.id.crop);
        crop.setOnClickListener(blistener);
    }

    /**
     * Listener that modifies seekbars if they are tracking or not
     */
    OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { loadBitmapVisual(); }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { loadBitmapVisual(); }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            loadBitmapVisual();
        }
    };

    /**
     * function which load the new Bitmap with its new hue depending on the selected interval
     */
    private void loadBitmapVisual() {
        if (picture != null) {
            toCrop[udlr] = cropBar.getProgress();
            //Gets seekbar values and updates text and image
            switch (udlr){
                case 0:
                    if (toCrop[0] > 100 - toCrop[1]){
                        toCrop[0] = 100 - toCrop[1];
                    }
                    cropText.setText("Crop up " + String.valueOf(toCrop[0]) + "%");
                    break;
                case 1:
                    if (toCrop[1] > 100 - toCrop[0]){
                        toCrop[1] = 100 - toCrop[0];
                    }
                    cropText.setText("Crop down " + String.valueOf(toCrop[1]) + "%");
                    break;
                case 2:
                    if (toCrop[2] > 100 - toCrop[3]){
                        toCrop[2] = 100 - toCrop[3];
                    }
                    cropText.setText("Crop left " + String.valueOf(toCrop[2]) + "%");
                    break;
                case 3:
                    if (toCrop[3] > 100 - toCrop[2]){
                        toCrop[3] = 100 - toCrop[2];
                    }
                    cropText.setText("Crop right " + String.valueOf(toCrop[3]) + "%");
                    break;
            }

            img.setImageBitmap(updateVisual(picture, toCrop));

        }
    }

    /**
     * function which keeps the selected hue (given in a interval) in the Bitmap and makes the other pixels in grey
     * @param src the bitmap the user wants to modify
     * @return the modified bitmap
     */
    private Bitmap updateVisual(Bitmap src, int[] toCrop) {
        int w = src.getWidth();
        int h = src.getHeight();
        int toCropUp = toCrop[0] * h / 100;
        int toCropDown = h - toCrop[1] * h / 100;
        int toCropLeft = toCrop[2] * w / 100;
        int toCropRight = w - toCrop[3] * w / 100;
        int[] mapSrc = new int[w * h];
        src.getPixels(mapSrc, 0, w, 0, 0, w, h);

        for (int y = toCropUp; y < toCropUp + 4; ++y) {
            for (int x = toCropLeft ; x < toCropRight ; ++x) {
                mapSrc[ y * w + x ] = Color.rgb(0,0,0);
            }
        }
        for (int y = toCropDown - 4; y < toCropDown; ++y) {
            for (int x = toCropLeft; x < toCropRight; ++x) {
                mapSrc[ y * w + x ] = Color.rgb(0,0,0);
            }
        }
        for (int x = toCropLeft; x < toCropLeft + 4; ++x) {
            for (int y = toCropUp; y < toCropDown; ++y) {
                mapSrc[ y * w + x ] = Color.rgb(0,0,0);
            }
        }
        for (int x = toCropRight - 4; x < toCropRight; ++x) {
            for (int y = toCropUp; y < toCropDown; ++y) {
                mapSrc[ y * w + x ] = Color.rgb(0,0,0);
            }
        }
        return Bitmap.createBitmap(mapSrc, w, h, Config.ARGB_8888);
    }

    private Bitmap crop(Bitmap src, int[] toCrop){
        int oldWidth = src.getWidth();
        int oldHeight = src.getHeight();
        int toCropUp = toCrop[0] * oldHeight / 100;
        int toCropDown = oldHeight - toCrop[1] * oldHeight / 100;
        int toCropLeft = toCrop[2] * oldWidth / 100;
        int toCropRight = oldWidth - toCrop[3] * oldWidth / 100;
        int newWidth = toCropRight - toCropLeft;
        int newHeight = toCropDown - toCropUp;
        return Bitmap.createBitmap(picture, toCropLeft,toCropUp,newWidth, newHeight);

    }

    /**
     * Defines some buttons like "save"
     */
    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {
                //Saves image in the gallery
                case R.id.save:
                    Bitmap pictureFinal = (crop(picture, toCrop)).copy(Bitmap.Config.ARGB_8888, true);
                    MediaStore.Images.Media.insertImage(getContentResolver(), pictureFinal, PhotoLoading.imgDecodableString + "_crop" , "");
                    Intent second = new Intent(CropMenu.this, PhotoLoading.class);
                    startActivity(second);
                    break;
                case R.id.reset:
                    cropBar.setProgress(0);
                    toCrop[0]=0; toCrop[1]=0; toCrop[2]=0; toCrop[3]=0;
                    picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    img.setImageBitmap(picture);
                    break;
                case R.id.up:
                    udlr = 0;
                    cropBar.setProgress(toCrop[0]);
                    break;
                case R.id.down:
                    udlr = 1;
                    cropBar.setProgress(toCrop[1]);
                    break;
                case R.id.left:
                    udlr = 2;
                    cropBar.setProgress(toCrop[2]);
                    break;
                case R.id.right:
                    udlr = 3;
                    cropBar.setProgress(toCrop[3]);
                    break;
                case R.id.crop:
                    picture = crop(picture, toCrop);
                    img.setImageBitmap(picture);
                    break;
                default:
                    break;
            }
        }
    };

}