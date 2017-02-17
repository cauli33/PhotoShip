package com.example.emmaveillat.photoship;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Le Menu PinchZoom permet de zoomer l'image en "écartant les doigts sur l'écran"/
 * L'image affichée est celle choisie est celle provenant du men de chargement de photo.
 * Pour modifier la taille de l'image manuellement, veuillez vous référer au menu Zoom.
 * @see  MenuZoom
 * @author emmaveillat
 */
public class MenuPinchZoom extends AppCompatActivity {

    /**
     * Image chargée à partir de la galerie
     */
    Bitmap pictureToUse;

    /**
     * Affichage de l'image
     */
    ImageView myImageView;

    /**
     *Image à zoomer
     */
    Bitmap picture;

    /**
     * Taille de l'image
     */
    int bmpWidth, bmpHeight;

    /**
     * Statut d'activité de l'utilisateur (inactivité, clic ou zoom)
     */
    int touchState;
    final int IDLE = 0;
    final int TOUCH = 1;
    final int PINCH = 2;

    //Distances
    float dist0, distCurrent;

    //Texte à afficher
    TextView txt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_pinch_zoom);

        //Préparation de l'affichage de l'image à zoomer
        myImageView = (ImageView)findViewById(R.id.imageview);

        //Récupération de l'image de la galerie
        pictureToUse = ChargementPhoto.scaleImage();

        //Création d'une image modifiable à partir de celle chargée
        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        //Récupération de la taille de l'image
        bmpWidth = picture.getWidth();
        bmpHeight = picture.getHeight();

        //Distances par défaut
        distCurrent = 1;
        dist0 = 1;
        drawMatrix();

        //implémentation de l'imageView via le MyOnTouchListener
        myImageView.setOnTouchListener(MyOnTouchListener);
        touchState = IDLE;

        //Affichage du coefficient de zoom
        txt = (TextView) findViewById(R.id.zoomfactor);
        txt.setText(String.valueOf(distCurrent/dist0));
    }

    /**
     * fonction pour afficher l'image zoomée en fonction des distances
     */
    private void drawMatrix(){
        float curScale = distCurrent/dist0;
        if (curScale < 0.1){
            curScale = 0.1f;
        }

        //Création d'une nouvelle image correspond au zoom de la précédente
        Bitmap resizedBitmap;
        int newHeight = (int) (bmpHeight * curScale);
        int newWidth = (int) (bmpWidth * curScale);
        resizedBitmap = Bitmap.createScaledBitmap(picture, newWidth, newHeight, false);
        myImageView.setImageBitmap(resizedBitmap);
    }

    /**
     * Implémentation du MyOnTouchListener en fonction du mouvement des doigts sur l'écran
     */
    View.OnTouchListener MyOnTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // TODO Auto-generated method stub
            float distx, disty;
            switch(event.getAction() & MotionEvent.ACTION_MASK) {

                //dans le cas où on touche l'écran avec un seul doigt, l'application considère seulement qu'on clique
                case MotionEvent.ACTION_DOWN:
                    touchState = TOUCH;

                    //Affichage du coefficient de zoom
                    txt = (TextView) findViewById(R.id.zoomfactor);
                    txt.setText(String.valueOf(distCurrent/dist0));
                    break;

                //dans le cas où on touche l'écran avec deux doigts, l'application considère qu'on zoome l'image
                case MotionEvent.ACTION_POINTER_DOWN:
                    touchState = PINCH;
                    //on calcule les distances afin de définir les coefficients de zoom
                    distx = event.getX(0) - event.getX(1);
                    disty = event.getY(0) - event.getY(1);
                    dist0 = (float) Math.sqrt(distx * distx + disty * disty);

                    //Affichage du coefficient de zoom
                    txt = (TextView) findViewById(R.id.zoomfactor);
                    txt.setText(String.valueOf(distCurrent/dist0));
                    break;

                //dans le cas où on "bouge" sur l'écran, l'application considère qu'on zoome l'image
                case MotionEvent.ACTION_MOVE:
                    if (touchState == PINCH) {
                        //on calcule les distances afin de définir les coefficients de zoom
                        distx = event.getX(0) - event.getX(1);
                        disty = event.getY(0) - event.getY(1);
                        distCurrent =  (float) Math.sqrt(distx * distx + disty * disty);
                        drawMatrix();

                        //Affichage du coefficient de zoom
                        txt = (TextView) findViewById(R.id.zoomfactor);
                        txt.setText(String.valueOf(distCurrent/dist0));
                    }
                    break;

                //dans le cas où on lève les deux doigts de l'écran,l'application est "en attente" (inactivité de l'utilisateur)
                case MotionEvent.ACTION_UP:
                    touchState = IDLE;

                    //Affichage du coefficient de zoom
                    txt = (TextView) findViewById(R.id.zoomfactor);
                    txt.setText(String.valueOf(distCurrent/dist0));
                    break;

                //dans le cas où on lève un doigt de l'écran, l'application considère seulement qu'on clique
                case MotionEvent.ACTION_POINTER_UP:
                    touchState = TOUCH;

                    //Affichage du coefficient de zoom
                    txt = (TextView) findViewById(R.id.zoomfactor);
                    txt.setText(String.valueOf(distCurrent/dist0));
                    break;
            }
                return true;
            }

        };
    }


