package com.example.emmaveillat.finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Arrays;

/**
 * This class is a general menu where the user can choose the transformation he wants to apply to his bitmap.
 */
public class GeneralMenu extends AppCompatActivity {

    ImageView img;
    /**
     * Buttons to access the menus
     */

    BitmapList memory;
    MyBitmap current, bmpTest;
    Bitmap pictureFromGallery;
    ImageButton gris, sepia, moy, gauss, sobel, laplacien, filtre, ED, teinte, HE;
    ImageButton top, left, right, bot;
    int[] toCrop;
    int cropdirection;
    Button ppv, couleur, test, ok, cancel, crop;
    HorizontalScrollView filtersBar;
    RelativeLayout seekbarsInterface, cropInterface, generalMenu;

    SeekBar seekbar1, seekbar2, seekbar3, cropbar;
    TextView textsb1, textsb2, textsb3, valsb1, valsb2, valsb3, textCrop;

    int filterToUse, seekbarsDisplayed, seekbarToColor, val1, val2, val3;

    /**
     * statments of the fingers on the screen
     */
    float mx, my;
    int touchState;
    final int IDLE = 0;
    final int TOUCH = 1;
    final int PINCH = 2;

    /**
     * distances from the fingers
     */
    float dist0, distCurrent, factor;

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
        img.setOnTouchListener(ScrollListener);
        Switch toggle = (Switch) findViewById(R.id.zoomswitch);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    img.setOnTouchListener(PinchZoomListener);
                } else {
                    img.setOnTouchListener(ScrollListener); {
                }
            }
        }});

        filtersBar = (HorizontalScrollView) findViewById(R.id.filterscrollview);
        seekbarsInterface = (RelativeLayout) findViewById(R.id.seekbars_interface);
        cropInterface = (RelativeLayout) findViewById(R.id.crop_interface);

        memory = new BitmapList(current);


        //initialization of the distances
        distCurrent = 1;
        dist0 = 1;

        touchState = IDLE;

        //Sets buttons

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

        test = (Button) findViewById(R.id.test);
        test.setOnClickListener(blistener);

        ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(blistener);

        cancel = (Button) findViewById(R.id.cancel);
        cancel.setOnClickListener(blistener);

        textsb1 = (TextView) findViewById(R.id.textsb1);
        valsb1 = (TextView) findViewById(R.id.valsb1);
        textsb2 = (TextView) findViewById(R.id.textsb2);
        valsb2 = (TextView) findViewById(R.id.valsb2);
        textsb3 = (TextView) findViewById(R.id.textsb3);
        valsb3 = (TextView) findViewById(R.id.valsb3);

        seekbar1 = (SeekBar) findViewById(R.id.seekbar1);
        seekbar2 = (SeekBar) findViewById(R.id.seekbar2);
        seekbar3 = (SeekBar) findViewById(R.id.seekbar3);

        seekbar1.setOnSeekBarChangeListener(seekBarChangeListener);
        seekbar2.setOnSeekBarChangeListener(seekBarChangeListener);
        seekbar3.setOnSeekBarChangeListener(seekBarChangeListener);
        seekbarsInterface.setVisibility(View.INVISIBLE);
        seekbarsInterface.setActivated(false);

        crop = (Button) findViewById(R.id.crop);
        crop.setOnClickListener(blistener);

        cropbar = (SeekBar) findViewById(R.id.seekbarcrop);
        cropbar.setOnSeekBarChangeListener(seekBarChangeListenerCrop);

        top = (ImageButton) findViewById(R.id.top);
        top.setOnClickListener(blistener);

        left = (ImageButton) findViewById(R.id.left);
        left.setOnClickListener(blistener);

        right = (ImageButton) findViewById(R.id.right);
        right.setOnClickListener(blistener);

        bot = (ImageButton) findViewById(R.id.bot);
        bot.setOnClickListener(blistener);

        textCrop = (TextView) findViewById(R.id.textcrop);

        toCrop = new int[4];

        cropInterface.setActivated(false);
        cropInterface.setVisibility(View.INVISIBLE);
    }

    private void setSeekbars(int n){
        seekbarsInterface.setVisibility(View.VISIBLE);
        seekbarsInterface.setActivated(true);

        if (n<3) {
            seekbar3.setVisibility(View.INVISIBLE);
            seekbar3.setActivated(false);
            textsb3.setVisibility(View.INVISIBLE);
            textsb3.setActivated(false);
            if (n==1){
                seekbar2.setVisibility(View.INVISIBLE);
                seekbar2.setActivated(false);
                textsb2.setVisibility(View.INVISIBLE);
                textsb2.setActivated(false);
            }
        }

        filtersBar.setVisibility(View.INVISIBLE);
        filtersBar.setActivated(false);

        seekbarsDisplayed = n;
        val1 = val2 = val3 = 0;
    }

    private void delSeekbars(){
        seekbarsInterface.setActivated(false);
        seekbarsInterface.setVisibility(View.INVISIBLE);
        filtersBar.setVisibility(View.VISIBLE);
        filtersBar.setActivated(true);
        seekbarsDisplayed = 0;
        seekbarToColor = -1;
    }

    private View.OnClickListener blistener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                //Gets to chosen activity when clicking a button

                case R.id.gray:
                    if (current.filter != 1) {
                        current = current.toGray();
                        memory.setNext(current);
                        img.setImageBitmap(current.bmp);
                    }
                    break;

                case R.id.sepia:
                    if (current.filter != 2) {
                        current = current.sepia();
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
                                        memory.setNext(current);;
                                        img.setImageBitmap(current.bmp);
                                    }
                                }
                            });
                    AlertDialog alert = moyDialog.create();
                    alert.show();
                    break;

                case R.id.gauss:
                    current = current.gauss();
                    memory.setNext(current);
                    img.setImageBitmap(current.bmp);
                    break;

                case R.id.sobel:
                    current = current.sobel();
                    memory.setNext(current);
                    img.setImageBitmap(current.bmp);
                    break;

                case R.id.laplacien:
                    current = current.laplacien();
                    memory.setNext(current);
                    img.setImageBitmap(current.bmp);
                    break;

                case R.id.color:
                    Intent third = new Intent(GeneralMenu.this, ColorMenu.class);
                    startActivity(third);
                    break;

                case R.id.filtre:
                    setSeekbars(1);
                    seekbar1.setProgress(0);
                    seekbar1.setMax(360);
                    textsb1.setText("Teinte");
                    seekbarToColor = 1;
                    filterToUse = 2;
                    break;

                case R.id.ppv:
                    Intent fifth = new Intent(GeneralMenu.this, ZoomMenu.class);
                    startActivity(fifth);
                    break;

                case R.id.ED:
                    if (current.filter != 4) {
                        current = current.dynamicExtension();
                        memory.setNext(current);
                        img.setImageBitmap(current.bmp);
                    }
                    break;


                case R.id.teinte:
                    setSeekbars(2);
                    seekbar1.setProgress(0);
                    seekbar1.setMax(180);
                    textsb1.setText("Tolérance");

                    seekbar2.setProgress(0);
                    seekbar2.setMax(360);
                    textsb2.setText("Teinte");
                    seekbarToColor = 2;

                    filterToUse = 1;
                    break;

                case R.id.test:
                    bmpTest = current.applyFilter(filterToUse, val1, val2, val3);
                    img.setImageBitmap(bmpTest.bmp);
                    break;

                case R.id.ok:
                    current = current.applyFilter(filterToUse, val1, val2, val3);
                    memory.setNext(current);
                    img.setImageBitmap(current.bmp);
                    delSeekbars();
                    break;

                case R.id.cancel:
                    img.setImageBitmap(current.bmp);
                    delSeekbars();
                    break;

                case R.id.HE:
                    if (current.filter != 3) {
                        current = current.histogramEqualization();
                        memory.setNext(current);
                        img.setImageBitmap(current.bmp);
                    }
                    break;

                case R.id.top:
                    cropdirection = 0;
                    cropbar.setProgress(toCrop[0]);
                    break;

                case R.id.bot:
                    cropdirection = 1;
                    cropbar.setProgress(toCrop[1]);
                    break;

                case R.id.left:
                    cropdirection = 2;
                    cropbar.setProgress(toCrop[2]);
                    break;

                case R.id.right:
                    cropdirection = 3;
                    cropbar.setProgress(toCrop[3]);
                    break;

                case R.id.crop:
                    current = current.crop(toCrop);
                    memory.setNext(current);
                    img.setImageBitmap(current.bmp);
                    cropInterface.setVisibility(View.INVISIBLE);
                    cropInterface.setActivated(false);
                    filtersBar.setActivated(true);
                    filtersBar.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        }
    };

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {editTextandColor();}

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {editTextandColor();}
    };

    SeekBar.OnSeekBarChangeListener seekBarChangeListenerCrop = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {updateCrop();}

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {updateCrop();}
    };

    private void editTextandColor(){
        val1 = seekbar1.getProgress();
        valsb1.setText("   " + String.valueOf(val1));
        if (seekbarToColor == 1){
            //editColor(seekbar1, val1);
        }
        if (seekbarsDisplayed>1){
            val2 = seekbar2.getProgress();
            valsb2.setText("   " + String.valueOf(val2));
            if (seekbarToColor == 2){
                //editColor(seekbar2, val2);
            }
            if (seekbarsDisplayed>2){
                val3 = seekbar3.getProgress();
                valsb3.setText("   " + String.valueOf(val3));
                if (seekbarToColor == 3){
                    //editColor(seekbar3, val3);
                }
            }
        }
    }

    private void editColor(SeekBar bar, int hue){
        float[] hsv = {(float) hue, 0.5F, 0.5F};
    }

    private void updateCrop(){
        toCrop[cropdirection] = cropbar.getProgress();
        switch (cropdirection){
            case 0:
                if (toCrop[0] > 100 - toCrop[1]){
                    toCrop[0] = 100 - toCrop[1];
                }
                textCrop.setText("Rogner " + String.valueOf(toCrop[0]) + "% en haut");
                break;
            case 1:
                if (toCrop[1] > 100 - toCrop[0]){
                    toCrop[1] = 100 - toCrop[0];
                }
                textCrop.setText("Rogner " + String.valueOf(toCrop[1]) + "% en bas");
                break;
            case 2:
                if (toCrop[2] > 100 - toCrop[3]){
                    toCrop[2] = 100 - toCrop[3];
                }
                textCrop.setText("Rogner " + String.valueOf(toCrop[2]) + "% à gauche");
                break;
            case 3:
                if (toCrop[3] > 100 - toCrop[2]){
                    toCrop[3] = 100 - toCrop[2];
                }
                textCrop.setText("Rogner " + String.valueOf(toCrop[3]) + "% à droite");
                break;
        }
        img.setImageBitmap(bmpTest.visualCrop(toCrop).bmp);
    }

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
                memory.setNext(current);
                img.setImageBitmap(current.bmp);
                return true;

            case R.id.rotateright:
                current = current.rotateRight();
                memory.setNext(current);
                img.setImageBitmap(current.bmp);
                return true;

            case R.id.fliplr:
                current = current.fliplr();
                memory.setNext(current);
                img.setImageBitmap(current.bmp);
                return true;

            case R.id.flipud:
                current = current.flipud();
                memory.setNext(current);
                img.setImageBitmap(current.bmp);
                return true;

            case R.id.cropmenu:
                if (seekbarsInterface.isActivated()){
                    img.setImageBitmap(current.bmp);
                    delSeekbars();
                }
                filtersBar.setVisibility(View.INVISIBLE);
                filtersBar.setActivated(false);
                cropInterface.setActivated(true);
                cropInterface.setVisibility(View.VISIBLE);
                bmpTest = current.copy();
                Arrays.fill(toCrop, 0);
                cropdirection = 0;
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    View.OnTouchListener ScrollListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View arg0, MotionEvent event) {

            float curX, curY;

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    touchState = TOUCH;
                    mx = event.getX();
                    my = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchState = TOUCH;
                    curX = event.getX();
                    curY = event.getY();
                    img.scrollBy((int) (mx - curX), (int) (my - curY));
                    mx = curX;
                    my = curY;
                    break;
                case MotionEvent.ACTION_UP:
                    touchState = TOUCH;
                    curX = event.getX();
                    curY = event.getY();
                    img.scrollBy((int) (mx - curX), (int) (my - curY));
                    break;
            }
            return true;
        }
    };

    View.OnTouchListener PinchZoomListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // TODO Auto-generated method stub
            float distx, disty;
            switch(event.getAction() & MotionEvent.ACTION_MASK) {

                //if the fingers pinch the bitmap, the distances change and the factor of zoom is displayed
                case MotionEvent.ACTION_POINTER_DOWN:touchState = PINCH;
                    distx = event.getX(0) - event.getX(1);
                    disty = event.getY(0) - event.getY(1);
                    dist0 = (float) Math.sqrt(distx * distx + disty * disty);
                    break;

                // if the fingers move, the distances change and the factor of zoom is displayed
                case MotionEvent.ACTION_MOVE:
                    if(touchState == PINCH) {
                        if (event.getPointerCount() >= 2) {
                            distx = event.getX(0) - event.getX(1);
                            disty = event.getY(0) - event.getY(1);
                            distCurrent = (float) Math.sqrt(distx * distx + disty * disty);
                            factor = distCurrent / dist0;
                            img.setImageBitmap(current.scale(factor));
                        }
                    }
                    break;

                //if the fingers stop zooming the bitmap, the factor of zoom is displayed
                case MotionEvent.ACTION_UP:touchState = IDLE;
                    if (current.filter == 20) {
                        current = new MyBitmap(current.scale(factor), 20);
                    }
                    else{
                        current = new MyBitmap(current.scale(factor), 20);
                        memory.setNext(current);
                    }
                    break;

                //if one finger touches the screen, the factor of zoom is displayed
                case MotionEvent.ACTION_POINTER_UP:touchState = TOUCH;
                    break;
            }
            return true;
        }

    };
}

