package com.example.emmaveillat.finalproject;

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
 * @author emmaveillat
 */
public class ChoiceMenu extends AppCompatActivity {

    /**
     * Valeur de hue
     */
    Float hue;

    /**
     * Valeur de saturation
     */
    Float gap;


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
    SeekBar hueBar, gapBar;

    /**
     * Affichage de légendes
     */
    TextView hueText, gapText;

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
        setContentView(R.layout.activity_choice_menu);

        //Récupération de l'image à partir de celle choisie dans la galerie
        pictureToUse = PhotoLoading.scaleImage();

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
        gapText = (TextView) findViewById(R.id.textgap);

        //Affichage des barres de progression et leurs implémentations dans un listener spécifique
        hueBar = (SeekBar) findViewById(R.id.huebar);
        gapBar = (SeekBar) findViewById(R.id.gapbar);
        hueBar.setOnSeekBarChangeListener(seekBarChangeListener);
        gapBar.setOnSeekBarChangeListener(seekBarChangeListener);

        //Affichage du bouton reset et son implémentation du listener
        reset = (Button)findViewById(R.id.reset);
        reset.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View arg0) {
                hueBar.setProgress(0);
                gapBar.setProgress(0);
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

            int hue = hueBar.getProgress();
            int gap = gapBar.getProgress();

            hueText.setText("Hue: " + String.valueOf(hue));
            gapText.setText("Gap: " + String.valueOf(gap));

            float settingHue = (float) hue;
            float settingGap = (float) gap;
            imageResult.setImageBitmap(updateHSV(picture, settingHue, settingGap));

        }
    }

    /**
     * fonction qui crée une nouvelle image à partir de l'image de base avec des nouvelles valeurs de hue, saturation et luminosité
     * @param src l'image à modifier
     * @param settingHue la nouvelle valeur de hue à appliquer
     * @param settingGap la nouvelle valeur de gap à appliquer
     * @return une nouvelle image avec les modifications de celle en paramètre
     */
    private Bitmap updateHSV(Bitmap src, float settingHue, float settingGap) {

        //attribution des valeurs de hue, saturation et luminosité en fonction de celles passées en paramètre
        hue = settingHue;
        gap = settingGap;

        //Création de deux tableaux d'entiers à partir de la taille de l'image
        int w = src.getWidth();
        int h = src.getHeight();
        int[] mapSrc = new int[w * h];

        //Création d'un tableau de floats de taille 3
        float[] pixelHSV = new float[3];

        //Récupération des pixels dans le tableau source de l'image passée en paramètre
        src.getPixels(mapSrc, 0, w, 0, 0, w, h);

        int index = 0;
        if ((hue >= gap)&&(hue<=360F - gap)){
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    int pixel = mapSrc[index];

                    // Conversion de l'espace RGB à l'espace HSV
                    Color.colorToHSV(pixel, pixelHSV);
                    float pixelHue = pixelHSV[0];

                    if ((pixelHue >= hue + gap) || (pixelHue <= hue - gap)) {
                        int red = Color.red(pixel);
                        int blue = Color.blue(pixel);
                        int green = Color.green(pixel);
                /* Je fais la moyenne de ces 3 valeurs et donne au pixel du bitmap de sortie le niveau de gris associé */
                        int gray = (red + blue + green) / 3;
                        mapSrc[index] = Color.rgb(gray, gray, gray);
                    }
                    //Reconversion de l'espace HSV à l'espace RGB
                    index++;
                }
            }
        }
        else if (hue < gap){
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    int pixel = mapSrc[index];

                    // Conversion de l'espace RGB à l'espace HSV
                    Color.colorToHSV(pixel, pixelHSV);
                    float pixelHue = pixelHSV[0];

                    if ((pixelHue >= hue + gap) && (pixelHue <= hue + 360F - gap)) {
                        int red = Color.red(pixel);
                        int blue = Color.blue(pixel);
                        int green = Color.green(pixel);
                /* Je fais la moyenne de ces 3 valeurs et donne au pixel du bitmap de sortie le niveau de gris associé */
                        int gray = (red + blue + green) / 3;
                        mapSrc[index] = Color.rgb(gray, gray, gray);
                    }
                    //Reconversion de l'espace HSV à l'espace RGB
                    index++;
                }
            }
        }
        else{
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    int pixel = mapSrc[index];

                    // Conversion de l'espace RGB à l'espace HSV
                    Color.colorToHSV(pixel, pixelHSV);
                    float pixelHue = pixelHSV[0];

                    if ((pixelHue >= hue -360F + gap) && (pixelHue <= hue - gap)) {
                        int red = Color.red(pixel);
                        int blue = Color.blue(pixel);
                        int green = Color.green(pixel);
                /* Je fais la moyenne de ces 3 valeurs et donne au pixel du bitmap de sortie le niveau de gris associé */
                        int gray = (red + blue + green) / 3;
                        mapSrc[index] = Color.rgb(gray, gray, gray);
                    }
                    //Reconversion de l'espace HSV à l'espace RGB
                    index++;
                }
            }
        }
        //Création de la nouvelle image avec les modifications
        return Bitmap.createBitmap(mapSrc, w, h, Config.ARGB_8888);
    }

    /**
     * Implémentation du listener
     */
    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {

                //dans le cas où on appuie sur le bouton save, l'image est créée dans la galerie. On est ensuite renvoyé au menu de chargement de photos
                case R.id.save:
                    Bitmap pictureFinal = (updateHSV(picture, hue, gap)).copy(Bitmap.Config.ARGB_8888, true);
                    MediaStore.Images.Media.insertImage(getContentResolver(), pictureFinal, PhotoLoading.imgDecodableString + "_couleur" , "");
                    Intent second = new Intent(ChoiceMenu.this, PhotoLoading.class);
                    startActivity(second);
                    break;
                default:
                    break;
            }
        }
    };

}
