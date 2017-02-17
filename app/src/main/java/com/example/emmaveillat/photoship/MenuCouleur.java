package com.example.emmaveillat.photoship;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * Le MenuCouleur permet à l'utilisateur de changer manuellement la couleur, la luminosité et la saturation de l'image choisie.
 * L'image choisie provient de celle chargée dans le menu de chargement de photo.
 * @see  ChargementPhoto
 * @author emmaveillat
 */
public class MenuCouleur extends AppCompatActivity {

    /**
     * Valeur de hue
     */
    Float hue;

    /**
     * Valeur de saturation
     */
    Float satur;

    /**
     * Valeur de value
     */
    Float value;


    /**
     * Bouton pour sauvegarder
     */
    Button save;

    /**
     * Affichage de l'image modifiée
     */
    ImageView imageResult;

    /**
     * Barres de progression pour les valeurs de hue, saturation et luminosité
     */
    SeekBar hueBar, satBar, valBar;

    /**
     * Affichage de légendes
     */
    TextView hueText, satText, valText;

    /**
     * Bouton pour annuler les modifications
     */
    Button reset;

    /**
     * Image provenant de la galerie
     */
    Bitmap picture;

    /**
     * Image qu'on va modifier
     */
    Bitmap pictureToUse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_couleur);

        //Récupération de l'image à partir de celle choisie dans la galerie
        pictureToUse = ChargementPhoto.scaleImage();

        //copie de l'image de base dans une image modifiable
        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        //Affichage de l'image dans l'application
        imageResult = (ImageView) findViewById(R.id.result);
        imageResult.setImageBitmap(picture);

        //Affichage du bouton save et implémentation du bouton dans le listener
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        // Affichage des légendes des barres de progression
        hueText = (TextView) findViewById(R.id.texthue);
        satText = (TextView) findViewById(R.id.textsat);
        valText = (TextView) findViewById(R.id.textval);

        //Affichage des barres de progression et leurs implémentations dans un listener spécifique
        hueBar = (SeekBar) findViewById(R.id.huebar);
        satBar = (SeekBar) findViewById(R.id.satbar);
        valBar = (SeekBar) findViewById(R.id.valbar);
        hueBar.setOnSeekBarChangeListener(seekBarChangeListener);
        satBar.setOnSeekBarChangeListener(seekBarChangeListener);
        valBar.setOnSeekBarChangeListener(seekBarChangeListener);

        //Affichage du bouton reset et son implémentation du listener
        reset = (Button)findViewById(R.id.reset);
        reset.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View arg0) {
                hueBar.setProgress(256);
                satBar.setProgress(256);
                valBar.setProgress(256);
                loadBitmapHSV();
            }});
    }


    /**
     * Implémentation du listener spécifique aux barres de progresion.
     * L'image charge les modifications à chaque moment où on manipule la barre de progression
     *
     */
    OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
        /**
         * fonction qui charge l'image pendant qu'on manipule la barre de progression
         * @param seekBar la barre de progression qu'on utilise
         * @param progress entier correspondant à la valeur de la barre
         * @param fromUser booléen témoin
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { loadBitmapHSV(); }

        /**
         * fonction qui charge l'image au début de la manipulation de la barre de progression
         * @param seekBar la barre de progression qu'on utilise
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { loadBitmapHSV(); }

        /**
         * fonction qui charge l'image à la fin de la manipulation de la barre de progression
         * @param seekBar la barre de progression qu'on utilise
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            loadBitmapHSV();
        }
    };

    /**
     * fonction qui charge l'affichage de l'image dont le domaine HSV est modifié en fonction de la valeur des barres de progression
     */
    private void loadBitmapHSV() {
        if (picture != null) {

            int progressHue = hueBar.getProgress() - 256;
            int progressSat = satBar.getProgress() - 256;
            int progressVal = valBar.getProgress() - 256;

            float hue = (float) progressHue * 360 / 256;
            float sat = (float) progressSat / 256;
            float val = (float) progressVal / 256;

            hueText.setText("Hue: " + String.valueOf(hue));
            satText.setText("Saturation: " + String.valueOf(sat));
            valText.setText("Value: " + String.valueOf(val));

            imageResult.setImageBitmap(updateHSV(picture, hue, sat, val));

        }
    }

    /**
     * fonction qui crée une nouvelle image à partir de l'image de base avec des nouvelles valeurs de hue, saturation et luminosité
     * @param src l'image à modifier
     * @param settingHue la nouvelle valeur de hue à appliquer
     * @param settingSat la nouvelle valeur de saturation à appliquer
     * @param settingVal la nouvelle valeur de luminosité à appliquer
     * @return une nouvelle image avec les modifications de celle en paramètre
     */
    private Bitmap updateHSV(Bitmap src, float settingHue, float settingSat, float settingVal) {

        //attribution des valeurs de hue, saturation et luminosité en fonction de celles passées en paramètre
        hue = settingHue;
        satur = settingSat;
        value = settingVal;

        //Création de deux tableaux d'entiers à partir de la taille de l'image
        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrcColor = new int[w * h];
        int[] mapDestColor = new int[w * h];

        //Création d'un tableau de floats de taille 3
        float[] pixelHSV = new float[3];

        //Récupération des pixels dans le tableau source de l'image passée en paramètre
        src.getPixels(mapSrcColor, 0, w, 0, 0, w, h);

        int index = 0;
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {

                // Conversion de l'espace RGB à l'espace HSV
                Color.colorToHSV(mapSrcColor[index], pixelHSV);

                //Ajustement de la valeur de hue
                pixelHSV[0] = pixelHSV[0] + settingHue;
                if (pixelHSV[0] < 0.0f) {
                    pixelHSV[0] = 0.0f;
                } else if (pixelHSV[0] > 360.0f) {
                    pixelHSV[0] = 360.0f;
                }

                //Ajustement de la valeur de saturation
                pixelHSV[1] = pixelHSV[1] + settingSat;
                if (pixelHSV[1] < 0.0f) {
                    pixelHSV[1] = 0.0f;
                } else if (pixelHSV[1] > 1.0f) {
                    pixelHSV[1] = 1.0f;
                }

                //Ajustement de la valeur de luminosité
                pixelHSV[2] = pixelHSV[2] + settingVal;
                if (pixelHSV[2] < 0.0f) {
                    pixelHSV[2] = 0.0f;
                } else if (pixelHSV[2] > 1.0f) {
                    pixelHSV[2] = 1.0f;
                }

                //Reconversion de l'espace HSV à l'espace RGB
                mapDestColor[index] = Color.HSVToColor(pixelHSV);

                index++;
            }
        }

        //Création de la nouvelle image avec les modifications
        return Bitmap.createBitmap(mapDestColor, w, h, Config.ARGB_8888);

    }

    /**
     * Implémentation du listener
     */
    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {

                //dans le cas où on appuie sur le bouton save, l'image est créée dans la galerie. On est ensuite renvoyé au menu de chargement de photos
                case R.id.save:
                    Bitmap pictureFinal = (updateHSV(picture, hue, satur, value)).copy(Bitmap.Config.ARGB_8888, true);
                    MediaStore.Images.Media.insertImage(getContentResolver(), pictureFinal, ChargementPhoto.imgDecodableString + "_couleur" , "");
                    Intent second = new Intent(MenuCouleur.this, ChargementPhoto.class);
                    startActivity(second);
                    break;
                default:
                    break;
            }
        }
    };

}