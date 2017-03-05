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

/**
 * This class is used to adjust contrast of the loaded image
 * @author emmaveillat
 */
public class EgalizationHistogramm extends AppCompatActivity {

    Bitmap picture;

    Bitmap pictureToUse, pictureFinal;

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

    //We need a gray level picture for histogram egalization algorithm
    public void toGray(Bitmap bmp) {
        for (int i = 0; i < bmp.getWidth(); i++) {
            for (int j = 0; j < bmp.getHeight(); j++) {
                int pixel = bmp.getPixel(i, j);

                //Gets RGB values from the pixel and sets average value in gray level
                int red = Color.red(pixel);
                int blue = Color.blue(pixel);
                int green = Color.green(pixel);

                int moy = (red + blue + green)/3;
                int gray = Color.rgb(moy, moy, moy);
                bmp.setPixel(i, j, gray);
            }
        }
    }

    public void egalization(Bitmap bmp) {
        try {
            int n = bmp.getHeight();
            int m = bmp.getWidth();
            int histogram[] = new int[256];
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    int pixel = bmp.getPixel(i, j);
                    int value = Color.red(pixel);
                    histogram[value]++;
                }
            }

            for (int i = 1; i < 256; i++){
                histogram[i]+= histogram[i-1];
            }

            for (int k = 0; k < m; k++) {
                for (int l = 0; l < n; l++) {
                    int oldPixel = pictureFinal.getPixel(k, l);
                    int oldValue = Color.red(oldPixel);
                    int newValue = (histogram[oldValue]*255)/(n*m);
                    int newPixel = Color.rgb(newValue, newValue, newValue);
                    pictureFinal.setPixel(k, l, newPixel);
                }
            }
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
                    MediaStore.Images.Media.insertImage(getContentResolver(), picture, PhotoLoading.imgDecodableString + "_histogramm_egalization" , "");
                    Intent second = new Intent(EgalizationHistogramm.this, PhotoLoading.class);
                    startActivity(second);
                    break;

                //Applicates histogram extension algorithm on picture
                case R.id.HE:
                    pictureFinal = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    toGray(pictureFinal);
                    egalization(pictureToUse);
                    img.setImageBitmap(pictureFinal);
                    break;

                default:
                    break;
            }
        }
    };
}


