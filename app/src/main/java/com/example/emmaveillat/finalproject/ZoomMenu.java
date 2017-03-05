package com.example.emmaveillat.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class ZoomMenu extends AppCompatActivity {

    Bitmap pictureToUse, pictureZoom, picture;

    float facteurDecimalX, facteurDecimalY;

    EditText facteurLongueur, facteurHauteur;

    Button save, zoom, reset;

    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_menu);

        // Creates textzones to choose dimension changes factor for height and width
        facteurLongueur = (EditText) findViewById(R.id.newWidth);
        facteurLongueur.setHint("Notez ici le facteur pour la longueur");

        facteurHauteur = (EditText) findViewById(R.id.newHeigh);
        facteurHauteur.setHint("Notez ici le facteur pour la hauteur");

        // Gets picture Bitmap chosen in the gallery
        pictureToUse = PhotoLoading.scaleImage();

        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        img = (ImageView) findViewById(R.id.pictureZoom);
        img.setImageBitmap(picture);

        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(blistener);

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        zoom = (Button) findViewById(R.id.ppv);
        zoom.setOnClickListener(blistener);

    }


    public void algoPPV(Bitmap bmp) {
        try {
            float oldHeight = (float) bmp.getHeight();
            float oldWidth = (float) bmp.getWidth();

            //Calculates new dimensions depending on factors from the textzones
            int newHeight = Math.round(oldHeight * facteurDecimalY);
            int newWidth = Math.round(oldWidth * facteurDecimalX);

            pictureZoom = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < newWidth; i++) {
                for (int j = 0; j < newHeight; j++) {
                    //Gets closest neighbor pixel value
                    pictureZoom.setPixel(i, j, bmp.getPixel(Math.round(i / facteurDecimalX), Math.round(j / facteurDecimalY)));
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            e.getMessage();
        }
    }


    private View.OnClickListener blistener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                //Undoes changes by getting the original picture back
                case R.id.reset:
                    pictureZoom = null;
                    picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    img.setImageBitmap(picture);
                    break;

                //Gets values of factors from textzones
                case R.id.ppv:
                    facteurDecimalX = Float.valueOf(facteurLongueur.getText().toString());
                    facteurDecimalY = Float.valueOf(facteurHauteur.getText().toString());

                    algoPPV(picture);
                    img.setImageBitmap(pictureZoom);
                    break;

                //Saves image in the gallery
                case R.id.save:
                    try {
                        MediaStore.Images.Media.insertImage(getContentResolver(), pictureZoom, PhotoLoading.imgDecodableString + "_deforme", "");
                        Intent second = new Intent(ZoomMenu.this, PhotoLoading.class);
                        startActivity(second);
                        break;
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                default:
                    break;
            }
        }
    };
}
