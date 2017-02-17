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

import java.util.Random;

/**
 * Le Menu Filtre permet à l'utilisateur de changer l'image de couleur via un filtre généré aléatoirement.
 * L'image choisie est celle chargée de la galerie via le menu de chargement photo.
 * @see ChargementPhoto
 * @author emmaveillat
 */
public class MenuFiltre extends AppCompatActivity {

    /**
     * Image qu'on va modifier
     */
    static Bitmap picture;

    /**
     * Image provenant de la galerie
     */
    Bitmap pictureToUse;

    /**
     * Bouton qui va permettre de sauvegarder
     */
    Button save;

    /**
     * Bouton qui va permettre d'appliquer les modifications sur l'image
     */
    Button couleur;

    /**
     * Bouton pour annuler les modifications faites
     */
    Button reset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_filtre);

        //Récupération de l'image choisie dans la galerie
        pictureToUse = ChargementPhoto.scaleImage();

        //Création d'une image modifiable et implémentation de son affichage dans l'application
        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
        ImageView img = (ImageView) findViewById(R.id.picture);
        img.setImageBitmap(picture);

        //Affichage du bouton save et implémentation du bouton dans le listener
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        //Affichage du bouton couleur et implémentation du bouton dans le listener
        couleur = (Button) findViewById(R.id.couleur);
        couleur.setOnClickListener(blistener);

        //Affichage du bouton reset et implémentation du bouton dans le listener
        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(blistener);

    }

    /**
     * fonction qui change l'image de couleur en appliquant un filtre coloré aléatoirement
     * @param bmp l'image à modifier
     */
    public void colorize(Bitmap bmp) {

        //Création d'un tableau de floats de taille 3
        float [] hsv = new float[3];

        //Création d'un float aléatoire
        float hue = 0F + new Random().nextFloat() * (359F - 0F);

        for(int y = 0; y < bmp.getHeight(); y++){
            for(int x = 0; x < bmp.getWidth(); x++){
                //Récupération du pixel[i,j]
                int pixel = bmp.getPixel(x,y);
                //Conversion de l'espace RGB en l'espace HSV
                Color.colorToHSV(pixel,hsv);
                //Remplacement de la valeur de hue par le float random
                hsv[0] = hue;
                //Changement de l'image en repassant de l'espace HSV en l'espace RGB
                bmp.setPixel(x,y,Color.HSVToColor(Color.alpha(pixel),hsv));
            }
        }
    }

    /**
     * Implémentation du listener
     */
    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {

                //dans le cas où on appuie sur le bouton reset, on annule les modifications en affichant l'image de base
                case R.id.reset:
                    picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    ImageView img2 = (ImageView) findViewById(R.id.picture);
                    img2.setImageBitmap(picture);
                    break;

                //dans le cas où on appuie sur le bouton save, l'image est sauvegardée dans la galerie. On est ensuite renvoyé au menu de chargement de photo
                case R.id.save:
                    MediaStore.Images.Media.insertImage(getContentResolver(), picture, ChargementPhoto.imgDecodableString + "_filtre" , "");
                    Intent second = new Intent(MenuFiltre.this, ChargementPhoto.class);
                    startActivity(second);
                    break;

                //dans le cas où on appuie sur le bouton couleur, on applique les modifications à l'image et on l'affiche
                case R.id.couleur:
                    colorize(picture);
                    ImageView img = (ImageView) findViewById(R.id.picture);
                    img.setImageBitmap(picture);
                    break;

                // par défaut, on ne fait rien
                default:
                    break;
            }
        }
    };
}
