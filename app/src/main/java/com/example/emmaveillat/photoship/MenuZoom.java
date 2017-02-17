package com.example.emmaveillat.photoship;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Le Menu Zoom permet à l'utilisateur de déformer l'image en séletionnant les facteurs de multiplication de sa hauteur et sa largeur.
 * Il s'agit d'interpolation au plus proche voisin.
 * L'utilisateur peut sauvegarder ses déformations. S'il souhaite uniquement zoomer, il doit se référer au menu PinchZoom.
 * @see MenuPinchZoom
 * @author emmaveillat
 */
public class MenuZoom extends AppCompatActivity {

    /**
     * Image chargée de la galerie
     */
    Bitmap pictureToUse;

    /**
     * Image déformée
     */
    Bitmap pictureZoom;

    /**
     * Image à déformer
     */
    Bitmap picture;

    /**
     * Facteurs pour la longueur et la largeur
     */
    float facteurDecimalX;
    float facteurDecimalY;

    /**
     * Légendes des facteurs à compléter par l'utilisateur
     */
    EditText facteurLongueur;
    EditText facteurHauteur;

    /**
     * Bouton pour sauvegarder
     */
    Button save;

    /**
     * Bouton pour déformer
     */
    Button zoom;

    /**
     * Bouton pour annuler les modifications
     */
    Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_zoom);

        //Affichage de la légende pour le facteur multiplicateur de la longueur de l'image
        facteurLongueur = (EditText) findViewById(R.id.newWidth);
        facteurLongueur.setHint("Notez ici le facteur pour la longueur");

        //Affichage de la légende pour le facteur multiplicateur de la hauteur de l'image
        facteurHauteur = (EditText) findViewById(R.id.newHeigh);
        facteurHauteur.setHint("Notez ici le facteur pour la hauteur");

        //Récupération de l'image à partir du menu de chargement de photo
        pictureToUse = ChargementPhoto.scaleImage();

        //Création d'une image modifiable
        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        //Affichage de l'image dans l'application
        ImageView img = (ImageView) findViewById(R.id.pictureZoom);
        img.setImageBitmap(picture);

        //Affichage du bouton reset et implémentation du bouton dans le listener
        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(blistener);

        //Affichage du bouton save et implémentation du bouton dans le listener
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        //Affichage du bouton zoom et implémentation
        zoom = (Button) findViewById(R.id.ppv);
        zoom.setOnClickListener(blistener);

    }

    /**
     * Fonction qui déforme une image en fonction de deux facteurs multiplicateurs (interpolation au plus proche voisin)
     * @param bmp l'image à modifier
     */
    public void algoPPV(Bitmap bmp) {
        try {
            //Création de deux floats correspondant aux facteurs multiplicateurs
            float oldHeight = (float) bmp.getHeight();
            float oldWidth = (float) bmp.getWidth();

            //Définition des nouvelles dimensions de l'image
            int newHeight = Math.round(oldHeight * facteurDecimalY);
            int newWidth = Math.round(oldWidth * facteurDecimalX);

            //Création d'une nouvelle image correspondant à celle déformée
            pictureZoom = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < newWidth; i++) {
                for (int j = 0; j < newHeight; j++) {
                    //on comble les pixels manquants en fonction de leur place sur l'image de base
                    pictureZoom.setPixel(i, j, bmp.getPixel(Math.round(i / facteurDecimalX), Math.round(j / facteurDecimalY)));
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            e.getMessage();
        }
    }

    /**
     * Implémentation du listener
     */
    private View.OnClickListener blistener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {

                //dans le cas où on appuie sur le bouton reset, on annule les modifications en affichant l'image de base
                case R.id.reset:
                    pictureZoom = null;
                    ImageView img2 = (ImageView) findViewById(R.id.pictureZoom);
                    img2.setImageBitmap(pictureZoom);
                    picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    img2.setImageBitmap(picture);
                    break;

                //dans le cas où on appuie sur le bouton ppv, on déforme l'image en fonction des facteurs remplis par l'utilisateur puis on l'affiche
                case R.id.ppv:
                    //on récupère les valeurs données par l'utilisateur
                    facteurDecimalX = Float.valueOf(facteurLongueur.getText().toString());
                    facteurDecimalY = Float.valueOf(facteurHauteur.getText().toString());

                    //on déforme l'image puis on l'affiche
                    algoPPV(picture);
                    ImageView img = (ImageView) findViewById(R.id.pictureZoom);
                    img.setImageBitmap(pictureZoom);
                    break;

                //dans le cas où on appuie sur le bouton save, on enregistre l'image déformée dans la galerie et on retourne au menu de chargement de photo
                case R.id.save:
                    try {
                        MediaStore.Images.Media.insertImage(getContentResolver(), pictureZoom, ChargementPhoto.imgDecodableString + "_deforme", "");
                        Intent second = new Intent(MenuZoom.this, ChargementPhoto.class);
                        startActivity(second);
                        break;
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                 //par défaut, on ne fait rien
                default:
                    break;
            }
        }
    };
}
