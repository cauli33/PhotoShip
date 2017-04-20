package com.example.emmaveillat.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * La classe LogoUniversite sert de page de présentation à l'application. L'utilisateur voit
 * le logo de l'université durant un cours laps de temps.
 * Il s'agit du code de Lost in Bielefeld, trouvé sur le forum Stackoverflow, et adapté à notre
 * application.
 */

public class LogoUniversite extends Activity{
    //Durée d'affichage de la page d'accueil
    private final int dureeAffichage = 1000;

    @Override
    public void onCreate(Bundle accueil) {
        super.onCreate(accueil);
        setContentView(R.layout.activity_logo_universite);

        //Démarrage de la page d'accueil et fermeture après un temps limité.
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                //Lien entre la page d'accueil et la page de chargement d'images.
                Intent photo = new Intent(LogoUniversite.this,PageAccueil.class);
                LogoUniversite.this.startActivity(photo);
                LogoUniversite.this.finish();
            }
        }, dureeAffichage);
    }
}
