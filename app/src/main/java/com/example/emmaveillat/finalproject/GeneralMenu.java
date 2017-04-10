package com.example.emmaveillat.finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * This class is a general menu where the user can choose the transformation he wants to apply to his bitmap.
 */
public class GeneralMenu extends AppCompatActivity {

    ImageView img;
    /**
     * Buttons to access the menus
     */

    BitmapList memory;
    MyBitmap current;
    Bitmap pictureFromGallery;
    ImageButton gris, sepia, moy, gauss, sobel, laplacien, filtre, ED, teinte, HE, crop, rotate;
    Button ppv, pinch, couleur, replace, finger;

    /**
     * the dimensions of the bitmap
     */
    int bmpWidth, bmpHeight;

    /**
     * statments of the fingers on the screen
     */
    int touchState;
    final int IDLE = 0;
    final int TOUCH = 1;
    final int PINCH = 2;

    /**
     * distances from the fingers
     */
    float dist0, distCurrent;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.general_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_menu);

        pictureFromGallery = PhotoLoading.scaleImage();
        //copies the original bitmap to be mutable

        current = new MyBitmap(pictureFromGallery.copy(Bitmap.Config.ARGB_8888, true), 0);
        //Objects displayed in the menu
        img = (ImageView) findViewById(R.id.picture);
        img.setImageBitmap(current.bmp);

        memory = new BitmapList(current);


        //initialization of the distances
        distCurrent = 1;
        dist0 = 1;
        drawMatrix();

        img.setOnTouchListener(MyOnTouchListener);
        touchState = IDLE;

        //Sets buttons
        pinch = (Button) findViewById(R.id.pinch);
        pinch.setOnClickListener(blistener);

        teinte = (ImageButton) findViewById(R.id.teinte);
        teinte.setOnClickListener(blistener);

        ppv = (Button) findViewById(R.id.ppv);
        ppv.setOnClickListener(blistener);

        gris = (ImageButton) findViewById(R.id.gray);
        gris.setOnClickListener(blistener);

        couleur = (Button) findViewById(R.id.color);
        couleur.setOnClickListener(blistener);

        filtre = (ImageButton) findViewById(R.id.filtre);
        filtre.setOnClickListener(blistener);

        ED = (ImageButton) findViewById(R.id.ED);
        ED.setOnClickListener(blistener);

        HE = (ImageButton) findViewById(R.id.HE);
        HE.setOnClickListener(blistener);

        sepia = (ImageButton) findViewById(R.id.sepia);
        sepia.setOnClickListener(blistener);

        moy = (ImageButton) findViewById(R.id.moy);
        moy.setOnClickListener(blistener);

        gauss = (ImageButton) findViewById(R.id.gauss);
        gauss.setOnClickListener(blistener);

        sobel = (ImageButton) findViewById(R.id.sobel);
        sobel.setOnClickListener(blistener);

        laplacien = (ImageButton) findViewById(R.id.laplacien);
        laplacien.setOnClickListener(blistener);

        crop = (ImageButton) findViewById(R.id.crop);
        crop.setOnClickListener(blistener);
    }

    /**
     * function which draw a matrix depending on the distances and creates the new zoomed bitmap
     */
    private void drawMatrix(){
        float curScale = distCurrent/dist0;
        if (curScale < 0.1){
            curScale = 0.1f;
        }

        Bitmap resizedBitmap;
        int newHeight = (int) (current.height * curScale);
        int newWidth = (int) (current.width * curScale);
        resizedBitmap = Bitmap.createScaledBitmap(current.bmp, newWidth, newHeight, false);
        img.setImageBitmap(resizedBitmap);
    }

    private View.OnClickListener blistener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                //Gets to chosen activity when clicking a button

                case R.id.gray:
                    if (current.filter != 1) {
                        current = current.toGray(memory.valMap);
                        memory.setNext(current);
                        img.setImageBitmap(current.bmp);
                    }
                    break;

                case R.id.sepia:
                    if (current.filter != 2) {
                        current = current.sepia(memory.valMap);
                        memory.validHistogram = 0;
                        memory.setNext(current);
                        img.setImageBitmap(current.bmp);
                    }
                    break;

                case R.id.moy:
                    AlertDialog.Builder moyDialog = new AlertDialog.Builder(GeneralMenu.this);
                    moyDialog.setTitle("Filtre moyenneur");
                    final EditText input = new EditText(GeneralMenu.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    moyDialog.setView(input);
                    moyDialog.setMessage("Entrez le paramètre du filtre moyenneur. Il doit être impair et au moins égal à 3. Un paramètre trop grand entraînera un ralentissement ou un échec.")
                            .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int param = Integer.valueOf(input.getText().toString());
                                    if ((param < 3)||(param%2==0)){
                                        Toast incorrectParameter = Toast.makeText(getApplicationContext(), "Le paramètre du filtre moyenneur doit être impair et au moins égal à 3.", Toast.LENGTH_LONG);
                                        incorrectParameter.show();
                                    }
                                    else if (param>50){
                                        Toast tooBigParameter = Toast.makeText(getApplicationContext(), "Le paramètre du filtre moyenneur est trop grand.", Toast.LENGTH_LONG);
                                        tooBigParameter.show();
                                    }
                                    else{
                                        current = current.moyenne(param);
                                        memory.setNext(current);
                                        memory.validHistogram = 0;
                                        img.setImageBitmap(current.bmp);
                                    }
                                }
                            });
                    AlertDialog alert = moyDialog.create();
                    alert.show();
                    break;

                case R.id.gauss:
                    current = current.gauss();
                    memory.validHistogram = 0;
                    memory.setNext(current);
                    img.setImageBitmap(current.bmp);
                    break;

                case R.id.sobel:
                    current = current.sobel();
                    memory.validHistogram = 0;
                    memory.setNext(current);
                    img.setImageBitmap(current.bmp);
                    break;

                case R.id.laplacien:
                    current = current.laplacien();
                    memory.validHistogram = 0;
                    memory.setNext(current);
                    img.setImageBitmap(current.bmp);
                    break;

                case R.id.color:
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
                    if (current.filter != 4) {
                        current = current.dynamicExtension(memory);
                        memory.validHistogram = 0;
                        memory.setNext(current);
                        img.setImageBitmap(current.bmp);
                    }
                    break;


                case R.id.teinte:
                    Intent eight = new Intent(GeneralMenu.this, ChoiceMenu.class);
                    startActivity(eight);
                    break;



                case R.id.HE:
                    if (current.filter != 3) {
                        current = current.histogramEqualization(memory);
                        memory.validHistogram = 0;
                        memory.setNext(current);
                        img.setImageBitmap(current.bmp);
                    }
                    break;


                case R.id.crop:
                    Intent twelve = new Intent(GeneralMenu.this, CropMenu.class);
                    startActivity(twelve);
                    break;

                case R.id.replace:
                    Intent thirteen = new Intent(GeneralMenu.this, ReplaceMenu.class);
                    startActivity(thirteen);
                    break;

                case R.id.finger:
                    Intent fifteen = new Intent(GeneralMenu.this, FingerMenu.class);
                    startActivity(fifteen);
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save:
                try {
                    MediaStore.Images.Media.insertImage(getContentResolver(), current.bmp, PhotoLoading.imgDecodableString + "_photoship", "");
                    Intent picturechoice = new Intent(GeneralMenu.this, PhotoLoading.class);
                    startActivity(picturechoice);
                    break;
                } catch (Exception e){
                    e.printStackTrace();
                }
                return true;
            case R.id.previous:
                if (memory.current > 0) {
                    current = memory.getPrevious();
                    img.setImageBitmap(current.bmp);
                }
                else{
                    Toast noprevious = Toast.makeText(getApplicationContext(), "Il n'y a pas de changement à annuler ou la mémoire a été effacée", Toast.LENGTH_LONG);
                    noprevious.show();
                }
                return true;
            case R.id.next:
                if (memory.current < memory.maxknown) {
                    current = memory.getNext();
                    img.setImageBitmap(current.bmp);
                }
                return true;

            case R.id.rotateleft:
                current = current.rotateLeft();
                memory.validHistogram = 0;
                memory.setNext(current);
                img.setImageBitmap(current.bmp);
                return true;

            case R.id.rotateright:
                current = current.rotateRight();
                memory.validHistogram = 0;
                memory.setNext(current);
                img.setImageBitmap(current.bmp);
                return true;

            case R.id.fliplr:
                current = current.fliplr();
                memory.validHistogram = 0;
                memory.setNext(current);
                img.setImageBitmap(current.bmp);
                return true;

            case R.id.flipud:
                current = current.flipud();
                memory.validHistogram = 0;
                memory.setNext(current);
                img.setImageBitmap(current.bmp);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    View.OnTouchListener MyOnTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // TODO Auto-generated method stub
            float distx, disty;
            switch(event.getAction() & MotionEvent.ACTION_MASK) {

                //if a finger touches the screen, the factor of zoom is displayed
                case MotionEvent.ACTION_DOWN:touchState = TOUCH;
                    break;

                //if the fingers pinch the bitmap, the distances change and the factor of zoom is displayed
                case MotionEvent.ACTION_POINTER_DOWN:touchState = PINCH;
                    distx = event.getX(0) - event.getX(1);
                    disty = event.getY(0) - event.getY(1);
                    dist0 = (float) Math.sqrt(distx * distx + disty * disty);

                    break;

                // if the fingers move, the distances change and the factor of zoom is displayed
                case MotionEvent.ACTION_MOVE:
                    if (touchState == PINCH) {
                        distx = event.getX(0) - event.getX(1);
                        disty = event.getY(0) - event.getY(1);
                        distCurrent =  (float) Math.sqrt(distx * distx + disty * disty);
                        drawMatrix();
                    }
                    break;

                //if the fingers stop zooming the bitmap, the factor of zoom is displayed
                case MotionEvent.ACTION_UP:touchState = IDLE;
                    break;

                //if one finger touches the screen, the factor of zoom is displayed
                case MotionEvent.ACTION_POINTER_UP:touchState = TOUCH;
                    break;
            }
            return true;
        }

    };
}

