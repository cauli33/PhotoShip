package com.example.emmaveillat.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ReplaceMenu extends AppCompatActivity {
    Bitmap picture;

    Bitmap pictureToUse;

    ImageView img;



    /**
     * Buttons to save, reset or applies the grey filter
     */
    Button save, replace, reset;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace_menu);
        // Gets picture Bitmap chosen in the gallery
        pictureToUse = PhotoLoading.scaleImage();

        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        ImageView img = (ImageView) findViewById(R.id.picture);
        img.setImageBitmap(picture);

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        replace = (Button) findViewById(R.id.replace);
        replace.setOnClickListener(blistener);

        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(blistener);
    }

    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {

                //Undoes changes by getting the original picture back
                case R.id.reset:
                    picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    img.setImageBitmap(picture);
                    break;

                //Saves image in the gallery
                case R.id.save:
                    MediaStore.Images.Media.insertImage(getContentResolver(), picture, PhotoLoading.imgDecodableString + "_replaced" , "");
                    Intent second = new Intent(ReplaceMenu.this, PhotoLoading.class);
                    startActivity(second);
                    break;

                default:
                    break;
            }
        }
    };

    public Bitmap cut(Bitmap bmp, float hue, float gap){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] map = new int[w * h];
        //Array to stock pixel hsv values
        float[] pixelHSV = new float[3];
        //Array of pixel values from the bitmap
        bmp.getPixels(map, 0, w, 0, 0, w, h);

        int white = Color.rgb(255,255,255);

        int index = 0;
        //3 cases: each one changes the way we choose the pixels we cut and the one we keep
        //Chosen hue value is not too close to 0 or 360
        if ((hue >= gap)&&(hue<=360F - gap)){
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    int pixel = map[index];
                    Color.colorToHSV(pixel, pixelHSV);
                    float pixelHue = pixelHSV[0];
                    //Get pixels out of the gap defined around hue value in gray
                    if ((pixelHue <= hue + gap) && (pixelHue >= hue - gap)) {
                        map[index] = white;
                    }
                    index++;
                }
            }
        }
        //Chosen hue value is close to 0
        else if (hue < gap){
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    int pixel = map[index];
                    Color.colorToHSV(pixel, pixelHSV);
                    float pixelHue = pixelHSV[0];
                    if ((pixelHue <= hue + gap) || (pixelHue >= hue + 360F - gap)) {
                        map[index] = white;
                    }
                    index++;
                }
            }
        }
        //Chosen value is close to 360
        else{
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    int pixel = map[index];
                    Color.colorToHSV(pixel, pixelHSV);
                    float pixelHue = pixelHSV[0];
                    if ((pixelHue <= hue -360F + gap) || (pixelHue >= hue - gap)) {
                        map[index] = white;
                    }
                    index++;
                }
            }
        }
        return Bitmap.createBitmap(map, w, h, Bitmap.Config.ARGB_8888);
    }

    public Bitmap replace(Bitmap bmpFront, Bitmap bmpBack, int k){
        int frontWidth = bmpFront.getWidth();
        int frontHeight = bmpFront.getHeight();
        int backWidth = bmpBack.getWidth();
        int backHeight = bmpBack.getHeight();
        float fact; int toUse;
        int newbackWidth; int newbackHeight;
        if (frontHeight>frontWidth){
            fact = frontHeight/backHeight;
            newbackHeight = frontHeight; newbackWidth = Math.round(backWidth * fact);
            toUse = 0;
        }
        else{
            fact = frontWidth/backWidth;
            newbackHeight = Math.round(backHeight * fact); newbackWidth = frontWidth;
            toUse = 1;
        }


        int[] mapSrc = new int[backWidth * backHeight];
        bmpBack.getPixels(mapSrc, 0, backWidth, 0, 0, backWidth, backHeight);
        int[] mapDest = new int[newbackWidth*newbackHeight];

        int index=0;
        for (int y=0; y<newbackHeight; y++){
            for (int x=0; x<newbackWidth; x++){
                mapDest[index] = mapSrc[Math.round((y*backWidth + x)/fact)];
            }
        }

        int[] mapFinalback = new int[frontWidth * frontHeight];
        index = 0;
        if (toUse==0){
            for (int y=0; y<frontHeight; y++) {
                for (int x = 0; x < frontWidth; x++) {
                    mapFinalback[index] = mapDest[y*frontWidth + x + k];
                }
            }
        }
        else{
            for (int y=0; y < frontHeight; y++) {
                for (int x = 0; x < frontWidth; x++) {
                    mapFinalback[index] = mapDest[(y+k)*frontWidth + x];
                }
            }
        }

        int[] mapFront = new int[frontWidth * frontHeight];
        bmpFront.getPixels(mapFront, 0, frontWidth, 0, 0, frontWidth, frontHeight);
        for (int i=0; i<frontWidth * frontHeight; i++){
            if (mapFront[i]==0){
                mapFront[i] = mapFinalback[i];
            }
        }
        return Bitmap.createBitmap(mapFront, frontWidth, frontHeight, Bitmap.Config.ARGB_8888);
    }
}
