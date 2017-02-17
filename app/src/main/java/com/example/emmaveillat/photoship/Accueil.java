package com.example.emmaveillat.photoship;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * L'accueil sert de page d'accueil à l'application. Elle envoie directement au menu de chargement de photo.
 * @see  ChargementPhoto
 * @author emmaveillat
 */
public class Accueil extends AppCompatActivity {

    /**
     * Bouton qui amène au menu de chargement des photos
     */
    Button go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        //Affichage du bouton et implémentation du bouton dans le listener
        go = (Button) findViewById(R.id.go);
        go.setOnClickListener(blistener);
    }

    /**
     * Implémentation du listener
     */
    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {

                //Quand on appuie sur le bouton, ça amène au menu de chargement des images
                case R.id.go:
                    Intent second = new Intent(Accueil.this, ChargementPhoto.class);
                    startActivity(second);
                    break;

                //par défaut, on ne fait rien
                default:
                    break;
            }
        }
    };
}
