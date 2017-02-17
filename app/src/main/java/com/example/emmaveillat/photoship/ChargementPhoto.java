package com.example.emmaveillat.photoship;

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

/**
 * Le menu ChargementPhoto permet d'accéder à la galerie afin de choisir soi-même l'image à modifier.
 * On accède ensuite au menu des modifications possibles sur l'image.
 * @see Menu
 * @author emmaveillat
 */
public class ChargementPhoto extends Activity {

    private static int RESULT_LOAD_IMG = 1;

    /**
     * Nom de l'image prise de la galerie
     */
    static String imgDecodableString;

    /**
     * Bouton pour accéder au menu des modifications
     */
    Button choice;

    /**
     * Image à choisir dans la galerie qu'on va modifier
     */
    public Bitmap pictureToUse;

    private static final int CAMERA_REQUEST = 1888;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chargement_photo);

        //Affichage du bouton choice et implémentation du bouton dans le blistener
        choice = (Button) findViewById(R.id.choice);
        choice.setOnClickListener(blistener);

        Button photoButton = (Button) this.findViewById(R.id.button1);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    /**
     * fonction qui permet d'accéder à un lieu de stockage des images (la galerie par exemple) et de l'afficher
     * @param view l'affichage dédié à la galerie
     */
    public void chargementImage(View view) {
        // création d'un intent pour accéder à la galerie et choisir une image
        Intent galerie = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //démarrage de l'intent
        startActivityForResult(galerie, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            //Quand on choisit l'image
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {

                //Récupération de l'image à partir des données
                Uri selectedImage = data.getData();
                pictureToUse = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                //Récupération du nom de l'image
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                //on affiche l'image via un imageView après avoir décodé en fonction de son nom
                ImageView imgView = (ImageView) findViewById(R.id.imgView);
                imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));


            }
            //TODO Generate method "get picture from Camera"

            else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                MediaStore.Images.Media.insertImage(getContentResolver(), photo, ChargementPhoto.imgDecodableString, "");
                pictureToUse = photo.copy(Bitmap.Config.ARGB_8888, false);
                ImageView imgView = (ImageView) findViewById(R.id.imgView);
                imgView.setImageBitmap(photo);
            } else {
                //affichage d'un message d'erreur
                Toast.makeText(this, "Vous n'avez pas choisi d'image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            //affichage d'un message d'erreur
            Toast.makeText(this, "Quelque chose a mal fonctionné, veuillez réessayer.", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Fonction pour pouvoir récupérer l'image après son chargement
     * @return Bitmap l'image choisie dans la galerie
     */
    protected static Bitmap scaleImage() {
        Bitmap nad = BitmapFactory.decodeFile(imgDecodableString);
        return nad;
    }

    /**
     * instanciation du blistener pour définir les actions engendrées par le bouton choice
     */
    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {
                //dans le cas où on appuie sur le bouton choice, ce qui amène au menu des modifications
                case R.id.choice :
                    if (pictureToUse != null){
                        Intent second = new Intent(ChargementPhoto.this, Menu.class);
                        startActivity(second);}
                break;

                //par défaut, on ne fait rien
                default:
                    break;
            }
        }
    };
}
