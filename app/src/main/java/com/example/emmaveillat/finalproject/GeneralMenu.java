package com.example.emmaveillat.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class GeneralMenu extends AppCompatActivity {

    Button ppv, pinch, gris, couleur, filtre, ED, teinte, sepia, HE;

    ImageButton aide_ppv, aide_pinch, aide_gris, aide_couleur, aide_filtre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_menu);

        pinch = (Button) findViewById(R.id.pinch);
        pinch.setOnClickListener(blistener);

        teinte = (Button) findViewById(R.id.teinte);
        teinte.setOnClickListener(blistener);

        ppv = (Button) findViewById(R.id.ppv);
        ppv.setOnClickListener(blistener);

        gris = (Button) findViewById(R.id.gris);
        gris.setOnClickListener(blistener);

        couleur = (Button) findViewById(R.id.couleur);
        couleur.setOnClickListener(blistener);

        filtre = (Button) findViewById(R.id.filtre);
        filtre.setOnClickListener(blistener);

        ED = (Button) findViewById(R.id.ED);
        ED.setOnClickListener(blistener);

        HE = (Button) findViewById(R.id.HE);
        HE.setOnClickListener(blistener);

        sepia = (Button) findViewById(R.id.sepia);
        sepia.setOnClickListener(blistener);

        aide_couleur = (ImageButton)findViewById(R.id.aide_couleur);
        aide_couleur.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Toast toastAuNutella = Toast.makeText(getApplicationContext(), "Ce menu permet de modifier manuellement la luminosité, la couleur ou encore la saturation de l'image.", Toast.LENGTH_LONG);
                toastAuNutella.show();
            }});

        aide_filtre = (ImageButton) findViewById(R.id.aide_filtre);
        aide_filtre.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Toast toastAuBeurre = Toast.makeText(getApplicationContext(), "Ce menu permet d'appliquer à l'image un filtre de couleur choisie aléatoirement par l'application.", Toast.LENGTH_LONG);
                toastAuBeurre.show();
            }});

        aide_gris = (ImageButton) findViewById(R.id.aide_gris);
        aide_gris.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Toast toastALaConfitureDeFraise = Toast.makeText(getApplicationContext(), "Ce menu permet de mettre l'image en noir et blanc.", Toast.LENGTH_LONG);
                toastALaConfitureDeFraise.show();
            }});

        aide_pinch = (ImageButton) findViewById(R.id.aide_pinch);
        aide_pinch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Toast toastAuMiel = Toast.makeText(getApplicationContext(), "Ce menu permet de faire un zoom sur l'image.", Toast.LENGTH_LONG);
                toastAuMiel.show();
            }});

        aide_ppv = (ImageButton) findViewById(R.id.aide_ppv);
        aide_ppv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Toast toastAuSiropDErable = Toast.makeText(getApplicationContext(), "Ce menu permet de déformer l'image grâce à des facteurs multiplicateurs à choisir. Veiller à ne pas utiliser de facteur tropgrand ou trop petit (pas plus de 10 ou pas moins de 0,001).", Toast.LENGTH_LONG);
                toastAuSiropDErable.show();
            }});
    }


    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {

                case R.id.gris:
                    Intent second = new Intent(GeneralMenu.this, GrayMenu.class);
                    startActivity(second);
                    break;

                case R.id.couleur:
                    Intent third = new Intent(GeneralMenu.this, ColorMenu.class);
                    startActivity(third);
                    break;

                case R.id.filtre:
                    Intent fourth = new Intent(GeneralMenu.this, FilterMenu.class);
                    startActivity(fourth);
                    break;

                case R.id.ppv:
                    Intent fifth = new Intent(GeneralMenu.this, ZoomMenu.class);
                    startActivity(fifth);
                    break;

                case R.id.pinch:
                    Intent sixth = new Intent(GeneralMenu.this, PinchZoomMenu.class);
                    startActivity(sixth);
                    break;

                case R.id.ED:
                    Intent seven = new Intent(GeneralMenu.this, ExtensionDynamic.class);
                    startActivity(seven);
                    break;


                case R.id.teinte:
                    Intent eight = new Intent(GeneralMenu.this, ChoiceMenu.class);
                    startActivity(eight);
                    break;

                case R.id.sepia:
                    Intent nine = new Intent(GeneralMenu.this, SepiaMenu.class);
                    startActivity(nine);
                    break;

                case R.id.HE:
                    Intent ten = new Intent(GeneralMenu.this, EgalizationHistogramm.class);
                    startActivity(ten);
                    break;

                default:
                    break;
            }
        }
    };
}
