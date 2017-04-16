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

//TODO
/**
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
     * image choisie par l'utilisateur et récupérée à partir du menu ChargementPhoto
     */
    Bitmap imgGalerie;

    /**
     * Boutons montrant les transformations disponibles (à l'aide d'images) et permettant d'accéder
     * aux transformations correspondantes
     */
    ImageButton original, gris, sepia, invers, moy, sobel, laplacien, filtre, ED, teinte, HE,
            crayon1, crayon2, crayon3, cartoon, couleur;

    /**
     * Boutons d'images pour aider l'utilisateur à rogner celle-ci
     */
    ImageButton haut, gauche, droite, bas;

    //TODO
    int[] aRogner;

    //TODO
    int rogneDirection;

    /**
     * Boutons pour tester, annuler ou valider les modifications liées aux seekbars ou au rognage
     */
    Button test, ok, annul, rogne;

    /**
     * Scrollview permettant de faire défiler horizontalement les boutons-images montrant les
     * transformations disponibles.
     */
    HorizontalScrollView barImages;

    /**
     * Seekbars et interface de rognage invisibles si l'utilisateur n'a pas choisi les
     * transformations correspondantes.
     */
    RelativeLayout seekbarsInterface, cropInterface;

    /**
     * Échantillons de couleurs visibles par l'utilisateur lors de n'importe quelle modification de
     * le teinte par une seekbar
     */
    ImageView colorviewleft, colorviewmid, colorviewright;

    /**
     * Seekbars disponibles
     */
    SeekBar seekbar1, seekbar2, seekbar3, cropbar;

    /**
     * taille rognée affichée ou valeurs de teinte, saturation, etc affichées
     */
    TextView textsb1, textsb2, textsb3, valsb1, valsb2, valsb3, textCrop;

    //TODO
    int filterToUse, seekbarsDisplayed, huebar, satbar, valbar, gapbar, val1, val2, val3;

    //TODO
    float mx, my, curX, curY;

    /**
     * positions de départ et d'arrivée du doigt lors de l'application des filtres avec celui-ci
     */
    int startX, startY, endX, endY;

    //TODO
    int touchState;

    //TODO
    final int IDLE = 0;
    final int TOUCH = 1;
    final int PINCH = 2;

    //TODO
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

        //Récupération de l'image choisie dans le menu ChargementPhoto précédemment par
        // l'utilisateur
        imgGalerie = ChargementPhoto.scaleImage();

        //création de l'image qui sera modifiée par l'utilisateur à partir de l'image chargée
        // précédemment
        courant = new MaBitmap(imgGalerie.copy(Bitmap.Config.ARGB_8888, true), 0);

        //Affichage de l'image courante dans l'imageview et préparation au zoom de celle-ci
        img = (ImageView) findViewById(R.id.picture);
        img.setImageBitmap(courant.bmp);
        img.setOnTouchListener(PinchZoomListener);

        //Implémentation d'un bouton à switcher pour choisir l'application du filtre automatique sur
        // toute l'image ou partiellement au doigt par l'utilisateur
        final Switch toggle = (Switch) findViewById(R.id.zoomswitch);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //si le bouton switch est enclenché, l'utilisateur ne peut plus zoomer
                if (isChecked) {
                    //si aucun filtre n'est choisi, un message est affiché et le bouton ne
                    // s'enclenche pas
                    if (memoire.courant == 0){
                        Toast noFilter = Toast.makeText(getApplicationContext(), "Veuillez choisir un filtre.", Toast.LENGTH_SHORT);
                        noFilter.show();
                        toggle.setChecked(false);
                    }
                    else {
                        //si le filtre a été choisi précédemment, le bouton renvoie vers la méthode
                        //dédiée à l'application du filtre choisi au doigt et affichage de cette
                        //image
                        appliquerDoigt = courant.copie();
                        courant = memoire.getPrecedent();
                        img.setImageBitmap(courant.bmp);
                        img.setOnTouchListener(ApplyWithFingerListener);
                    }
                }
                //si le bouton n'est plus enclenché, l'utilisateur a de nouveau la possibilité de
                //zoomer
                else {
                    img.setOnTouchListener(PinchZoomListener); {
                }
            }
        }});
        toggle.setChecked(false);

        //TODO
        barImages = (HorizontalScrollView) findViewById(R.id.filterscrollview);
        seekbarsInterface = (RelativeLayout) findViewById(R.id.seekbars_interface);
        cropInterface = (RelativeLayout) findViewById(R.id.crop_interface);

        //affichage des échantillons de couleurs en les rendant invisibles en temps normal
        colorviewleft = (ImageView) findViewById(R.id.color_left);
        colorviewleft.setVisibility(View.INVISIBLE);
        colorviewmid = (ImageView) findViewById(R.id.color_mid);
        colorviewmid.setVisibility(View.INVISIBLE);
        colorviewright = (ImageView) findViewById(R.id.color_right);
        colorviewright.setVisibility(View.INVISIBLE);

        //initialisation de l'historique d'images
        memoire = new BitmapListe(courant);

        //initialisation des distances
        distCurrent = 1;
        dist0 = 1;

        //TODO
        touchState = IDLE;

        //Affichage des boutons-images représentant les différents filtres et disponibles dans
        // l'application
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

        //TODO
        test = (Button) findViewById(R.id.test);
        test.setOnClickListener(blistener);
        ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(blistener);
        annul = (Button) findViewById(R.id.cancel);
        annul.setOnClickListener(blistener);

        //Affichage des titres et des valeurs des seekbars
        textsb1 = (TextView) findViewById(R.id.textsb1);
        valsb1 = (TextView) findViewById(R.id.valsb1);
        textsb2 = (TextView) findViewById(R.id.textsb2);
        valsb2 = (TextView) findViewById(R.id.valsb2);
        textsb3 = (TextView) findViewById(R.id.textsb3);
        valsb3 = (TextView) findViewById(R.id.valsb3);

        //Affichage des seekbars et implémentation dans le listener correspondant.
        //Les seekbars sont invisibles aux yeux de l'utilisateur tant qu'il n'en a pas besoin dans
        //les filtres qu'il applique.
        seekbar1 = (SeekBar) findViewById(R.id.seekbar1);
        seekbar2 = (SeekBar) findViewById(R.id.seekbar2);
        seekbar3 = (SeekBar) findViewById(R.id.seekbar3);
        seekbar1.setOnSeekBarChangeListener(seekBarChangeListener);
        seekbar2.setOnSeekBarChangeListener(seekBarChangeListener);
        seekbar3.setOnSeekBarChangeListener(seekBarChangeListener);
        seekbarsInterface.setVisibility(View.INVISIBLE);
        seekbarsInterface.setActivated(false);

        //Affichage de l'interface de rognage (d'abord invisible par l'utilisateur) avec les boutons
        //indiquants la direction de rognage
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

    /**
     * fonction qui permet l'affichage du nombre adéquat de seekbar quand le filtre à appliquer le
     * demande
     * @param n le nombre de seekbars à afficher
     */
    private void setSeekbars(int n){
        //rend l'interface contenant les seekbars visible
        seekbarsInterface.setVisibility(View.VISIBLE);
        seekbarsInterface.setActivated(true);

        //s'il faut afficher moins de 3 seekbars, on fait disparaître au moins la troisième
        if (n<3) {
            seekbar3.setVisibility(View.INVISIBLE);
            seekbar3.setActivated(false);
            valsb3.setVisibility(View.INVISIBLE);
            textsb3.setVisibility(View.INVISIBLE);
            //s'il faut afficher moins de 2 seekbars, on fait disparaître la deuxième
            if (n<2){
                seekbar2.setVisibility(View.INVISIBLE);
                seekbar2.setActivated(false);
                textsb2.setVisibility(View.INVISIBLE);
                valsb2.setVisibility(View.INVISIBLE);
            }
        }
        //sinon on affiche la troisième seekbar
        else{
            seekbar3.setVisibility(View.VISIBLE);
            seekbar3.setActivated(true);
            valsb3.setVisibility(View.VISIBLE);
            textsb3.setVisibility(View.VISIBLE);
        }
        //s'il faut 2 seekbars, on affiche la deuxième en plus
        if (n==2){
            seekbar2.setVisibility(View.VISIBLE);
            seekbar2.setActivated(true);
            textsb2.setVisibility(View.VISIBLE);
            valsb2.setVisibility(View.VISIBLE);
        }

        //Disparition de la bar montrant les transformations disponibles
        barImages.setVisibility(View.INVISIBLE);
        barImages.setActivated(false);

        seekbarsDisplayed = n;
        val1 = val2 = val3 = 0;
    }

    /**
     * fonction qui fait disparaître toutes les seekbars et les échantillons de couleur lorsque que
     * la fermeture des interfaces correspondantes est appelée
     */
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

    /**
     *listener indiquant le comportement des boutons-images lorsque l'utilisateur clique dessus
     */
    private View.OnClickListener blistener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {

                //quand l'utilisateur appuie sur le bouton "Original"
                case R.id.original:
                    if (courant.filtre != 0) {
                        //récupération de l'image originale et stockage de celle-ci dans
                        // l'historique des modifications
                        imgGalerie = ChargementPhoto.scaleImage();
                        courant = new MaBitmap(imgGalerie.copy(Bitmap.Config.ARGB_8888, true), 0);
                        memoire.setSuivant(courant);
                        //Affichage de l'image dans l'application
                        img.setImageBitmap(courant.bmp);
                    }
                    break;

                //quand l'utilisateur appuie sur le bouton "Noir et Blanc"
                case R.id.gray:
                    if (courant.filtre != 1) {
                        //transformation de l'image et stockage de l'image dans l'historique
                        courant = courant.enGris();
                        memoire.setSuivant(courant);
                        //Affichage de l'image dans l'application
                        img.setImageBitmap(courant.bmp);
                    }
                    break;

                //quand l'utilisateur appuie sur le bouton "Sepia"
                case R.id.sepia:
                    if (courant.filtre != 2) {
                        //transformation de l'image et stockage de l'image dans l'historique
                        courant = courant.sepia();
                        memoire.setSuivant(courant);
                        //Affichage de l'image dans l'application
                        img.setImageBitmap(courant.bmp);
                    }
                    break;

                //quand l'utilisateur appuie sur le bouton "Négatif"
                case R.id.invert:
                    //transformation de l'image et stockage dans l'historique
                    courant = courant.inverser();
                    memoire.setSuivant(courant);
                    //Affichage de l'image dans l'application
                    img.setImageBitmap(courant.bmp);
                    break;

                //quand l'utilisateur appuie sur le bouton "Flou"
                case R.id.moy:
                    //Génération d'une boîte de dialogue pour avertir l'utilisateur des conditions
                    //d'utilisation du filtre
                    AlertDialog.Builder moyDialog = new AlertDialog.Builder(MenuGeneral.this);
                    moyDialog.setTitle("Flou");
                    final EditText input = new EditText(MenuGeneral.this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    moyDialog.setView(input);
                    moyDialog.setMessage("Flou moyenneur ou flou gaussien? Entrez le paramètre " +
                            "du filtre moyenneur si vous le choisissez. Il doit être impair et " +
                            "au moins égal à 3. Un paramètre trop grand entraînera un " +
                            "ralentissement ou un échec.")
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
                                    //Si l'utilisateur ne fournit pas le bon paramètre, l'applicaion
                                    //lui envoie un message d'avertissement
                                    if ((param < 3) || (param % 2 == 0)) {
                                        Toast incorrectParameter = Toast.makeText(
                                                getApplicationContext(), "Le paramètre du filtre " +
                                                        "moyenneur doit être impair et au moins " +
                                                        "égal à 3.", Toast.LENGTH_SHORT);
                                        incorrectParameter.show();
                                    } else if (param > 50) {
                                        //pareil si le paramètre est trop grand
                                        Toast tooBigParameter = Toast.makeText(
                                                getApplicationContext(), "Le paramètre du filtre " +
                                                        "moyenneur est trop grand.", Toast.
                                                        LENGTH_SHORT);
                                        tooBigParameter.show();
                                    } else {
                                        //si le paramètre est ok, on applique le filtre avec le
                                        //paramètre fourni et on affiche l'image résultante après
                                        //l'avoir stockée dans l'historique
                                        courant = courant.moyenneur(param);
                                        memoire.setSuivant(courant);
                                        img.setImageBitmap(courant.bmp);
                                    }
                                }
                            })
                            .setPositiveButton("Gauss", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //pareil concernant le filtre gaussien
                                    courant = courant.gauss();
                                    memoire.setSuivant(courant);
                                    img.setImageBitmap(courant.bmp);
                                }
                            });
                    AlertDialog alert = moyDialog.create();
                    alert.show();
                    break;

                //quand l'utilisateur appuie sur le bouton "Sobel"
                case R.id.sobel:
                    //transformation de l'image et stockage dans l'historique
                    courant = courant.sobel();
                    memoire.setSuivant(courant);
                    //Affichage de l'image dans l'application
                    img.setImageBitmap(courant.bmp);
                    break;

                //quand l'utilisateur appuie sur le bouton "Laplacien"
                case R.id.laplacien:
                    //transformation de l'image et stockage dans l'histogramme
                    courant = courant.laplacien();
                    memoire.setSuivant(courant);
                    //Affichage de l'image dans l'application
                    img.setImageBitmap(courant.bmp);
                    break;

                //quand l'utilisateur appuie sur le bouton "Filtre couleur"
                case R.id.filtre:
                    //Affichage et initialisation de la seekbar nécessaire
                    setSeekbars(1);
                    seekbar1.setProgress(0);
                    seekbar1.setMax(360);
                    //implémentation des changements dans le listener dédié
                    seekbar1.setOnSeekBarChangeListener(huebarlistener);
                    textsb1.setText("Teinte");
                    colorviewmid.setVisibility(View.VISIBLE);
                    editHueColor();
                    huebar = 1;
                    filterToUse = 2;
                    break;

                //quand l'utilisateur appuie sur le bouton "Choix couleur"
                case R.id.color:
                    //Affichage et initialisation des 3 seekbars nécessaires
                    //Implémentation des changements dans le listener
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

                //quand l'utilisateur appuie sur le bouton "Extension dynamiques"
                case R.id.ED:
                    if (courant.filtre != 4) {
                        //transformation de l'image et stockage dans l'historique
                        courant = courant.extensionDynamiques();
                        memoire.setSuivant(courant);
                        //Affichage de l'image dans l'application
                        img.setImageBitmap(courant.bmp);
                    }
                    break;

                //TODO
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

                //quand l'utilisateur appuie sur le bouton "Test"
                case R.id.test:
                    //transformation de l'image en fonction du filtre et des valeurs fournies par
                    //les seekbars
                    bmpTest = courant.applicationFiltre(filterToUse, val1, val2, val3);
                    //Affichage de l'image dans l'application
                    img.setImageBitmap(bmpTest.bmp);
                    break;

                //quand l'utilisateur appuie sur le bouton "Valider"
                case R.id.ok:
                    //Application définitive de la transformation
                    courant = courant.applicationFiltre(filterToUse, val1, val2, val3);
                    //stockage dans l'historique
                    memoire.setSuivant(courant);
                    //Affichage de l'image dans l'application
                    img.setImageBitmap(courant.bmp);
                    //Disparition des seekbars affichées
                    delSeekbars();
                    break;

                //quand l'utilisateur appuie sur le bouton "Annuler"
                case R.id.cancel:
                    //Réaffichage de l'image précédente
                    img.setImageBitmap(courant.bmp);
                    //Disparition des seekbars affichées
                    delSeekbars();
                    break;

                //quand l'utilisateur appuie sur le bouton "Egalisation histogramme"
                case R.id.HE:
                    if (courant.filtre != 3) {
                        //transformation de l'image et stockage dans l'historique
                        courant = courant.egalisationHistogramme();
                        memoire.setSuivant(courant);
                        //Affichage de l'image dans l'application
                        img.setImageBitmap(courant.bmp);
                    }
                    break;

                //quand l'utilisateur appuie sur le bouton "Effet dessin 1"
                case R.id.pencil1:
                    if (courant.filtre != 17) {
                        //transformation de l'image et stockage dans l'historique
                        courant = courant.dessinCrayon();
                        memoire.setSuivant(courant);
                        //Affichage de l'image dans l'application
                        img.setImageBitmap(courant.bmp);
                    }
                    break;

                //quand l'utilisateur appuie sur le bouton "Effet dessin 2"
                case R.id.pencil2:
                    //transformation de l'image par différents filtres successifs et stockage dans
                    //l'historique
                    courant = courant.laplacien();
                    courant = courant.enGris();
                    courant = courant.inverser();
                    memoire.setSuivant(courant);
                    //Affichage de l'image dans l'application
                    img.setImageBitmap(courant.bmp);
                    break;

                //quand l'utilisateur appuie sur le bouton "Effet dessin 3"
                case R.id.pencil3:
                    //transformation de l'image par différents filtres successifs et stockage dans
                    //l'historique
                    courant = courant.sobel();
                    courant = courant.enGris();
                    courant = courant.inverser();
                    memoire.setSuivant(courant);
                    //Affichage de l'image dans l'application
                    img.setImageBitmap(courant.bmp);
                    break;

                //quand l'utilisateur appuie sur le bouton "Cartoon"
                case R.id.cartoon:
                    try {
                        //transformation de l'image, stockage dans l'historique et affichage de
                        // celle-ci
                        courant = courant.cartoon();
                        memoire.setSuivant(courant);
                        img.setImageBitmap(courant.bmp);
                        //Affichage d'une seekbar pour régler l'effet cartoon
                        setSeekbars(1);
                        seekbar1.setProgress(0);
                        seekbar1.setMax(100);
                        textsb1.setText(null);
                        filterToUse = 3;
                        seekbar1.setOnSeekBarChangeListener(cartoonbarlistener);
                    }
                    //Message d'erreur si la qualité de l'image est trop élevée pour appliquer le
                    //filtre
                    catch (StackOverflowError e) {
                        AlertDialog.Builder reducedialog = new AlertDialog.Builder(MenuGeneral.
                                this);
                        reducedialog.setTitle("Échec")
                                .setMessage("Il se peut que la qualité de l'image soit trop " +
                                        "élevée. " +
                                        "Réduisez la qualité via l'outil DÉFORMER")
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

                //quand l'utilisateur appuie sur le bouton correspondant au haut de l'image, alors
                //la fonction de rognage rogne l'image par le haut
                case R.id.top:
                    rogneDirection = 0;
                    cropbar.setProgress(aRogner[0]);
                    break;

                //quand l'utilisateur appuie sur le bouton correspondant au bas de l'image, alors
                //la fonction de rognage rogne l'image par le bas
                case R.id.bot:
                    rogneDirection = 1;
                    cropbar.setProgress(aRogner[1]);
                    break;

                //quand l'utilisateur appuie sur le bouton correspondant à la gauche de l'image, alors
                //la fonction de rognage rogne l'image par la gauche
                case R.id.left:
                    rogneDirection = 2;
                    cropbar.setProgress(aRogner[2]);
                    break;

                //quand l'utilisateur appuie sur le bouton correspondant à la droite de l'image, alors
                //la fonction de rognage rogne l'image par la droite
                case R.id.right:
                    rogneDirection = 3;
                    cropbar.setProgress(aRogner[3]);
                    break;

                //quand l'utilisateur appuie sur le bouton "Rogner", la transformation s'applique,
                // l'image est stockée dans l'historique puis est affichée dans l'application
                case R.id.crop:
                    courant = courant.rognage(aRogner);
                    memoire.setSuivant(courant);
                    img.setImageBitmap(courant.bmp);
                    //l'interface de rognage est désactivé et les boutons-images sont de nouveau
                    // visibles
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

    /**
     * listener définissant le comportement de toutes les seekbars, lors que l'utilisateur commence
     * à les bouger, les bouge ou finit de les bouger
     */
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {editText();}
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {editText();}
    };

    /**
     * listener définissant le comportement des seekbars pour le rognage, lors que l'utilisateur
     * commence à les bouger, les bouge ou finit de les bouger
     */
    SeekBar.OnSeekBarChangeListener seekBarChangeListenerCrop = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {updateCrop();}

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {updateCrop();}
    };

    /**
     * listener définissant le comportement des seekbars pour la modification de teinte, lors que
     * l'utilisateur commence à les bouger, les bouge ou finit de les bouger
     */
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

    /**
     * listener définissant le comportement des seekbars pour la modification d'intervalle, lors que
     * l'utilisateur commence à les bouger, les bouge ou finit de les bouger
     */
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

    /**
     * listener définissant le comportement des seekbars pour l'effet cartoon, lors que
     * l'utilisateur commence à les bouger, les bouge ou finit de les bouger
     */
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

    /**
     * fonction permettant d'afficher les valeurs des seekbars pendant leur manipulation par
     * l'utilisateur.
     * Cela dépend du nombre de seekbars nécessaires au filtre actuellement appliqué.
     */
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

    //TODO
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

    //TODO
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

    //TODO
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
        //comportement des icônes dans l'actionBar
        switch (item.getItemId()) {

            //quand l'utilisateur appuie sur le bouton de sauvegarde
            case R.id.save:
                try {
                    //sauvegarde de l'image finale dans la galerie et renvoie au menu de
                    //chargement d'images
                    MediaStore.Images.Media.insertImage(getContentResolver(), courant.bmp, ChargementPhoto.cheminImg + "_photoship", "");
                    Intent picturechoice = new Intent(MenuGeneral.this, ChargementPhoto.class);
                    startActivity(picturechoice);
                    break;
                } catch (Exception e){
                    e.printStackTrace();
                }
                return true;

            //quand l'utilisateur appuie sur le bouton de retour
            case R.id.previous:
                if (memoire.courant > 0) {
                    //l'image courante est remplacée par celle à la position précédente dans
                    //l'historique puis est affichée dans l'application
                    courant = memoire.getPrecedent();
                    img.setImageBitmap(courant.bmp);
                }
                else{
                    //s'il n'y a pas d'image précédent l'image courante, un message d'erreur est
                    //envoyé à l'utilisateur
                    Toast noprevious = Toast.makeText(getApplicationContext(), "Il n'y a pas de " +
                            "changement à annuler ou la mémoire a été effacée", Toast.LENGTH_SHORT);
                    noprevious.show();
                }
                return true;

            //quand l'utilisateur appuie sur le bouton suivant
            case R.id.next:
                if (memoire.courant < memoire.maxNbImgConnu) {
                    //si la position de l'image courante est bien inférieure au nombre d'images
                    // total connu par l'historique, l'application remplace l'image courante par
                    // celle à la position supérieure et l'affiche
                    courant = memoire.getSuivant();
                    img.setImageBitmap(courant.bmp);
                }
                return true;

            //quand l'utilisateur appuie sur le bouton "Rotation à gauche"
            case R.id.rotateleft:
                //transformation de l'image et stockage dans l'historique
                courant = courant.rotationGauche();
                memoire.setSuivant(courant);
                //affichage de l'image dans l'application
                img.setImageBitmap(courant.bmp);
                return true;

            //quand l'utilisateur appuie sur le bouton "Rotation à droite"
            case R.id.rotateright:
                //transformation de l'image et stockage dans l'historique
                courant = courant.rotationDroit();
                memoire.setSuivant(courant);
                //affichage de l'image dans l'application
                img.setImageBitmap(courant.bmp);
                return true;

            //quand l'utilisateur appuie sur le bouton "Miroir horizontal"
            case R.id.fliplr:
                //transformation de l'image et stockage dans l'historique
                courant = courant.miroirHorizontal();
                memoire.setSuivant(courant);
                //affichage de l'image dans l'application
                img.setImageBitmap(courant.bmp);
                return true;

            //quand l'utilisateur appuie sur le bouton "Miroir vertical"
            case R.id.flipud:
                //transformation de l'image et stockage dans l'historique
                courant = courant.miroirVertical();
                memoire.setSuivant(courant);
                //affichage de l'image dans l'application
                img.setImageBitmap(courant.bmp);
                return true;

            //quand l'utilisateur appuie sur le bouton "Rogner"
            case R.id.cropmenu:
                //activation de l'interface nécessaire au rognage de l'image
                if (seekbarsInterface.isActivated()){
                    img.setImageBitmap(courant.bmp);
                    delSeekbars();
                }
                //TODO
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

            //TODO
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

                //TODO
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

    //TODO
    View.OnTouchListener PinchZoomListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            float distx, disty;
            switch(event.getAction() & MotionEvent.ACTION_MASK) {

                //TODO
                case MotionEvent.ACTION_POINTER_DOWN:
                    touchState = PINCH;
                    distx = event.getX(0) - event.getX(1);
                    disty = event.getY(0) - event.getY(1);
                    dist0 = (float) Math.sqrt(distx * distx + disty * disty);
                    break;

                //TODO
                case MotionEvent.ACTION_DOWN:
                    touchState = TOUCH;
                    mx = event.getX();
                    my = event.getY();
                    break;

                //TODO
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

                //TODO
                case MotionEvent.ACTION_UP:
                    if (touchState == TOUCH) {
                        curX = event.getX();
                        curY = event.getY();
                        img.scrollBy((int) (mx - curX), (int) (my - curY));
                    }
                    touchState = IDLE;
                    break;

                //TODO
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

    //TODO
    View.OnTouchListener ApplyWithFingerListener = new View.OnTouchListener(){

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            //TODO
            int[] viewCoords = new int[2];
            img.getLocationOnScreen(viewCoords);
            int touchX, touchY;
            MaBitmap copy = courant.copie();//Copy if yourBMP is not mutable
            Canvas canvas = new Canvas(copy.bmp);
            MaBitmap transform = courant.copie();
            transform.enGris();
            int test = 0;

            switch(event.getAction() & MotionEvent.ACTION_MASK) {

                //TODO
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

                //TODO
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

                //TODO
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

