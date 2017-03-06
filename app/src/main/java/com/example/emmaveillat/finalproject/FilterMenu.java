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

import java.util.Random;

public class FilterMenu extends AppCompatActivity {

    static Bitmap picture;

    Bitmap pictureToUse;

    Button save, couleur, reset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_menu);

        //Gets picture Bitmap chosen in the gallery
        pictureToUse = PhotoLoading.scaleImage();

        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
        ImageView img = (ImageView) findViewById(R.id.picture);
        img.setImageBitmap(picture);

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        couleur = (Button) findViewById(R.id.color);
        couleur.setOnClickListener(blistener);

        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(blistener);

    }


    public void colorize(Bitmap bmp) { //needs changes to get/setPixelS
        //Replaces hue value from every pixel to the same random hue value
        float [] hsv = new float[3];

        float hue = 0F + new Random().nextFloat() * (359F - 0F);

        for(int y = 0; y < bmp.getHeight(); y++){
            for(int x = 0; x < bmp.getWidth(); x++){
                int pixel = bmp.getPixel(x,y);
                Color.colorToHSV(pixel,hsv);
                hsv[0] = hue;
                bmp.setPixel(x,y,Color.HSVToColor(Color.alpha(pixel),hsv));
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
                    MediaStore.Images.Media.insertImage(getContentResolver(), picture, PhotoLoading.imgDecodableString + "_filtre" , "");
                    Intent second = new Intent(FilterMenu.this, PhotoLoading.class);
                    startActivity(second);
                    break;

                //Applicates random color filter
                case R.id.color:
                    colorize(picture);
                    ImageView img = (ImageView) findViewById(R.id.picture);
                    img.setImageBitmap(picture);
                    break;

                default:
                    break;
            }
        }
    };
}
