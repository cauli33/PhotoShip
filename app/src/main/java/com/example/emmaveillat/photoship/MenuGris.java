package com.example.emmaveillat.photoship;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Le MenuGris permet à l'utilisateur de transformer son image en niveaux de gris.
 * L'image choisie provient de la galerie par l'intermédiaire du menu de chargement de photos.
 * @see ChargementPhoto
 * @author emmaveillat
 */
public class MenuGris extends AppCompatActivity {

    /**
     * Image modifiable par l'utilisateur
     */
    static Bitmap picture;

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
    Button gris;

    /**
     * Bouton pour annuler les modifications
     */
    Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_gris);

        //Récupération de l'image à partir de celle choisir dans le menu ChargementPhoto
        pictureToUse = ChargementPhoto.scaleImage();

        //Création d'une image modifiable à partir de celle récupérée
        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        //Affichage de l'image
        ImageView img = (ImageView) findViewById(R.id.picture);
        img.setImageBitmap(picture);

        //Affichage du bouton save et implémentation du bouton dans le listener
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        //Affichage du bouton gris et implémentation du bouton dans le listener
        gris = (Button) findViewById(R.id.gris);
        gris.setOnClickListener(blistener);

        //Affichage du bouton reset et implémentation du bouton dans le listener
        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(blistener);

    }

    /**
     * Fonction qui transforme l'image en niveaux de gris
     * @param bmp l'image à modifier
     */
    public void toGray(Bitmap bmp) {
        for (int i = 0; i < bmp.getWidth(); i++) {
            for (int j = 0; j < bmp.getHeight(); j++) {
                //Récupération du pixel[i,j]
                int pixel = bmp.getPixel(i, j);

                //Récupération des valeurs de rouge, bleu et vert du pixel
                int red = Color.red(pixel);
                int blue = Color.blue(pixel);
                int green = Color.green(pixel);

                //Moyenne de toutes les valeurs et remplacement dans le pixel par la moyenne obtenue
                int moy = (red + blue + green)/3;
                int gray = Color.rgb(moy, moy, moy);
                bmp.setPixel(i, j, gray);
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
                    MediaStore.Images.Media.insertImage(getContentResolver(), picture, ChargementPhoto.imgDecodableString + "_gris" , "");
                    Intent second = new Intent(MenuGris.this, ChargementPhoto.class);
                    startActivity(second);
                    break;

                //dans le cas où on appuie sur le bouton gris, on modifie l'image et on affiche les modifications
                case R.id.gris:
                    toGray(picture);
                    ImageView img = (ImageView) findViewById(R.id.picture);
                    img.setImageBitmap(picture);
                    break;

                //par défaut, on ne fait rien
                default:
                    break;
            }
        }
    };
}
