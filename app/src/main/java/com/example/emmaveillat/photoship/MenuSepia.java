package com.example.emmaveillat.photoship;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MenuSepia extends AppCompatActivity {

    /**
     * Image modifiable par l'utilisateur
     */
    static Bitmap picture;

    Bitmap newPic;

    /**
     * Image chargée dans la galerie
     */
    Bitmap pictureToUse;

    /**
     * Bouton pour sauvegarder
     */
    Button save;

    /**
     * Bouton pour mettre en gris
     */
    Button sepia;

    /**
     * Bouton pour annuler les modifications
     */
    Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_sepia);

        //Récupération de l'image à partir de celle choisir dans le menu ChargementPhoto
        pictureToUse = ChargementPhoto.scaleImage();

        //Création d'une image modifiable à partir de celle récupérée
        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
        newPic = picture.copy(Bitmap.Config.ARGB_8888, true);

        //Affichage de l'image
        ImageView img = (ImageView) findViewById(R.id.picture);
        img.setImageBitmap(picture);

        //Affichage du bouton save et implémentation du bouton dans le listener
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        //Affichage du bouton gris et implémentation du bouton dans le listener
        sepia = (Button) findViewById(R.id.sepia);
        sepia.setOnClickListener(blistener);

        //Affichage du bouton reset et implémentation du bouton dans le listener
        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(blistener);

    }

    public void toSephia(Bitmap bmpOriginal)
    {
        int width, height, r,g, b, c, gry;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        int depth = 20;
        Canvas canvas = new Canvas(newPic);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setScale(.3f, .3f, .3f, 1.0f);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        canvas.drawBitmap(bmpOriginal, 0, 0, paint);
        for(int x=0; x < width; x++) {
            for(int y=0; y < height; y++) {
                c = bmpOriginal.getPixel(x, y);

                r = Color.red(c);
                g = Color.green(c);
                b = Color.blue(c);

                gry = (r + g + b) / 3;
                r = g = b = gry;

                r = r + (depth * 2);
                g = g + depth;

                if(r > 255) {
                    r = 255;
                }
                if(g > 255) {
                    g = 255;
                }
                newPic.setPixel(x, y, Color.rgb(r, g, b));
            }
        }
    }

    /**
     * Implémentation du listener
     */
    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {

                //dans le cas où on appuie sur le bouton reset, on annule les modifications effectuées et on affiche l'image de départ
                case R.id.reset:
                    picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    ImageView img2 = (ImageView) findViewById(R.id.picture);
                    img2.setImageBitmap(picture);
                    break;

                //dans le cas où on appuie sur le bouton save, on enregistre dans la galerie la photo modifiée. On est renvoyé sur le menu de chargement de photo
                case R.id.save:
                    MediaStore.Images.Media.insertImage(getContentResolver(), newPic, ChargementPhoto.imgDecodableString + "_sepia" , "");
                    Intent second = new Intent(MenuSepia.this, ChargementPhoto.class);
                    startActivity(second);
                    break;

                //dans le cas où on appuie sur le bouton gris, on modifie l'image et on affiche les modifications
                case R.id.sepia:
                    toSephia(picture);
                    ImageView img = (ImageView) findViewById(R.id.picture);
                    img.setImageBitmap(newPic);
                    break;

                //par défaut, on ne fait rien
                default:
                    break;
            }
        }
    };
}
