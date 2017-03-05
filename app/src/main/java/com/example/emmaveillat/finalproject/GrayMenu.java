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

public class GrayMenu extends AppCompatActivity {

    static Bitmap picture;

    Bitmap pictureToUse;

    Button save, gris, reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gray_menu);
        // Gets picture Bitmap chosen in the gallery
        pictureToUse = PhotoLoading.scaleImage();

        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        ImageView img = (ImageView) findViewById(R.id.picture);
        img.setImageBitmap(picture);

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        gris = (Button) findViewById(R.id.gris);
        gris.setOnClickListener(blistener);

        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(blistener);

    }


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
                    MediaStore.Images.Media.insertImage(getContentResolver(), picture, PhotoLoading.imgDecodableString + "_gris" , "");
                    Intent second = new Intent(GrayMenu.this, PhotoLoading.class);
                    startActivity(second);
                    break;

                //Changes the image to gray levels
                case R.id.gris:
                    toGray(picture);
                    ImageView img = (ImageView) findViewById(R.id.picture);
                    img.setImageBitmap(picture);
                    break;

                default:
                    break;
            }
        }
    };
}
