package com.example.emmaveillat.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import static com.example.emmaveillat.finalproject.R.id.imgView;

/**
 * La classe ChargementPhoto aide l'utilisateur à choisir l'image qu'il veut transformer. Il peut
 * ainsi accéder à sa galerie d'images ou à l'appareil photo de son appareil. Dans ce cas-là, la
 * prise de photo l'entraîne dans la galerie où il pourra trouver la photo qu'il a prise dans un
 * dossier dédié à l'application
 */
public class ChargementPhoto extends Activity {

    /**
     * Résultat pour sélectionner l'appareil photo ou la galerie
     */
    private static int tmp_resultat = 1;

    /**
     * Chemin d'accès de l'image choisie
     */
    static String cheminImg;

    /**
     * Bouton pour confirmer le choix de l'image
     */
    Button choix;

    /**
     * l'image choisie à modifier
     */
    public Bitmap imgUtilisee;

    /**
     * Vue de l'image choisie par l'utilisateur
     */
    public ImageView imageView;

    /**
     * Entier pour accéder à la caméra
     */
    private static final int accesCamera = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chargement_photo);

        //Vue du bouton pour valider son choix invisible lors du premier accès à l'application.
        choix = (Button) findViewById(R.id.choix);
        choix.setOnClickListener(blistener);
        choix.setActivated(false);
        choix.setVisibility(View.INVISIBLE);

        //Accès à la caméra si appui sur le bouton correspondant.
        Button boutonCamera = (Button) this.findViewById(R.id.camera);
        boutonCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, accesCamera);
            }
        });
    }

    /**
     * fonction pour accéder à la galerie d'images grâce à une nouvelle vue.
     * @param view la nouvelle vue utilisée pour la galerie
     */
    public void chargementImage(View view) {
        Intent galerie = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.
                EXTERNAL_CONTENT_URI);
        startActivityForResult(galerie, tmp_resultat);
    }

    @Override
    protected void onActivityResult(int codeRequis, int resultat, Intent data) {
        super.onActivityResult(codeRequis, resultat, data);
        try {
            //pour l'accès à la galerie
            if (codeRequis == tmp_resultat && resultat == RESULT_OK && null != data) {
                //on récupère l'image et ses informations comme le chemin lors de sa sélection par
                // toucher.
                Uri imgSelec = data.getData();
                imgUtilisee = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                        imgSelec);
                String[] donneesChemin = { MediaStore.Images.Media.DATA };

                Cursor curseur = getContentResolver().query(imgSelec, donneesChemin, null, null,
                        null);
                curseur.moveToFirst();
                int indexColonne = curseur.getColumnIndex(donneesChemin[0]);

                //Récupération du chemin de l'image.
                cheminImg = curseur.getString(indexColonne);
                curseur.close();

                //Affichage de l'image choisie et du bouton pour valider le choix.
                imageView = (ImageView) findViewById(imgView);
                imageView.setImageBitmap(BitmapFactory.decodeFile(cheminImg));
                choix.setActivated(true);
                choix.setVisibility(View.VISIBLE);

            }

            //pour l'accès à la caméra.
            else if (codeRequis == accesCamera && resultat == Activity.RESULT_OK && null != data) {
                //l'image prise via la caméra est stockée dans la galerie et l'utilisateur y est
                // envoyé
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                cheminImg = "newPhoto" + Math.random();
                MediaStore.Images.Media.insertImage(getContentResolver(), photo, cheminImg, "photo " +
                        "from camera");

                chargementImage(imageView);


            }
            //Affichage messages d'erreurs
            else {
                Toast.makeText(this, "Vous n'avez pas choisi d'image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Quelque chose a mal fonctionné, veuillez réessayer.", Toast.
                    LENGTH_LONG).show();
        }

    }

    /**
     * fonction permettant de récupérer l'image choisie dans une autre activité grâce à son chemin.
     * @return l'image choisie précédemment
     */
    protected static Bitmap scaleImage() {
        Bitmap nad = BitmapFactory.decodeFile(cheminImg);
        int largeur = nad.getWidth();
        int hauteur = nad.getHeight();
        if (nad.getWidth() * nad.getHeight() > 3000000) {
            int nl, nh;
            float fact;
            if (largeur > hauteur) {
                nl = 1920;
                nh = nl * hauteur / largeur;
            } else {
                nh = 1920;
                nl = nh * largeur / hauteur;
            }
            return Bitmap.createScaledBitmap(nad, nl, nh, false);
        }
        return nad;
    }


    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {
                //dans le cas où l'image a été choisie, cela permet l'accès au menu general de
                //l'application.
                case R.id.choix:
                    if (imgUtilisee != null){
                        Intent second = new Intent(ChargementPhoto.this, MenuGeneral.class);
                        startActivity(second);}
                    break;

                default:
                    break;
            }
        }
    };
}

