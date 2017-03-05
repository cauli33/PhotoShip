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

public class ExtensionDynamic extends AppCompatActivity {

    Bitmap picture;

    Bitmap pictureToUse, pictureFinal;

    Button save, ED, reset;

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

    //We need a gray level picture for dynamic extension algorithm
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

    public void extension(Bitmap bmp) {
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
            int i = 0;
            while (histogram[i] == 0) {
                i++;
            }
            int min = i;

            int j = 255;
            while (histogram[j] == 0) {
                j--;
            }
            int max = j;


            int LUT[] = new int[256];
            for (int ng = 0; ng < 256; ng++) {
                if (max - min != 0) {
                    LUT[ng] = 255 * (ng - min) / (max - min);
                } else{
                    Toast.makeText(this, "Problème dans la construction de la LUT", Toast.LENGTH_LONG).show();

                }
            }
            for (int k = 0; k < m; k++) {
                for (int l = 0; l < n; l++) {
                    int oldPixel = pictureFinal.getPixel(k, l);
                    int oldValue = Color.red(oldPixel);
                    int newValue = LUT[oldValue];
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
                    ImageView img2 = (ImageView) findViewById(R.id.picture);
                    img2.setImageBitmap(picture);
                    break;

                //Saves image in the gallery
                case R.id.save:
                    MediaStore.Images.Media.insertImage(getContentResolver(), picture, PhotoLoading.imgDecodableString + "_dynamic_extension" , "");
                    Intent second = new Intent(ExtensionDynamic.this, PhotoLoading.class);
                    startActivity(second);
                    break;

                //Applicates dynamic extension algorithm on picture
                case R.id.ED:
                    pictureFinal = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    toGray(pictureFinal);
                    extension(pictureToUse);
                    img.setImageBitmap(pictureFinal);
                    break;

                default:
                    break;
            }
        }
    };
}

