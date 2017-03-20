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
import android.widget.Toast;

import java.util.Arrays;

/**
 * This class is used to adjust contrast of the loaded image
 * @author emmaveillat, caulihonore
 */
public class EgalizationHistogramm extends AppCompatActivity {

    Bitmap picture;

    Bitmap pictureToUse;

    Button save, HE, reset;

    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egalization_histogramm);

        pictureToUse = PhotoLoading.scaleImage();

        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        img = (ImageView) findViewById(R.id.picture);
        img.setImageBitmap(picture);

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        HE = (Button) findViewById(R.id.HE);
        HE.setOnClickListener(blistener);

        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(blistener);

    }

    public HistandMap histogram(Bitmap bmp) {
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
        for (int i = 1; i < 256; i++) {
            histogram[i] += histogram[i - 1];
        }
        return new HistandMap(histogram, pixels);
    }

    public void equalization(Bitmap bmp, HistandMap histmap) {
        int h = bmp.getHeight();
        int w = bmp.getWidth();
        int[] pixels = new int[w * h];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        int pixel, oldValue;
        float newValue;
        int[] valMap = histmap.getMap();
        int[] histogram = histmap.getHistogram();
        float[] pixelHSV = new float[3];
        for (int i = 0; i < w * h; i++) {
            pixel = pixels[i];
            oldValue = valMap[i];
            newValue = (float) (histogram[oldValue]) / (w * h);
            Color.colorToHSV(pixel, pixelHSV);
            pixelHSV[2] = newValue;
            pixels[i] = Color.HSVToColor(pixelHSV);
        }
        bmp.setPixels(pixels, 0, w, 0, 0, w, h);
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
                    MediaStore.Images.Media.insertImage(getContentResolver(), picture, PhotoLoading.imgDecodableString + "_histogramm_egalization" , "");
                    Intent second = new Intent(EgalizationHistogramm.this, PhotoLoading.class);
                    startActivity(second);
                    break;

                //Applicates histogram extension algorithm on picture
                case R.id.HE:
                    picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    HistandMap histmap = histogram(picture);
                    equalization(picture, histmap);
                    img.setImageBitmap(picture);
                    break;

                default:
                    break;
            }
        }
    };
}

final class HistandMap {
    private final int[] histogram;
    private final int[] map;

    public HistandMap(int[] histogram, int[] map) {
        this.histogram = histogram;
        this.map = map;
    }

    public int[] getHistogram() {
        return histogram;
    }

    public int[] getMap() {
        return map;
    }
}
