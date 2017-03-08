package com.example.emmaveillat.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * This class is used to deform a bitmap with chosen factors.
 * @author emmaveillat
 */
public class CropMenu extends AppCompatActivity {

    /**
     * Bitmpas used to be modified
     */
    Bitmap pictureToUse, picture;

    /**
     * Values of deformation
     */
    float facteurDecimalX, facteurDecimalY;

    /**
     * Texts chosen by the user
     */
    EditText facteurLongueur, facteurHauteur;

    /**
     * Buttons to save or reset a bitmap
     */
    Button save, crop, reset;

    /**
     * The bitmap displayed in the menu
     */
    ImageView img;

    ImageButton help_crop;

    int width, height;

    int newW, newH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_menu);

        // Creates textzones to choose dimension changes factor for height and width
        facteurLongueur = (EditText) findViewById(R.id.newWidth);
        facteurLongueur.setHint("Notez ici le facteur pour la longueur");

        facteurHauteur = (EditText) findViewById(R.id.newHeigh);
        facteurHauteur.setHint("Notez ici le facteur pour la hauteur");

        // Gets picture Bitmap chosen in the gallery
        pictureToUse = PhotoLoading.scaleImage();

        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        img = (ImageView) findViewById(R.id.pictureCroped);
        img.setImageBitmap(picture);

        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(blistener);

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        crop = (Button) findViewById(R.id.crop);
        crop.setOnClickListener(blistener);

        width = picture.getWidth();
        height = picture.getHeight();

        help_crop = (ImageButton) findViewById(R.id.help_crop);
        help_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast toastAuSiropDErable = Toast.makeText(getApplicationContext(), "Cette image a une hauteur de " + height +" pixels et une longueur " + width + " pixels." , Toast.LENGTH_LONG);
                toastAuSiropDErable.show();
            }});
    }

    private View.OnClickListener blistener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                //Undoes changes by getting the original picture back
                case R.id.reset:
                    picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    img.setImageBitmap(picture);
                    break;

                //Gets values of factors from textzones
                case R.id.crop:
                    facteurDecimalX = Float.valueOf(facteurLongueur.getText().toString());
                    facteurDecimalY = Float.valueOf(facteurHauteur.getText().toString());
                    newW = (int) facteurDecimalX;
                    newH = (int) facteurDecimalY;
                    picture=Bitmap.createBitmap(picture, 0,0,newW, newH);
                    img.setImageBitmap(picture);
                    break;

                //Saves image in the gallery
                case R.id.save:
                    try {
                        MediaStore.Images.Media.insertImage(getContentResolver(), picture, PhotoLoading.imgDecodableString + "_deforme", "");
                        Intent second = new Intent(CropMenu.this, PhotoLoading.class);
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
