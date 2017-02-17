package com.example.emmaveillat.photoship;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Le Menu liste les différentes modifications applicables à l'image choisie par l'utilisateur.
 * @see MenuCouleur
 * @see MenuFiltre
 * @see MenuGris
 * @see MenuPinchZoom
 * @see MenuZoom
 * @author emmaveillat
 */
public class Menu extends AppCompatActivity {

    /**
     * Bouton qui amène au menu de déformation (interpolation ppv)
     */
    Button ppv;

    /**
     * Bouton qui amène au menu de zoom de l'image
     */
    Button pinch;

    /**
     * Bouton qui amène au menu de mise en niveaux de gris
     */
    Button gris;

    /**
     * Bouton qui amène au menu de changement de couleur, saturation et luminosité
     */
    Button couleur;

    /**
     * Bouton qui amène au menu de changement de filtre
     */
    Button filtre;

    Button HE;

    Button teinte;

    Button sepia;

    /**
     * Bouton qui apporte une aide concernant le menu de déformation
     */
    ImageButton aide_ppv;

    /**
     * Bouton qui apporte une aide concernant le menu de zoom
     */
    ImageButton aide_pinch;

    /**
     * Bouton qui apporte une aide concernant le menu de mise en niveaux de gris
     */
    ImageButton aide_gris;

    /**
     * Bouton qui apporte une aide concernant le menu de changement de couleur, saturation et luminosité
     */
    ImageButton aide_couleur;

    /**
     * Bouton qui apporte une aide concernant le menu de changement de filtre de couleur
     */
    ImageButton aide_filtre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Affichage du Bouton pinch et implémentation du bouton dans le listener
        pinch = (Button) findViewById(R.id.pinch);
        pinch.setOnClickListener(blistener);

        teinte = (Button) findViewById(R.id.teinte);
        teinte.setOnClickListener(blistener);

        //Affichage du Bouton ppv et implémentation du bouton dans le listener
        ppv = (Button) findViewById(R.id.ppv);
        ppv.setOnClickListener(blistener);

        //Affichage du Bouton gris et implémentation du bouton dans le listener
        gris = (Button) findViewById(R.id.gris);
        gris.setOnClickListener(blistener);

        //Affichage du Bouton couleur et implémentation du bouton dans le listener
        couleur = (Button) findViewById(R.id.couleur);
        couleur.setOnClickListener(blistener);

        //Affichage du Bouton filtre et implémentation du bouton dans le listener
        filtre = (Button) findViewById(R.id.filtre);
        filtre.setOnClickListener(blistener);

        HE = (Button) findViewById(R.id.HE);
        HE.setOnClickListener(blistener);

        sepia = (Button) findViewById(R.id.sepia);
        sepia.setOnClickListener(blistener);

        //Affichage du bouton d'aide_couleur et son implémentation dans son listener
        aide_couleur = (ImageButton)findViewById(R.id.aide_couleur);
        aide_couleur.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                //Affichage d'un message d'explication
                Toast toastAuNutella = Toast.makeText(getApplicationContext(), "Ce menu permet de modifier manuellement la luminosité, la couleur ou encore la saturation de l'image.", Toast.LENGTH_LONG);
                toastAuNutella.show();
            }});

        //Affichage du bouton d'aide_filtre et son implémentation dans son listener
        aide_filtre = (ImageButton) findViewById(R.id.aide_filtre);
        aide_filtre.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                //Affichage d'un message d'explication
                Toast toastAuBeurre = Toast.makeText(getApplicationContext(), "Ce menu permet d'appliquer à l'image un filtre de couleur choisie aléatoirement par l'application.", Toast.LENGTH_LONG);
                toastAuBeurre.show();
            }});

        //Affichage du bouton d'aide_gris et son implémentation dans son listener
        aide_gris = (ImageButton) findViewById(R.id.aide_gris);
        aide_gris.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                //Affichage d'un message d'explication
                Toast toastALaConfitureDeFraise = Toast.makeText(getApplicationContext(), "Ce menu permet de mettre l'image en noir et blanc.", Toast.LENGTH_LONG);
                toastALaConfitureDeFraise.show();
            }});

        //Affichage du bouton d'aide_pinch et son implémentation dans son listener
        aide_pinch = (ImageButton) findViewById(R.id.aide_pinch);
        aide_pinch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                //Affichage d'un message d'explication
                Toast toastAuMiel = Toast.makeText(getApplicationContext(), "Ce menu permet de faire un zoom sur l'image.", Toast.LENGTH_LONG);
                toastAuMiel.show();
            }});

        //Affichage du bouton d'aide_ppv et son implémentation dans son listener
        aide_ppv = (ImageButton) findViewById(R.id.aide_ppv);
        aide_ppv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                //Affichage d'un message d'explication
                Toast toastAuSiropDErable = Toast.makeText(getApplicationContext(), "Ce menu permet de déformer l'image grâce à des facteurs multiplicateurs à choisir. Veiller à ne pas utiliser de facteur tropgrand ou trop petit (pas plus de 10 ou pas moins de 0,001).", Toast.LENGTH_LONG);
                toastAuSiropDErable.show();
            }});
    }


    /**
     * implémentation du listener
     */
    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {

                //dans le cas où on appuie sur le bouton gris, on accède au menu de mise en niveaux de gris
                case R.id.gris:
                    Intent second = new Intent(Menu.this, MenuGris.class);
                    startActivity(second);
                    break;

                //dans le cas où on appuie sur le bouton couleur, on accède au menu de changement de couleur, saturation, luminosité
                case R.id.couleur:
                    Intent third = new Intent(Menu.this, MenuCouleur.class);
                    startActivity(third);
                    break;

                //dans le cas où on appuie sur le bouton filtre, on accède au menu de changement de filtre
                case R.id.filtre:
                    Intent fourth = new Intent(Menu.this, MenuFiltre.class);
                    startActivity(fourth);
                    break;

                //dans le cas où on appuie sur le bouton ppv, on accède au menu de déformation (interpolation ppv)
                case R.id.ppv:
                    Intent fifth = new Intent(Menu.this, MenuZoom.class);
                    startActivity(fifth);
                    break;

                //dans le cas où on appuie sur le bouton pinch, on accède au menu de zoom de l'image
                case R.id.pinch:
                    Intent sixth = new Intent(Menu.this, MenuPinchZoom.class);
                    startActivity(sixth);

                case R.id.HE:
                    Intent seven = new Intent(Menu.this, MenuHE.class);
                    startActivity(seven);

                case R.id.teinte:
                    Intent eight = new Intent(Menu.this, MenuChoixTeinte.class);
                    startActivity(eight);

                case R.id.sepia:
                    Intent nine = new Intent(Menu.this, MenuSepia.class);
                    startActivity(nine);

                    //par défaut, on ne fait rien
                default:
                    break;
            }
        }
    };
}
