package com.example.emmaveillat.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Arrays;

/**
 * This class improves the contrast of a bitmap thanks to the extension of dynamics.
 * @author emmaveillat
 */
public class ExtensionDynamic extends AppCompatActivity {

    /**
     * Bitmaps used to be modified
     */
    Bitmap picture,pictureToUse;

    /**
     * Buttons to save, reste or applies the extension of dynamics
     */
    Button save, ED, reset;

    /**
     * The bitmap displayed in the menu
     */
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extension_dynamic);

        //Gets picture Bitmap chosen in the gallery
        pictureToUse = PhotoLoading.scaleImage();

        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        img = (ImageView) findViewById(R.id.picture);
        img.setImageBitmap(picture);

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        ED = (Button) findViewById(R.id.ED);
        ED.setOnClickListener(blistener);

        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(blistener);

    }

    public MinMaxandMap minmax(Bitmap bmp) {
        int h = bmp.getHeight();
        int w = bmp.getWidth();
        int[] pixels = new int[w * h];
        int[] histogram = new int[256];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        Arrays.fill(histogram, 0);
        int red, blue, green, gray, pixel;
        for (int i = 0; i < w * h; i++) {
            pixel = pixels[i];
            red = Color.red(pixel);
            blue = Color.blue(pixel);
            green = Color.green(pixel);
                /* Je fais la moyenne de ces 3 valeurs et donne au pixel du bitmap de sortie le niveau de gris associé */
            gray = (int) (0.299F*red + 0.587F*green + 0.114F*blue);
            pixels[i] = gray;
            histogram[gray]++;
        }
        //Gets the min and the max values
        int k = 0;
        while (histogram[k] == 0) {
            k++;
        }
        int min = k;

        k = 255;
        while (histogram[k] == 0) {
            k--;
        }
        int max = k;
        return new MinMaxandMap(min, max, pixels);
    }

    /**
     * function which applies the dynamic extension
     * @param bmp the original bitmap
     */
    public void extension(Bitmap bmp, MinMaxandMap minmax) {
        try {
            int h = bmp.getHeight();
            int w = bmp.getWidth();
            int min = minmax.getMin();
            int max = minmax.getMax();

            int[] pixels = new int[w * h];
            bmp.getPixels(pixels, 0, w, 0, 0, w, h);
            int pixel, oldValue;
            int valMap[]=minmax.getMap();
            float newValue;
            float[] pixelHSV = new float[3];
            for (int i = 0; i < w*h; i++) {
                    pixel = pixels[i];
                    oldValue = valMap[i];
                    newValue = (float)(oldValue - min)/(max - min);
                    Color.colorToHSV(pixel, pixelHSV);
                    pixelHSV[2] = newValue;
                    pixels[i] = Color.HSVToColor(pixelHSV);
            }
            bmp.setPixels(pixels, 0, w, 0, 0, w, h);
        } catch (Exception e) {
            Toast.makeText(this, "Quelque chose a mal fonctionné, veuillez réessayer.", Toast.LENGTH_LONG).show();
        }
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
                    MediaStore.Images.Media.insertImage(getContentResolver(), picture, PhotoLoading.imgDecodableString + "_dynamic_extension" , "");
                    Intent second = new Intent(ExtensionDynamic.this, PhotoLoading.class);
                    startActivity(second);
                    break;

                //Applies dynamic extension algorithm on picture
                case R.id.ED:
                    picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    MinMaxandMap minmax = minmax(picture);
                    extension(picture, minmax);
                    img.setImageBitmap(picture);
                    break;

                default:
                    break;
            }
        }
    };
}

final class MinMaxandMap {
    private final int min;
    private final int max;
    private final int[] map;

    public MinMaxandMap(int min, int max, int[] map) {
        this.min = min;
        this.max = max;
        this.map = map;
    }

    public int getMin() {
        return min;
    }

    public int getMax() { return max; }

    public int[] getMap() {
        return map;
    }
}