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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_menu);

        facteurLongueur = (EditText) findViewById(R.id.newWidth);
        facteurLongueur.setHint("Notez ici le facteur pour la longueur");

        facteurHauteur = (EditText) findViewById(R.id.newHeigh);
        facteurHauteur.setHint("Notez ici le facteur pour la hauteur");

        pictureToUse = PhotoLoading.scaleImage();

        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        ImageView img = (ImageView) findViewById(R.id.pictureZoom);
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

            int newHeight = Math.round(oldHeight * facteurDecimalY);
            int newWidth = Math.round(oldWidth * facteurDecimalX);

            pictureZoom = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < newWidth; i++) {
                for (int j = 0; j < newHeight; j++) {
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

                case R.id.reset:
                    pictureZoom = null;
                    ImageView img2 = (ImageView) findViewById(R.id.pictureZoom);
                    img2.setImageBitmap(pictureZoom);
                    picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    img2.setImageBitmap(picture);
                    break;

                case R.id.ppv:
                    facteurDecimalX = Float.valueOf(facteurLongueur.getText().toString());
                    facteurDecimalY = Float.valueOf(facteurHauteur.getText().toString());

                    algoPPV(picture);
                    ImageView img = (ImageView) findViewById(R.id.pictureZoom);
                    img.setImageBitmap(pictureZoom);
                    break;

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
