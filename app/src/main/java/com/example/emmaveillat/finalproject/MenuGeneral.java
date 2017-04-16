package com.example.emmaveillat.finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

/**TODO
 * La classe MenuGeneral est le principal menu de l'application, à partir duquel l'utilisateur peut
 * accéder à toutes les transformations possibles pour ses images.
 */
public class MenuGeneral extends AppCompatActivity {

    /**
     * imageview de l'image à modifier
     */
    ImageView img;

    /**
     * Liste d'images servant d'historique lors de l'ouverture de l'application
     */
    BitmapListe memoire;

    /**
     * bitmaps utilisées lors des transformations
     */
    MaBitmap courant, bmpTest;

    /**
     * bitmap spécifique à l'application des filtres au doigt
     */
    MaBitmap appliquerDoigt;

    /**
     * TODO
     */
    Bitmap imgGalerie;

    /**
     *
     */
    ImageButton original, gris, sepia, invers, moy, sobel, laplacien, filtre, ED, teinte, HE,
            crayon1, crayon2, crayon3, cartoon, couleur;

    /**
     *
     */
    ImageButton haut, gauche, droite, bas;

    /**
     *
     */
    int[] aRogner;

    /**
     *
     */
    int rogneDirection;

    /**
     *
     */
    Button test, ok, annul, rogne;

    /**
     *
     */
    HorizontalScrollView barImages;

    /**
     *
     */
    RelativeLayout seekbarsInterface, cropInterface;

    /**
     *
     */
    ImageView colorviewleft, colorviewmid, colorviewright;

    /**
     *
     */
    SeekBar seekbar1, seekbar2, seekbar3, cropbar;

    /**
     *
     */
    TextView textsb1, textsb2, textsb3, valsb1, valsb2, valsb3, textCrop;

    /**
     *
     */
    int filterToUse, seekbarsDisplayed, huebar, satbar, valbar, gapbar, val1, val2, val3;

    /**
     *
     */
    float mx, my, curX, curY;

    /**
     *
     */
    int startX, startY, endX, endY;

    /**
     *
     */
    int touchState;

    /**
     *
     */
    final int IDLE = 0;
    final int TOUCH = 1;
    final int PINCH = 2;

    /**
     *
     */
    float dist0, distCurrent, factor;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //TODO what ?
        inflater.inflate(R.menu.general_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_general);

        imgGalerie = ChargementPhoto.scaleImage();
        //copies the original bitmap to be mutable

        courant = new MaBitmap(imgGalerie.copy(Bitmap.Config.ARGB_8888, true), 0);
        //Objects displayed in the menu
        img = (ImageView) findViewById(R.id.picture);
        img.setImageBitmap(courant.bmp);
        img.setOnTouchListener(PinchZoomListener);
        final Switch toggle = (Switch) findViewById(R.id.zoomswitch);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (memoire.courant == 0){
                        Toast noFilter = Toast.makeText(getApplicationContext(), "Veuillez choisir un filtre.", Toast.LENGTH_SHORT);
                        noFilter.show();
                        toggle.setChecked(false);
                    }
                    else {
                        appliquerDoigt = courant.copie();
                        courant = memoire.getPrecedent();
                        img.setImageBitmap(courant.bmp);
                        img.setOnTouchListener(ApplyWithFingerListener);
                    }

                } else {
                    img.setOnTouchListener(PinchZoomListener); {
                }
            }
        }});
        toggle.setChecked(false);

        barImages = (HorizontalScrollView) findViewById(R.id.filterscrollview);
        seekbarsInterface = (RelativeLayout) findViewById(R.id.seekbars_interface);
        cropInterface = (RelativeLayout) findViewById(R.id.crop_interface);

        colorviewleft = (ImageView) findViewById(R.id.color_left);
        colorviewleft.setVisibility(View.INVISIBLE);
        colorviewmid = (ImageView) findViewById(R.id.color_mid);
        colorviewmid.setVisibility(View.INVISIBLE);
        colorviewright = (ImageView) findViewById(R.id.color_right);
        colorviewright.setVisibility(View.INVISIBLE);

        memoire = new BitmapListe(courant);


        //initialization of the distances
        distCurrent = 1;
        dist0 = 1;

        touchState = IDLE;

        //Sets buttons
        original = (ImageButton) findViewById(R.id.original);
        original.setOnClickListener(blistener);

        gris = (ImageButton) findViewById(R.id.gray);
        gris.setOnClickListener(blistener);

        sepia = (ImageButton) findViewById(R.id.sepia);
        sepia.setOnClickListener(blistener);

        invers = (ImageButton) findViewById(R.id.invert);
        invers.setOnClickListener(blistener);

        teinte = (ImageButton) findViewById(R.id.teinte);
        teinte.setOnClickListener(blistener);

        couleur = (ImageButton) findViewById(R.id.color);
        couleur.setOnClickListener(blistener);

        filtre = (ImageButton) findViewById(R.id.filtre);
        filtre.setOnClickListener(blistener);

        ED = (ImageButton) findViewById(R.id.ED);
        ED.setOnClickListener(blistener);

        HE = (ImageButton) findViewById(R.id.HE);
        HE.setOnClickListener(blistener);

        moy = (ImageButton) findViewById(R.id.moy);
        moy.setOnClickListener(blistener);

        sobel = (ImageButton) findViewById(R.id.sobel);
        sobel.setOnClickListener(blistener);

        laplacien = (ImageButton) findViewById(R.id.laplacien);
        laplacien.setOnClickListener(blistener);

        crayon1 = (ImageButton) findViewById(R.id.pencil1);
        crayon1.setOnClickListener(blistener);

        crayon2 = (ImageButton) findViewById(R.id.pencil2);
        crayon2.setOnClickListener(blistener);

        crayon3 = (ImageButton) findViewById(R.id.pencil3);
        crayon3.setOnClickListener(blistener);

        cartoon = (ImageButton) findViewById(R.id.cartoon);
        cartoon.setOnClickListener(blistener);

        test = (Button) findViewById(R.id.test);
        test.setOnClickListener(blistener);

        ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(blistener);

        annul = (Button) findViewById(R.id.cancel);
        annul.setOnClickListener(blistener);

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

        rogne = (Button) findViewById(R.id.crop);
        rogne.setOnClickListener(blistener);

        cropbar = (SeekBar) findViewById(R.id.seekbarcrop);
        cropbar.setOnSeekBarChangeListener(seekBarChangeListenerCrop);

        haut = (ImageButton) findViewById(R.id.top);
        haut.setOnClickListener(blistener);

        gauche = (ImageButton) findViewById(R.id.left);
        gauche.setOnClickListener(blistener);

        droite = (ImageButton) findViewById(R.id.right);
        droite.setOnClickListener(blistener);

        bas = (ImageButton) findViewById(R.id.bot);
        bas.setOnClickListener(blistener);

        textCrop = (TextView) findViewById(R.id.textcrop);

        aRogner = new int[4];

        cropInterface.setActivated(false);
        cropInterface.setVisibility(View.INVISIBLE);
    }

    private void setSeekbars(int n){
        seekbarsInterface.setVisibility(View.VISIBLE);
        seekbarsInterface.setActivated(true);

        if (n<3) {
            seekbar3.setVisibility(View.INVISIBLE);
            seekbar3.setActivated(false);
            valsb3.setVisibility(View.INVISIBLE);
            textsb3.setVisibility(View.INVISIBLE);
            if (n<2){
                seekbar2.setVisibility(View.INVISIBLE);
                seekbar2.setActivated(false);
                textsb2.setVisibility(View.INVISIBLE);
                valsb2.setVisibility(View.INVISIBLE);
            }
        }
        else{
            seekbar3.setVisibility(View.VISIBLE);
            seekbar3.setActivated(true);
            valsb3.setVisibility(View.VISIBLE);
            textsb3.setVisibility(View.VISIBLE);
        }
        if (n==2){
            seekbar2.setVisibility(View.VISIBLE);
            seekbar2.setActivated(true);
            textsb2.setVisibility(View.VISIBLE);
            valsb2.setVisibility(View.VISIBLE);
        }

        barImages.setVisibility(View.INVISIBLE);
        barImages.setActivated(false);

        seekbarsDisplayed = n;
        val1 = val2 = val3 = 0;
    }

    private void delSeekbars(){
        seekbarsInterface.setActivated(false);
        seekbarsInterface.setVisibility(View.INVISIBLE);
        barImages.setVisibility(View.VISIBLE);
        barImages.setActivated(true);
        seekbarsDisplayed = 0;
        huebar = 0;
        gapbar = 0;
        colorviewleft.setVisibility(View.INVISIBLE);
        colorviewmid.setVisibility(View.INVISIBLE);
        colorviewright.setVisibility(View.INVISIBLE);
    }

    private View.OnClickListener blistener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                //Gets to chosen activity when clicking a button

                case R.id.original:
                    if (courant.filtre != 0) {
                        imgGalerie = ChargementPhoto.scaleImage();
                        courant = new MaBitmap(imgGalerie.copy(Bitmap.Config.ARGB_8888, true), 0);
                        memoire.setSuivant(courant);
                        img.setImageBitmap(courant.bmp);
                    }
                    break;

                case R.id.gray:
                    if (courant.filtre != 1) {
                        courant = courant.enGris();
                        memoire.setSuivant(courant);
                        img.setImageBitmap(courant.bmp);
                    }
                    break;

                case R.id.sepia:
                    if (courant.filtre != 2) {
                        courant = courant.sepia();
                        memoire.setSuivant(courant);
                        img.setImageBitmap(courant.bmp);
                    }
                    break;

                case R.id.invert:
                    courant = courant.inverser();
                    memoire.setSuivant(courant);
                    img.setImageBitmap(courant.bmp);
                    break;

                case R.id.moy:
                    AlertDialog.Builder moyDialog = new AlertDialog.Builder(MenuGeneral.this);
                    moyDialog.setTitle("Flou");
                    final EditText input = new EditText(MenuGeneral.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    moyDialog.setView(input);
                    moyDialog.setMessage("Flou moyenneur ou flou gaussien? Entrez le paramètre du filtre moyenneur si vous le choisissez. Il doit être impair et au moins égal à 3. Un paramètre trop grand entraînera un ralentissement ou un échec.")
                            .setNeutralButton("Annuler", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("Moyenneur", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int param = Integer.valueOf(input.getText().toString());
                                    if ((param < 3) || (param % 2 == 0)) {
                                        Toast incorrectParameter = Toast.makeText(getApplicationContext(), "Le paramètre du filtre moyenneur doit être impair et au moins égal à 3.", Toast.LENGTH_SHORT);
                                        incorrectParameter.show();
                                    } else if (param > 50) {
                                        Toast tooBigParameter = Toast.makeText(getApplicationContext(), "Le paramètre du filtre moyenneur est trop grand.", Toast.LENGTH_SHORT);
                                        tooBigParameter.show();
                                    } else {
                                        courant = courant.moyenneur(param);
                                        memoire.setSuivant(courant);
                                        img.setImageBitmap(courant.bmp);
                                    }
                                }
                            })
                            .setPositiveButton("Gauss", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    courant = courant.gauss();
                                    memoire.setSuivant(courant);
                                    img.setImageBitmap(courant.bmp);
                                }
                            });
                    AlertDialog alert = moyDialog.create();
                    alert.show();
                    break;

                case R.id.sobel:
                    courant = courant.sobel();
                    memoire.setSuivant(courant);
                    img.setImageBitmap(courant.bmp);
                    break;

                case R.id.laplacien:
                    courant = courant.laplacien();
                    memoire.setSuivant(courant);
                    img.setImageBitmap(courant.bmp);
                    break;

                case R.id.filtre:
                    setSeekbars(1);
                    seekbar1.setProgress(0);
                    seekbar1.setMax(360);
                    seekbar1.setOnSeekBarChangeListener(huebarlistener);
                    textsb1.setText("Teinte");
                    colorviewmid.setVisibility(View.VISIBLE);
                    editHueColor();
                    huebar = 1;
                    filterToUse = 2;
                    break;

                case R.id.color:
                    setSeekbars(3);
                    seekbar1.setProgress(0);
                    seekbar1.setMax(360);
                    seekbar1.setOnSeekBarChangeListener(huebarlistener);
                    textsb1.setText("Teinte");
                    seekbar2.setProgress(0);
                    seekbar2.setMax(360);
                    seekbar2.setOnSeekBarChangeListener(seekBarChangeListener);
                    textsb2.setText("Saturation");
                    seekbar3.setProgress(0);
                    seekbar3.setMax(360);
                    seekbar3.setOnSeekBarChangeListener(seekBarChangeListener);
                    textsb3.setText("Valeur");
                    huebar = 1;
                    valbar = 2;
                    satbar = 3;
                    colorviewmid.setVisibility(View.VISIBLE);
                    editHueColor();
                    filterToUse = 4;
                    break;

                case R.id.ED:
                    if (courant.filtre != 4) {
                        courant = courant.extensionDynamiques();
                        memoire.setSuivant(courant);
                        img.setImageBitmap(courant.bmp);
                    }
                    break;


                case R.id.teinte:
                    setSeekbars(2);
                    seekbar1.setProgress(0);
                    seekbar1.setMax(180);
                    textsb1.setText("Tolérance");
                    seekbar1.setOnSeekBarChangeListener(gapbarlistener);

                    seekbar2.setProgress(0);
                    seekbar2.setMax(360);
                    textsb2.setText("Teinte");
                    seekbar2.setOnSeekBarChangeListener(huebarlistener);
                    huebar = 2;
                    gapbar = 1;
                    colorviewleft.setVisibility(View.VISIBLE);
                    colorviewmid.setVisibility(View.VISIBLE);
                    colorviewright.setVisibility(View.VISIBLE);
                    editGapColor();
                    editHueColor();

                    filterToUse = 1;
                    break;

                case R.id.test:
                    bmpTest = courant.applicationFiltre(filterToUse, val1, val2, val3);
                    img.setImageBitmap(bmpTest.bmp);
                    break;

                case R.id.ok:
                    courant = courant.applicationFiltre(filterToUse, val1, val2, val3);
                    memoire.setSuivant(courant);
                    img.setImageBitmap(courant.bmp);
                    delSeekbars();
                    break;

                case R.id.cancel:
                    img.setImageBitmap(courant.bmp);
                    delSeekbars();
                    break;

                case R.id.HE:
                    if (courant.filtre != 3) {
                        courant = courant.egalisationHistogramme();
                        memoire.setSuivant(courant);
                        img.setImageBitmap(courant.bmp);
                    }
                    break;

                case R.id.pencil1:
                    if (courant.filtre != 17) {
                        courant = courant.dessinCrayon();
                        memoire.setSuivant(courant);
                        img.setImageBitmap(courant.bmp);
                    }
                    break;

                case R.id.pencil2:
                    courant = courant.laplacien();
                    courant = courant.enGris();
                    courant = courant.inverser();
                    memoire.setSuivant(courant);
                    img.setImageBitmap(courant.bmp);
                    break;

                case R.id.pencil3:
                    courant = courant.sobel();
                    courant = courant.enGris();
                    courant = courant.inverser();
                    memoire.setSuivant(courant);
                    img.setImageBitmap(courant.bmp);
                    break;

                case R.id.cartoon:
                    try {
                        courant = courant.cartoon();
                        memoire.setSuivant(courant);
                        img.setImageBitmap(courant.bmp);
                        setSeekbars(1);
                        seekbar1.setProgress(0);
                        seekbar1.setMax(100);
                        textsb1.setText(null);
                        filterToUse = 3;
                        seekbar1.setOnSeekBarChangeListener(cartoonbarlistener);
                    } catch (StackOverflowError e) {
                        AlertDialog.Builder reducedialog = new AlertDialog.Builder(MenuGeneral.this);
                        reducedialog.setTitle("Échec")
                                .setMessage("Il se peut que la qualité de l'image soit trop élevée. Réduisez la qualité via l'outil DÉFORMER")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog reducealert = reducedialog.create();
                        reducealert.show();
                    }
                    break;


                case R.id.top:
                    rogneDirection = 0;
                    cropbar.setProgress(aRogner[0]);
                    break;

                case R.id.bot:
                    rogneDirection = 1;
                    cropbar.setProgress(aRogner[1]);
                    break;

                case R.id.left:
                    rogneDirection = 2;
                    cropbar.setProgress(aRogner[2]);
                    break;

                case R.id.right:
                    rogneDirection = 3;
                    cropbar.setProgress(aRogner[3]);
                    break;

                case R.id.crop:
                    courant = courant.rognage(aRogner);
                    memoire.setSuivant(courant);
                    img.setImageBitmap(courant.bmp);
                    cropInterface.setVisibility(View.INVISIBLE);
                    cropInterface.setActivated(false);
                    barImages.setActivated(true);
                    barImages.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        }
    };

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {editText();}

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {editText();}
    };

    SeekBar.OnSeekBarChangeListener seekBarChangeListenerCrop = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {updateCrop();}

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {updateCrop();}
    };

    SeekBar.OnSeekBarChangeListener huebarlistener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            editText();
            editHueColor();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            editText();
            editHueColor();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            editText();
            editHueColor();
        }
    };

    SeekBar.OnSeekBarChangeListener gapbarlistener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            editText();
            editGapColor();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            editText();
            editGapColor();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            editText();
            editGapColor();
        }
    };

    SeekBar.OnSeekBarChangeListener cartoonbarlistener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            editText();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            editText();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            editText();
        }
    };

    private void editText(){
        val1 = seekbar1.getProgress();
        valsb1.setText("   " + String.valueOf(val1));
        if (seekbarsDisplayed>1){
            val2 = seekbar2.getProgress();
            valsb2.setText("   " + String.valueOf(val2));
            if (seekbarsDisplayed>2){
                val3 = seekbar3.getProgress();
                valsb3.setText("   " + String.valueOf(val3));
            }
        }
    }

    private void editHueColor(){
        if (gapbar != 0){
            editGapColor();
        }
        float[] hsv = new float[3];
        if (huebar == 1){
            hsv[0] = (float) seekbar1.getProgress();
        }
        else{
            hsv[0] = (float) seekbar2.getProgress();
        }
        hsv[1] = hsv[2] = 0.5F;
        colorviewmid.setImageDrawable(new ColorDrawable(Color.HSVToColor(hsv)));
    }

    private void editGapColor(){
        float[] hsvleft = new float[3];
        float[] hsvright = new float[3];
        float hue, gap;
        if (huebar == 1){
            hue = (float) seekbar1.getProgress();
            gap = (float) seekbar2.getProgress();
        }
        else{
            hue = (float) seekbar2.getProgress();
            gap = (float) seekbar1.getProgress();
        }
        hsvleft[1] = hsvright[1] = hsvleft[2] = hsvright[2] = 0.5F;
        if (hue - gap < 0F){
            hsvleft[0] = hue - gap + 360F;
        }
        else{
            hsvleft[0] = hue - gap;
        }
        if (hue + gap >= 360F){
            hsvright[0] = hue + gap - 360F;
        }
        else{
            hsvright[0] = hue + gap;
        }
        colorviewleft.setImageDrawable(new ColorDrawable(Color.HSVToColor(hsvleft)));
        colorviewright.setImageDrawable(new ColorDrawable(Color.HSVToColor(hsvright)));

    }

    private void updateCrop(){
        aRogner[rogneDirection] = cropbar.getProgress();
        switch (rogneDirection){
            case 0:
                if (aRogner[0] > 100 - aRogner[1]){
                    aRogner[0] = 100 - aRogner[1];
                }
                textCrop.setText("Rogner " + String.valueOf(aRogner[0]) + "% en haut");
                break;
            case 1:
                if (aRogner[1] > 100 - aRogner[0]){
                    aRogner[1] = 100 - aRogner[0];
                }
                textCrop.setText("Rogner " + String.valueOf(aRogner[1]) + "% en bas");
                break;
            case 2:
                if (aRogner[2] > 100 - aRogner[3]){
                    aRogner[2] = 100 - aRogner[3];
                }
                textCrop.setText("Rogner " + String.valueOf(aRogner[2]) + "% à gauche");
                break;
            case 3:
                if (aRogner[3] > 100 - aRogner[2]){
                    aRogner[3] = 100 - aRogner[2];
                }
                textCrop.setText("Rogner " + String.valueOf(aRogner[3]) + "% à droite");
                break;
        }
        img.setImageBitmap(bmpTest.affichageRogne(aRogner).bmp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save:
                try {
                    MediaStore.Images.Media.insertImage(getContentResolver(), courant.bmp, ChargementPhoto.cheminImg + "_photoship", "");
                    Intent picturechoice = new Intent(MenuGeneral.this, ChargementPhoto.class);
                    startActivity(picturechoice);
                    break;
                } catch (Exception e){
                    e.printStackTrace();
                }
                return true;
            case R.id.previous:
                if (memoire.courant > 0) {
                    courant = memoire.getPrecedent();
                    img.setImageBitmap(courant.bmp);
                }
                else{
                    Toast noprevious = Toast.makeText(getApplicationContext(), "Il n'y a pas de changement à annuler ou la mémoire a été effacée", Toast.LENGTH_SHORT);
                    noprevious.show();
                }
                return true;
            case R.id.next:
                if (memoire.courant < memoire.maxNbImgConnu) {
                    courant = memoire.getSuivant();
                    img.setImageBitmap(courant.bmp);
                }
                return true;

            case R.id.rotateleft:
                courant = courant.rotationGauche();
                memoire.setSuivant(courant);
                img.setImageBitmap(courant.bmp);
                return true;

            case R.id.rotateright:
                courant = courant.rotationDroit();
                memoire.setSuivant(courant);
                img.setImageBitmap(courant.bmp);
                return true;

            case R.id.fliplr:
                courant = courant.miroirHorizontal();
                memoire.setSuivant(courant);
                img.setImageBitmap(courant.bmp);
                return true;

            case R.id.flipud:
                courant = courant.miroirVertical();
                memoire.setSuivant(courant);
                img.setImageBitmap(courant.bmp);
                return true;

            case R.id.cropmenu:
                if (seekbarsInterface.isActivated()){
                    img.setImageBitmap(courant.bmp);
                    delSeekbars();
                }
                barImages.setVisibility(View.INVISIBLE);
                barImages.setActivated(false);
                cropInterface.setActivated(true);
                cropInterface.setVisibility(View.VISIBLE);
                cropbar.setProgress(0);
                textCrop.setText("");
                bmpTest = courant.copie();
                Arrays.fill(aRogner, 0);
                rogneDirection = 0;
                break;

            case R.id.ppv:
                AlertDialog.Builder deformDialog = new AlertDialog.Builder(MenuGeneral.this);
                deformDialog.setTitle("Changer les dimensions");

                // A SUPPRIMER
                final EditText input = new EditText(MenuGeneral.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);

                LinearLayout layout = new LinearLayout(MenuGeneral.this);
                layout.setOrientation(LinearLayout.VERTICAL);

                TextView widthText = new TextView(MenuGeneral.this);
                widthText.setText("Largeur");
                layout.addView(widthText);

                final EditText widthBox = new EditText(MenuGeneral.this);
                widthBox.setHint("Largeur");
                widthBox.setText(String.valueOf(courant.largeur));
                layout.addView(widthBox);

                TextView heightText = new TextView(MenuGeneral.this);
                heightText.setText("Hauteur");
                layout.addView(heightText);

                final EditText heightBox = new EditText(MenuGeneral.this);
                heightBox.setHint("Hauteur");
                heightBox.setText(String.valueOf(courant.hauteur));
                layout.addView(heightBox);

                Button dimensions = new Button(MenuGeneral.this);
                dimensions.setText("Garder rapport");
                dimensions.setId(R.id.dimButton);
                dimensions.setTextAppearance(MenuGeneral.this, android.R.style.TextAppearance_Small);
                dimensions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case (R.id.dimButton):
                                float factor;
                                int newWidth = (Integer.valueOf(widthBox.getText().toString()));
                                int newHeight = (Integer.valueOf(heightBox.getText().toString()));
                                if (newWidth != courant.largeur) {
                                    newHeight = courant.hauteur * newWidth / courant.largeur;
                                    heightBox.setText(String.valueOf(newHeight));
                                } else if (newHeight != courant.largeur) {
                                    newWidth = courant.largeur * newHeight / courant.hauteur;
                                    widthBox.setText(String.valueOf(newWidth));
                                }
                        }
                    }
                });
                layout.addView(dimensions);

                deformDialog.setView(layout)
                        .setNeutralButton("Annuler", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setPositiveButton("Appliquer", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int newWidth = (Integer.valueOf(widthBox.getText().toString()));
                                int newHeight = (Integer.valueOf(heightBox.getText().toString()));
                                courant = courant.voisinLePlusProche(newWidth, newHeight);
                                memoire.setSuivant(courant);
                                img.setImageBitmap(courant.bmp);
                            }
                        });
                AlertDialog alert = deformDialog.create();
                alert.show();

            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    View.OnTouchListener PinchZoomListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            float distx, disty;
            switch(event.getAction() & MotionEvent.ACTION_MASK) {

                //if the fingers pinch the bitmap, the distances change and the factor of zoom is displayed
                case MotionEvent.ACTION_POINTER_DOWN:
                    touchState = PINCH;
                    distx = event.getX(0) - event.getX(1);
                    disty = event.getY(0) - event.getY(1);
                    dist0 = (float) Math.sqrt(distx * distx + disty * disty);
                    break;

                case MotionEvent.ACTION_DOWN:
                    touchState = TOUCH;
                    mx = event.getX();
                    my = event.getY();
                    break;

                // if the fingers move, the distances change and the factor of zoom is displayed
                case MotionEvent.ACTION_MOVE:
                    if (touchState == PINCH) {
                        if (event.getPointerCount() >= 2) {
                            distx = event.getX(0) - event.getX(1);
                            disty = event.getY(0) - event.getY(1);
                            distCurrent = (float) Math.sqrt(distx * distx + disty * disty);
                            factor = distCurrent / dist0;
                            img.setImageBitmap(courant.scale(factor));
                        }
                    } else if (touchState == TOUCH) {
                        touchState = TOUCH;
                        curX = event.getX();
                        curY = event.getY();
                        img.scrollBy((int) (mx - curX), (int) (my - curY));
                        mx = curX;
                        my = curY;
                    }
                    break;

                //if the fingers stop zooming the bitmap, the factor of zoom is displayed
                case MotionEvent.ACTION_UP:
                    if (touchState == TOUCH) {
                        curX = event.getX();
                        curY = event.getY();
                        img.scrollBy((int) (mx - curX), (int) (my - curY));
                    }
                    touchState = IDLE;
                    break;

                //if one finger touches the screen, the factor of zoom is displayed
                case MotionEvent.ACTION_POINTER_UP:touchState = IDLE;
                    if (courant.filtre == 20) {
                        courant = new MaBitmap(courant.scale(factor), 20);
                    } else {
                        courant = new MaBitmap(courant.scale(factor), 20);
                        memoire.setSuivant(courant);
                    }
                    break;
            }
            return true;
        }

    };

    View.OnTouchListener ApplyWithFingerListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            int[] viewCoords = new int[2];
            img.getLocationOnScreen(viewCoords);
            int touchX, touchY;
            MaBitmap copy = courant.copie();//Copy if yourBMP is not mutable
            Canvas canvas = new Canvas(copy.bmp);
            MaBitmap transform = courant.copie();
            transform.enGris();
            int test = 0;

            switch(event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    touchX = (int) event.getX();
                    touchY = (int) event.getY();
                    startX = touchX - viewCoords[0];
                    startY = touchY - viewCoords[1];
                    Paint paint = new Paint();
                    paint.setAlpha(50); //Put a value between 0 and 255
                    paint.setColor(Color.RED); //Put your line couleur
                    paint.setStrokeWidth(5); //Choose the width of your line
                    //canvas.drawCircle((float) startX, startY, 10, paint);
                    canvas.drawLine (startX, startY, endX,  endY, paint);
                    for (int i = 0; i<copy.largeur*copy.hauteur; i++){
                        if (copy.pixels[i] == Color.RED){
                            courant.pixels[i] = transform.pixels[i];
                            test += 1;
                        }
                    }

                    //courant = courant.applicationDoigt(appliquerDoigt, startX, startY, endX, endY);
                    canvas.setBitmap(courant.bmp);

            break;

                case MotionEvent.ACTION_MOVE:
                    if (touchState == TOUCH) {
                        touchState = TOUCH;
                        touchX = (int) event.getX();
                        touchY = (int) event.getY();
                        endX = touchX - viewCoords[0];
                        endY = touchY - viewCoords[1];
                        //courant = courant.applicationDoigt(appliquerDoigt, startX, startY, endX, endY);
                        copy = courant.copie();//Copy if yourBMP is not mutable
                        canvas = new Canvas(copy.bmp);
                        paint = new Paint();
                        paint.setAlpha(50); //Put a value between 0 and 255
                        paint.setColor(Color.RED); //Put your line couleur
                        paint.setStrokeWidth(5); //Choose the width of your line
                        canvas.drawCircle((float) endX, endY, 10, paint);
                        for (int i = 0; i<copy.largeur*copy.hauteur; i++){
                            if (copy.pixels[i] == Color.RED){
                                courant.pixels[i] = transform.pixels[i];
                                test += 1;
                            }
                        }

                        //courant = courant.applicationDoigt(appliquerDoigt, startX, startY, endX, endY);
                        canvas.setBitmap(courant.bmp);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    touchX = (int) event.getX();
                    touchY = (int) event.getY();
                    endX = touchX - viewCoords[0];
                    endY = touchY - viewCoords[1];

                    copy = courant.copie();//Copy if yourBMP is not mutable
                    canvas = new Canvas(copy.bmp);
                    paint = new Paint();
                    paint.setAlpha(50); //Put a value between 0 and 255
                    paint.setColor(Color.RED); //Put your line couleur
                    paint.setStrokeWidth(5); //Choose the width of your line
                    canvas.drawCircle((float) endX, endY, 10, paint);
                    for (int i = 0; i<copy.largeur*copy.hauteur; i++){
                        if (copy.pixels[i] == Color.RED) {
                            courant.pixels[i] = transform.pixels[i];
                        }
                    }
                    img.setImageBitmap(transform.bmp);


                    break;

                case MotionEvent.ACTION_CANCEL:
                    break;

            }

            //courant = courant.applicationDoigt(appliquerDoigt, startX, startY, endX, endY);
           // memoire.setSuivant(courant);
            //canvas.setBitmap(courant.bmp);
            //img.setImageBitmap(tra.bmp);

            return true;


        }

    };
}

