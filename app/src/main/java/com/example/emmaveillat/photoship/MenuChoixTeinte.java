package com.example.emmaveillat.photoship;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MenuChoixTeinte extends AppCompatActivity {

    int interv;

    /**
     * Valeur de hue
     */
    float teinte;

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
    SeekBar teinteBar, intervalleBar;

    /**
     * Affichage de légendes
     */
    TextView teinteText, intervalleText;

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
    Bitmap newPicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_choix_teinte);

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
        teinteText = (TextView) findViewById(R.id.teinteText);
        intervalleText = (TextView) findViewById(R.id.intervalleText);


        //Affichage des barres de progression et leurs implémentations dans un listener spécifique
        teinteBar = (SeekBar) findViewById(R.id.teinteBar);
        teinteBar.setOnSeekBarChangeListener(seekBarChangeListener);

        intervalleBar = (SeekBar) findViewById(R.id.intervalleBar);
        intervalleBar.setOnSeekBarChangeListener(seekBarChangeListener);


        //Affichage du bouton reset et son implémentation du listener
        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                intervalleBar.setProgress(256);
                teinteBar.setProgress(256);
                loadBitmapHSV();
            }
        });
    }


    /**
     * Implémentation du listener spécifique aux barres de progresion.
     * L'image charge les modifications à chaque moment où on manipule la barre de progression
     */
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
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

            int progressHue = teinteBar.getProgress() - 256;
            float hue = (float) progressHue * 360 / 256;

            //inverser la seekbar ?
            int progressInter = intervalleBar.getProgress() - 255;
            int intervalle = progressInter * (-1);

            teinteText.setText("Hue: " + String.valueOf(hue));
            intervalleText.setText("Intervalle: " + String.valueOf(intervalle));
            newPicture = picture.copy(Bitmap.Config.ARGB_8888, true);
            updateHSV(newPicture, hue, intervalle);

            imageResult.setImageBitmap(newPicture);

        }
    }

    //TODO Generate method
    /**
     * fonction qui crée une nouvelle image à partir de l'image de base avec des nouvelles valeurs de hue, saturation et luminosité
     *
     * @param bmp        l'image à modifier
     * @param settingHue la nouvelle valeur de hue à appliquer
     * @return une nouvelle image avec les modifications de celle en paramètre
     */
    private void updateHSV(Bitmap bmp, float settingHue, int intervalle) {

        //attribution des valeurs de hue, saturation et luminosité en fonction de celles passées en paramètre
        teinte = settingHue;
        interv = intervalle;

        //Création de deux tableaux d'entiers à partir de la taille de l'image
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        float [] actualPixel = new float [3];
        float [] settingPixel = new float [3];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                //Récupération du pixel[i,j]
                int actualpixel = bmp.getPixel(i, j);
                Color.colorToHSV(actualpixel,actualPixel);
                settingPixel[0] = settingHue;
                settingPixel[1] = actualPixel[1];

                settingPixel[2] = actualPixel[2];

                int settingpixel = Color.HSVToColor(settingPixel);

                int actualred = Color.red(actualpixel);
                int actualblue = Color.blue(actualpixel);
                int actualgreen = Color.green(actualpixel);

                if (!((actualpixel < settingpixel + intervalle ) && (actualpixel > settingpixel - intervalle))) {
                    int moy = (actualred + actualblue + actualgreen) / 3;
                    int gray = Color.rgb(moy, moy, moy);
                    bmp.setPixel(i, j, gray);
                }
            }
        }
    }

    /**
     * Implémentation du listener
     */
    private View.OnClickListener blistener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                default:
                    break;
            }
        }
    };

}

