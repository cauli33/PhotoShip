package com.example.emmaveillat.finalproject;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.nio.IntBuffer;
import java.util.Arrays;

/**
 * La classe MaBitmap est une classe définissant le type d'image propre à l'application. Elle
 * contient en paramètres l'image en elle-même, sa taille, la tableau de pixels correspondant,
 * un témoin si un filtre est appliqué, son histogramme ainsi qu'une map à appliquer pour les
 * convolutions. Elle permet également l'application de filtres simples (passage en niveaux de gris,
 * filtre sépia ...), de convolutions (flou gaussien, filtre laplacien ...), de gestion
 * d'histogramme ou encore de modification de l'image en elle-même comme le rognage.
 */
public class MaBitmap {

    /**
     * l'image utilisée.
     */
    public Bitmap bmp;

    /**
     * la largeur de l'image.
     */
    public int largeur;

    /**
     * la hauteur de l'image.
     */
    public int hauteur;

    /**
     * le tableau de pixels correspondants à l'image.
     */
    public int[] pixels;

    /**
     * le filtre utilisé.
     */
    public int filtre;

    /**
     * l'histogramme de l'image, initialisé à null.
     */
    public int[] histogramme = null;

    /**
     * le tableau correspondant à celui des pixels une fois l'image passée en niveaus de gris,
     * initialisé à null.
     */
    public int[] valMap = null;

    /**
     * le tableau servant à gérer les bordures lors des calculs de convolution, initialisé à null.
     */
    public int[] bordures = null;

    /**
     * le tableau servant à calculer le filtre de Sobel, initialisé à null;
     */
    public int[] mapSobel = null;

    /**
     * fonction qui crée une image utilisable par l'application à partir d'une bitmap classique et
     * d'un filtre. Elle récupère donc sa hauteur, sa largeur et ses pixels dans un tableau et
     * évidemment le filtre appliqué.
     * @param bitmap l'image à manipuler
     * @param filt le filtre utilisé
     */
    public MaBitmap(Bitmap bitmap, int filt){
        bmp = bitmap;
        largeur = bmp.getWidth();
        hauteur = bmp.getHeight();
        pixels = new int[largeur*hauteur];
        bmp.getPixels(pixels, 0, largeur, 0, 0, largeur, hauteur);
        filtre = filt;
    }

    /**
     * fonction qui copie une image existante. Elle est ainsi créée en passant en paramètres dans le
     * constructeur l'image à dupliquer avec son filtre.
     * @return l'image copiée
     */
    public MaBitmap copie(){
        return new MaBitmap(bmp, filtre);
    }

    /**
     * fonction qui récupère l'histogramme de l'image que l'utilisateur manipule actuellement.
     * Pour cela, elle parcourt le tableau de pixels de l'image, et place, dans le tableau
     * histogram, à la position correspondante le niveau de gris des pixels lus dans le tableau.
     */
    public void calculHistogramme() {
        //initialisation des tableaux valMap et histogram
        valMap = new int[largeur * hauteur];
        histogramme = new int[256];
        //initialisation des futures valeurs de rouge, vert et bleu du pixel, ainsi que du niveau de
        //gris associé
        int pixel, r, g, b, lvl;
        for (int i = 0; i < largeur * hauteur; i++) {
            //récupération des valeurs RGB du pixel dans l'image initiale
            pixel = pixels[i];
            r = Color.red(pixel);
            b = Color.blue(pixel);
            g = Color.green(pixel);
            //moyenneur des 3 valeurs et donne le niveau de gris associé
            lvl = (int) (0.299F * r + 0.587F * g + 0.114F * b);
            valMap[i] = lvl;
            histogramme[lvl]++;
        }
    }

    /**
     * fonction qui transforme l'image en niveaux de gris associés. Elle parcourt chaque pixel de
     * l'image à transformer, calcule la moyenneur des canaux RGB et retourne l'image grisée.
     * @return l'image en niveaux de gris
     */
    public MaBitmap enGris(){
        //si l'histogramme n'est pas déjà défini dans le tableau histogram, il est calculé via la
        //fonction calculHistogramme().
        if (histogramme == null){
            calculHistogramme();}
        int[] pixelsGray = new int[hauteur * largeur];
        //copie de l'image à transformer en la rendant "modifiable" grâce au booléen true.
        Bitmap bmpGray = bmp.copy(Bitmap.Config.ARGB_8888, true);
        int lvl;
        for (int i = 0; i < largeur * hauteur; i++) {
            //récupération des niveaux de gris à partir du tableau valMap
            lvl = valMap[i];
            pixelsGray[i] = Color.rgb(lvl,lvl,lvl);
        }
        bmpGray.setPixels(pixelsGray, 0, largeur, 0, 0, largeur, hauteur);
        //création d'une image utilisable par l'application grâce à l'image transformée en niveaux
        //de gris et au filtre gris associé à l'entier 1.
        MaBitmap gray = new MaBitmap(bmpGray, 1);
        gray.histogramme = histogramme;
        gray.valMap = valMap;
        return gray;
    }

    /**
     * fonction qui permet d'inverser les couleurs 'une image (filtre négatif). En récupérant chaque
     * valeur des canaux RGB, on effectue la différence de 255 par celles-ci afin d'obtenir les
     * valeurs contraires.
     * @return l'image avec ses couleurs inversées
     */
    public MaBitmap inverser(){
        int r, g, b, pixel;
        int[] pixelsInv = new int[hauteur * largeur];
        for (int i = 0; i < largeur * hauteur; i++) {
            //on soustrait à 255 chaque valeur de rouge, bleu et vert pour trouver leur valeur
            // inverse, puis on les remet dans le pixel correspondant.
            pixel = pixels[i];
            r = 255 - Color.red(pixel);
            g = 255 - Color.green(pixel);
            b = 255 - Color.blue(pixel);
            pixelsInv[i] = Color.rgb(r, g, b);
        }
        //ajout de la nouvelle image dans la liste servant d'historique de modifications.
        Bitmap bmpInv = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmpInv.setPixels(pixelsInv, 0, largeur, 0, 0, largeur, hauteur);
        MaBitmap inverted = new MaBitmap(bmpInv, 13);
        return inverted;
    }

    /**
     * fonction qui recalcule la taille de l'image en fonction du facteur passé en paramètre et
     * recrée une image à la taille adéquate
     * @param facteur le facteur d'agrandissement ou de réduction de l'image
     * @return l'image avec sa nouvelle taille
     */
    public Bitmap miseAEchelle(float facteur){
        if (facteur < 0.1){
            facteur = 0.1f;
        }
        int nh = (int) (hauteur * facteur);
        int nl = (int) (largeur * facteur);
        return Bitmap.createScaledBitmap(bmp, nl, nh, false);
    }

    /**
     * fonction qui applique un filtre sépia sur toute l'image. À partir des valeurs de rouge et de
     * vert de chaque pixel, elle calcule les valeurs équivalentes en sépia (la valeur de bleu est
     * ainsi écrasée).
     * @return l'image avec un filtre sépia
     */
    public MaBitmap sepia(){
        //initialisation d'entiers et du tableau contenant les pixels après application du filtre.
        if (histogramme == null){
            calculHistogramme();}
        int r, g, lvl;
        int[] pixelsSepia = new int[hauteur * largeur];
        int temoin = 20;
        //application du filtre sur chaque pixel suivant les valeurs de rouge, de vert, de valMap et
        //du temoin.
        for (int i = 0; i < largeur * hauteur; i++) {
            lvl = valMap[i];
            r = lvl + (temoin *2);
            g = lvl + temoin;
            if(r > 255) {
                r = 255;
            }
            if(g > 255) {
                g = 255;
            }
            pixelsSepia[i] = Color.rgb(r, g, lvl);
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpSepia = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmpSepia.setPixels(pixelsSepia, 0, largeur, 0, 0, largeur, hauteur);
        MaBitmap sepia = new MaBitmap(bmpSepia, 2);
        return sepia;
    }

    /**
     * fonction qui égalise l'histogramme de l'image à transformer. En utilisant l'espace HSV, on
     * peut ainsi améliorer les contrastes de l'image si celle-ci est trop sombre.
     * @return l'image avec son histogramme égalisé
     */
    public MaBitmap egalisationHistogramme(){
        //si l'histogramme n'a pas déjà été trouvé, le calculer
        if (histogramme == null){
            calculHistogramme();}
        int[] pixelsEqualized = new int[hauteur * largeur];
        //calcul de l'histogramme cumulé
        for (int i = 1; i < 256; i++) {
            histogramme[i] += histogramme[i - 1];
        }
        float[] pixelHSV = new float[3];
        int pixel;
        float newValue;
        //calcul des valeurs de l'histogramme égalisé par rapport à son histogramme cumulé et à
        // valMap
        for (int i = 0; i < largeur * hauteur; i++) {
            pixel = pixels[i];
            newValue = (float) (histogramme[valMap[i]]) / (largeur * hauteur);
            Color.colorToHSV(pixel, pixelHSV);
            pixelHSV[2] = newValue;
            pixelsEqualized[i] = Color.HSVToColor(pixelHSV);
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpEqualized = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmpEqualized.setPixels(pixelsEqualized, 0, largeur, 0, 0, largeur, hauteur);
        MaBitmap equalized = new MaBitmap(bmpEqualized, 3);
        return equalized;
    }

    /**
     * fonction qui calcule une extension linéaire de dynamiques de l'image. Cela équivaut à
     * améliorer la luminosité de l'image grâce à la manipulation de son histogramme.
     * @return l'image modifiée avec une extension linéaire de dynamiques
     */
    public MaBitmap extensionDynamiques(){
        //si l'histogramme n'a pas déjà été trouvé, le calculer.
        if (histogramme == null){
            calculHistogramme();}
        int[] pixelsExtension = new int[hauteur * largeur];

        //détermination des valeurs min et max de l'histogramme
        int k = 0;
        while (histogramme[k] == 0) {
            k++;
        }
        int min = k;

        k = 255;
        while (histogramme[k] == 0) {
            k--;
        }
        int max = k;

        float[] pixelHSV = new float[3];
        int pixel;
        float nValeur;
        //calcul pour chaque pixel de sa valeur après extension linéaire de dynamiques
        for (int i = 0; i < largeur * hauteur; i++) {
            pixel = pixels[i];
            nValeur = (float)(valMap[i] - min)/(max - min);
            Color.colorToHSV(pixel, pixelHSV);
            pixelHSV[2] = nValeur;
            pixelsExtension[i] = Color.HSVToColor(pixelHSV);
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpExtension = bmp.copy(Bitmap.Config.ARGB_8888, true);
        bmpExtension.setPixels(pixelsExtension, 0, largeur, 0, 0, largeur, hauteur);
        MaBitmap extension = new MaBitmap(bmpExtension, 4);
        return extension;
    }

    /**
     * fonction qui retourne l'image dans le sens contraire des aiguilles d'une montre. On
     * réattribue chaque pixel à la position équivalente dans l'image retournée via leurs tableaux.
     * @return l'image retournée vers la gauche
     */
    public MaBitmap rotationGauche(){
        int[] pixelsGauche = new int[hauteur * largeur];
        for (int y = 0; y < hauteur; ++y) {
            for (int x = 0 ; x < largeur ; ++x) {
                pixelsGauche[( largeur - x - 1) * hauteur + y] = pixels[y * largeur + x];
            }
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpGauche = Bitmap.createBitmap(pixelsGauche, hauteur, largeur, Bitmap.Config.ARGB_8888);
        MaBitmap gauche = new MaBitmap(bmpGauche, filtre);
        return gauche;
    }

    /**
     * fonction qui retourne l'image dans le sens des aiguilles d'une montre. On réattribue chaque
     * pixel à la position correspondante dans l'image retournée via leurs tableaux.
     * @return l'image retournée vers la droite
     */
    public MaBitmap rotationDroit(){
        int[] pixelsRight = new int[hauteur * largeur];
        for (int y = 0; y < hauteur; ++y) {
            for (int x = 0 ; x < largeur ; ++x) {
                pixelsRight[(x+1)*hauteur - y - 1] = pixels[y * largeur + x];
            }
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpRight = Bitmap.createBitmap(pixelsRight, hauteur, largeur, Bitmap.Config.ARGB_8888);
        MaBitmap right = new MaBitmap(bmpRight, filtre);
        return right;
    }

    /**
     * fonction qui retourne l'image suivant un axe horizontal (reflet dans l'eau). On réattribue
     * chaque pixel à la position correspondante dans l'image retournée via leurs tableaux.
     * @return l'image retournée horizontalement
     */
    public MaBitmap miroirHorizontal(){
        int[] pixelsMH = new int[largeur * hauteur];
        for (int y = 0; y < hauteur; ++y) {
            for (int x = 0 ; x < largeur ; ++x) {
                pixelsMH[(y+1) * largeur - x - 1] = pixels[y * largeur + x];
            }
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpMH = Bitmap.createBitmap(pixelsMH, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap miroirH = new MaBitmap(bmpMH, filtre);
        return miroirH;
    }

    /**
     * fonction qui retourne l'image suivant un axe vertical (reflet dans un miroir). On réattribue
     * chaque pixel à la position correspondante dans l'image retournée via leurs tableaux.
     * @return l'image retournée verticalement
     */
    public MaBitmap miroirVertical(){
        int[] pixelsMV = new int[largeur * hauteur];
        for (int y = 0; y < hauteur; ++y) {
            for (int x = 0 ; x < largeur ; ++x) {
                pixelsMV[(hauteur - y - 1) * largeur + x] = pixels[y * largeur + x];
            }
        }
        //copie de l'image dans la liste servant d'historique de modifications
        Bitmap bmpMV = Bitmap.createBitmap(pixelsMV, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap miroirV = new MaBitmap(bmpMV, filtre);
        return miroirV;
    }

    /**
     * fonction qui permet d'appliquer les filtres moyenneur, gaussien grâce à
     * un masque de taille prédéfini. Pour ne pas avoir à gérer le problème des bords, toutes les
     * bordures de l'image sont passées en blanc. L'utilisateur n'aura qu'à rogner ces bordures si
     * celles-ci le dérangent grâce à l'outil de rognage.
     * @param masque matrice à appliquer
     * @param fact facteur à appliquer aux canaux RGB
     * @param filt entier correspondant au filtre que l'utilisateur veut appliquer
     * @return l'image avec le filtre appliqué
     */
    private MaBitmap convolution(int[][] masque, int fact, int filt) {
        //initialisation de valeurs comme le masque, le tableau de pixels transformés ou les valeurs
        //des canaux RGB
        int n = masque.length / 2;
        int[] pixelsConv = new int[hauteur * largeur];
        int blanc = Color.rgb(255,255,255);
        int R, G, B, sommeR, sommeG, sommeB, pixel;
        float coef_masque;
        //on transforme les bordures du haut et du bas de l'image en blanc
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < largeur; x++) {
                pixelsConv[y * largeur + x] = blanc;
                pixelsConv[(hauteur - y - 1) * largeur + x] = blanc;
            }
        }
        //on transforme les bordures de gauche et de droite de l'image en blanc
        for (int x = 0; x < n; x++) {
            for (int y = n; y < hauteur - n; y++) {
                pixelsConv[y * largeur + x] = blanc;
                pixelsConv[(y + 1) * largeur - x - 1] = blanc;
            }
        }
        //on prépare la convolution avec le masque à appliquer à chaque pixel
        for (int y = n; y < hauteur - n; y++) {
            for (int x = n; x < largeur - n; x++) {
                sommeR = 0;
                sommeG = 0;
                sommeB = 0;
                for (int j = -n; j <= n; j++) {
                    for (int i = -n; i <= n; i++) {
                        coef_masque = masque[j + n][i + n];
                        pixel = pixels[(y + j) * largeur + x + i];
                        //on multiplie chaque composante RGB par les coefficients du masque de
                        //convolution
                        sommeR += coef_masque * Color.red(pixel);
                        sommeG += coef_masque * Color.green(pixel);
                        sommeB += coef_masque * Color.blue(pixel);
                    }
                }
                R = sommeR / fact;
                G = sommeG / fact;
                B = sommeB / fact;

                pixelsConv[y * largeur + x] = Color.rgb(R, G, B);
            }
        }
        Bitmap bmpConv = Bitmap.createBitmap(pixelsConv, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap conv = new MaBitmap(bmpConv, filt);
        return conv;
    }

    /**
     * fonction qui applique un filtre moyenneur via la fonction de convolution. On définit un
     * masque (matrice) entièrement à 1 qu'on applique à l'image dans la fonction de convolution.
     * @param n taille du masque à appliquer
     * @return l'image avec le filtre moyenneur
     */
    public MaBitmap moyenneur(int n) {
        int[][] masque = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {masque[i][j] = 1;}
        }
        return convolution(masque, n*n, 5);
    }

    /**
     * fonction qui applique un filtre gaussien sur l'image. Pour ce faire, on définit un masque
     * (matrice) précis à appliquer via la fonction de convolution.
     * @return l'image avec un filtre gaussien
     */
    public MaBitmap gauss(){
        int[][] masque ={{1,2,3,2,1},{2,6,8,6,2},{3,8,10,8,3},{2,6,8,6,2},{1,2,3,2,1}};
        return convolution(masque, 98, 6);
    }

    /**
     * fonction qui aide à appliquer les filtres laplacien et sobel grâce à
     * un masque de taille prédéfini. Pour ne pas avoir à gérer le problème des bords, on garde les
     * valeurs originelles des pixels.
     * @param masque matrice à appliquer
     * @return un tableau qui à chaque pixel donne un tableau de 3 cases pour les composantes RGB
     * on a besoin de renvoyer de cette facon car les valeurs ne restent pas entre 0 et 255
     */
    public int[][] borduresConvol(int[][] masque) {
        int n = masque.length / 2;
        int[][] pixelsConvRGB = new int[hauteur * largeur][3];
        int sommeR, sommeG, sommeB;
        int pixel;
        //On garde les valeurs d'origine pour les bordures
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < largeur; x++) {
                pixel = pixels[y * largeur + x];
                pixelsConvRGB[y * largeur + x][0] = Color.red(pixel);
                pixelsConvRGB[y * largeur + x][1] = Color.green(pixel);
                pixelsConvRGB[y * largeur + x][2] = Color.blue(pixel);
                pixelsConvRGB[(hauteur - y - 1) * largeur + x][0] = Color.red(pixel);
                pixelsConvRGB[(hauteur - y - 1) * largeur + x][1] = Color.green(pixel);
                pixelsConvRGB[(hauteur - y - 1) * largeur + x][2] = Color.blue(pixel);
            }
        }
        for (int y = hauteur - n; y < hauteur - n; y++) {
            for (int x = 0; x < n; x++) {
                pixel = pixels[y * largeur + x];
                pixelsConvRGB[y * largeur + x][0] = Color.red(pixel);
                pixelsConvRGB[y * largeur + x][1] = Color.green(pixel);
                pixelsConvRGB[y * largeur + x][2] = Color.blue(pixel);
                pixelsConvRGB[(y + 1) * largeur - x - 1][0] = Color.red(pixel);
                pixelsConvRGB[(y + 1) * largeur - x - 1][1] = Color.green(pixel);
                pixelsConvRGB[(y + 1) * largeur - x - 1][2] = Color.blue(pixel);
            }
        }
        //Convolution classique
        float coef_masque;
        for (int y = n; y < hauteur - n; y++) {
            for (int x = n; x < largeur - n; x++) {
                sommeR = 0;
                sommeG = 0;
                sommeB = 0;
                for (int j = -n; j <= n; j++) {
                    for (int i = -n; i <= n; i++) {
                        coef_masque = masque[j + n][i + n];
                        pixel = pixels[(y + j) * largeur + x + i];
                        sommeR += coef_masque * Color.red(pixel);
                        sommeG += coef_masque * Color.green(pixel);
                        sommeB += coef_masque * Color.blue(pixel);
                    }
                }
                //Pour les filtres laplacien et sobel, une valeur et son opposé sont aussi
                //intéressantes
                if (sommeR < 0) {
                    sommeR = -sommeR;
                }
                if (sommeG < 0) {
                    sommeG = -sommeG;
                }
                if (sommeB < 0) {
                    sommeB = -sommeB;
                }
                pixelsConvRGB[y * largeur + x][0] = sommeR;
                pixelsConvRGB[y * largeur + x][1] = sommeG;
                pixelsConvRGB[y * largeur + x][2] = sommeB;
            }
        }
        return pixelsConvRGB;
    }

    /**
     * le filtre sobel permet d'afficher les contours à partir de deux convolutions permettant
     * de faire la dérivée seconde horizontalement et verticalement. On fait la norme de ces deux
     * résultats et enfin on revient à un espace de 0 à 255.
     * @return l'image avec sobel appliqué
     */
    public MaBitmap sobel(){
        int R,G,B;
        int[][] hx = {{-1,0,1},{-2,0,2},{-1,0,1}};
        int[][] Gx = borduresConvol(hx);
        int[][] hy = {{-1,-2,-1},{0,0,0},{1,2,1}};
        int[][] Gy = borduresConvol(hy);

        int[][] norme = new int[largeur*hauteur][3];

        int[] pixelsSobel = new int[largeur*hauteur];
        int max = 0;

        //On fait la norme pour chaque valeur rgb et on cherche la valeur maximum
        for (int i=0; i<hauteur*largeur; i++) {
            norme[i][0] = (int) Math.sqrt(Math.pow(Gx[i][0], 2) + Math.pow(Gy[i][0], 2));
            norme[i][1] = (int) Math.sqrt(Math.pow(Gx[i][1], 2) + Math.pow(Gy[i][1], 2));
            norme[i][2] = (int) Math.sqrt(Math.pow(Gx[i][2], 2) + Math.pow(Gy[i][2], 2));
            if (norme[i][0] > max){max = norme[i][0];}
            if (norme[i][1] > max){max = norme[i][1];}
            if (norme[i][2] > max){max = norme[i][2];}
        }
        //On revient dans l'espace de 0 à 255 grâce à la valeur maximum trouvée
        if (max!=0){
            for (int i=0; i<hauteur*largeur; i++) {
                R = norme[i][0] * 255 / max;
                G = norme[i][1] * 255 / max;
                B = norme[i][2] * 255 / max;
                pixelsSobel[i] = Color.rgb(R,G,B);
            }
        }
        Bitmap bmpSobel = Bitmap.createBitmap(pixelsSobel, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap sobel = new MaBitmap(bmpSobel, 7);
        return sobel;
    }

    /**
     * le filtre laplacien permet d'afficher les contours.
     * On doit faire attention à revenir entre 0 et 255 pour les valeurs rgb.
     * La méthode est similaire à la deuxième partie de sobel.
     * @return l'image avec laplacien appliqué
     */
    public MaBitmap laplacien(){
        int R,G,B;
        //application des matrices hx et hy
        int[][] masque = {{1,1,1},{1,-8,1},{1,1,1}};
        int[][] Gl = borduresConvol(masque);

        int[] pixelsLapla = new int[largeur*hauteur];
        int max = 0;
        for (int i=0; i<hauteur*largeur; i++) {
            if (Gl[i][0] > max){max = Gl[i][0];}
            if (Gl[i][1] > max){max = Gl[i][1];}
            if (Gl[i][2] > max){max = Gl[i][2];}
        }
        if (max!=0){
            for (int i=0; i<hauteur*largeur; i++) {
                R = Gl[i][0] * 255 / max;
                G = Gl[i][1] * 255 / max;
                B = Gl[i][2] * 255 / max;
                pixelsLapla[i] = Color.rgb(R,G,B);
            }
        }
        Bitmap bmpLapla = Bitmap.createBitmap(pixelsLapla, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap laplacien = new MaBitmap(bmpLapla, 8);
        return laplacien;
    }

    /**
     * fonction qui permet d'appliquer les filtres nécessitant 1 à 3 seekbars
     * @param filtUtil le filtre à appliquer à l'image
     * @param v1 valeur de la première seekbar
     * @param v2 valeur de la deuxième seekbar
     * @param v3 valeur de la troisième seekbar
     * @return l'image après avoir passé dans un des filtres appelés dans la fonction
     */
    public MaBitmap applicationFiltre(int filtUtil, int v1, int v2, int v3){
        //si l'utilisateur souhaite appliquer le filtre de sélection de teinte, il a les valeurs des
        //deux seekbars nécessaires
        if (filtUtil == 1){
            return selectionTeinte(v1, v2);
        }
        //si l'utilisateur souhaite appliquer le filtre de changement de teinte, il a la valeur de
        //la seekbar nécessaire
        if (filtUtil == 2){
            return changeTeinte(v1);
        }
        //
        if (filtUtil == 3){
            return borduresCartoon(1F - (float)v1/100F);
        }
        //si l'utilisateur souhaite appliquer le filtre pour changer l'espace HSV, il a alors les
        //trois valeurs de teinte, saturation et valeur.
        if (filtUtil == 4){
            return changeHSV((float)v1, (float)v2/100F, (float)v3/100);
        }
        return null;
    }

    /**
     * fonction qui garde les pixels de la teinte demandée et transforme le reste en noir et blanc
     * @param pas tolérance par rapport à la teinte donnée
     * @param teinte teinte demandée et donnée par la seekbar
     * @return l'image transformée
     */
    private MaBitmap selectionTeinte(int pas, int teinte){
        int[] pixelsSelect = pixels.clone();
        float[] pixelHSV = new float[3];
        int r, g, b, gris;
        int index = 0;
        //Il y a répétition de code pour éviter un calcul compliqué à chaque pixel, trois situations
        //Dans tous les cas, on ne fait rien si la teinte du pixel nous convient, sinon on passe en
        //niveaux de gris

        //Si la teinte choisie est suffisamment éloignée de 0 ou 360 ( plus loin que la tolérance )
        if ((teinte >= pas)&&(teinte<=360F - pas)){
            for (int y = 0; y < hauteur; ++y) {
                for (int x = 0; x < largeur; ++x) {
                    int pixel = pixelsSelect[index];
                    Color.colorToHSV(pixel, pixelHSV);
                    float teintePixel = pixelHSV[0];
                    //Get pixels out of the gap defined around teinte value in gray
                    if ((teintePixel >= teinte + pas) || (teintePixel <= teinte - pas)) {
                        r = Color.red(pixel);
                        g = Color.blue(pixel);
                        b = Color.green(pixel);
                        gris = (int) (0.6F*r + 0.2F*g + 0.1*b);
                        pixelsSelect[index] = Color.rgb(gris, gris, gris);
                    }
                    index++;
                }
            }
        }
        //Si la teinte choisie est plus proche de 0 que la tolérance on doit accepter certaines
        //valeurs proches de 360
        else if (teinte < pas){
            for (int y = 0; y < hauteur; ++y) {
                for (int x = 0; x < largeur; ++x) {
                    int pixel = pixelsSelect[index];
                    Color.colorToHSV(pixel, pixelHSV);
                    float pixelHue = pixelHSV[0];
                    if ((pixelHue >= teinte + pas) && (pixelHue <= teinte + 360F - pas)) {
                        r = Color.red(pixel);
                        g = Color.blue(pixel);
                        b = Color.green(pixel);
                        gris = (int) (0.6F*r + 0.2F*g + 0.1*b);
                        pixelsSelect[index] = Color.rgb(gris, gris, gris);
                    }
                    index++;
                }
            }
        }
        //Si la teinte choisie est plus proche de 360 que la tolérance on doit accepter certaines
        //valeurs proches de 0
        else{
            for (int y = 0; y < hauteur; ++y) {
                for (int x = 0; x < largeur; ++x) {
                    int pixel = pixelsSelect[index];
                    Color.colorToHSV(pixel, pixelHSV);
                    float pixelHue = pixelHSV[0];
                    if ((pixelHue >= teinte -360F + pas) && (pixelHue <= teinte - pas)) {
                        r = Color.red(pixel);
                        g = Color.blue(pixel);
                        b = Color.green(pixel);
                        gris = (int) (0.6F*r + 0.2F*g + 0.1*b);
                        pixelsSelect[index] = Color.rgb(gris, gris, gris);
                    }
                    index++;
                }
            }
        }
        Bitmap bmpSelect = Bitmap.createBitmap(pixelsSelect, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap select = new MaBitmap(bmpSelect, 10);
        return select;
    }

    /**
     * fonction qui permet de changer la teinte d'une image sans modifier la luminosité, les
     * contrastes ou encore la saturation
     * @param teinte la teinte à remplacer
     * @return l'image ayant un filtre de couleur unie
     */
    public MaBitmap changeTeinte(int teinte){
        int[] pixelsFilt = new int[largeur * hauteur];
        float[] pixelHSV = new float[3];

        //pour chaque pixel on remplace la valeur de teinte dans l'espace HSV
        for (int i = 0; i < largeur*hauteur; ++i) {
            int pixel = pixels[i];
            Color.colorToHSV(pixel, pixelHSV);
            pixelHSV[0] = teinte;
            pixelsFilt[i] = Color.HSVToColor(pixelHSV);
        }
        Bitmap bmpFilt = Bitmap.createBitmap(pixelsFilt, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap filtre = new MaBitmap(bmpFilt, 11);
        return filtre;
    }

    /**
     * fonction qui permet de changer indépendament les valeurs de l'espace HSV d'une image. Elle
     * supporte les dépassements d'intervalles.
     * @param teinte la valeur à ajouter pour modifier la teinte
     * @param sat la valeur à ajouter pour modifier la saturation
     * @param val la valeur à ajouter pour modifier la luminosité
     * @return l'image transformée
     */
    public MaBitmap changeHSV(float teinte, float sat, float val){
        //création d'un tableau de pixels pour y placer les pixels modifiés
        int[] pixelsFilt = new int[largeur * hauteur];
        float[] pixelHSV = new float[3];
        for (int i = 0; i < largeur*hauteur; ++i) {
            //pour chaque pixel on passe dans l'espace HSV
            int pixel = pixels[i];
            Color.colorToHSV(pixel, pixelHSV);
            //concernant la teinte
            pixelHSV[0] += teinte;
            if (pixelHSV[0] < 0.0f) {
                pixelHSV[0] += 360.0f;
            } else if (pixelHSV[0] > 360.0f) {
                pixelHSV[0] -= 360.0f;
            }
            //concernant la saturation
            pixelHSV[1] += sat;
            if (pixelHSV[1] < 0.0f) {
                pixelHSV[1] += 1.0f;
            } else if (pixelHSV[1] > 1.0f) {
                pixelHSV[1] -= 1.0f;
            }
            //concernant la luminosité
             pixelHSV[2] += val;
            if (pixelHSV[2] < 0.0f) {
                pixelHSV[2] += 1.0f;
            } else if (pixelHSV[2] > 1.0f) {
                pixelHSV[2] -= 1.0f;
            }
            pixelsFilt[i] = Color.HSVToColor(pixelHSV);
        }
        //création de la MaBitmap à partir du tableau de pixels précédent
        Bitmap bmpFilt = Bitmap.createBitmap(pixelsFilt, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap filtre = new MaBitmap(bmpFilt, 11);
        return filtre;
    }

    /**
     * fonction qui permet d'afficher les traits noirs pour que l'utilisateur voit ce qu'il va
     * rogner
     * @param zone tableau des 4 valeurs haut, bas, gauche, droite de ce qu'on va rogner
     * @return l'image avec les 4 barres d'indication en noir
     */
    public MaBitmap affichageRogne(int[] zone){
        int[] pixelsRogne = pixels.clone();
        int RogneHaut = zone[0] * hauteur / 100;
        int RogneBas = hauteur - zone[1] * hauteur / 100;
        int RogneGauche = zone[2] * largeur / 100;
        int RogneDroite = largeur - zone[3] * largeur / 100;

        for (int y = RogneHaut; y < RogneHaut + 4; ++y) {
            for (int x = RogneGauche ; x < RogneDroite ; ++x) {
                pixelsRogne[ y * largeur + x ] = Color.rgb(0,0,0);
            }
        }
        for (int y = RogneBas - 4; y < RogneBas; ++y) {
            for (int x = RogneGauche; x < RogneDroite; ++x) {
                pixelsRogne[ y * largeur + x ] = Color.rgb(0,0,0);
            }
        }
        for (int x = RogneGauche; x < RogneGauche + 4; ++x) {
            for (int y = RogneHaut; y < RogneBas; ++y) {
                pixelsRogne[ y * largeur + x ] = Color.rgb(0,0,0);
            }
        }
        for (int x = RogneDroite - 4; x < RogneDroite; ++x) {
            for (int y = RogneHaut; y < RogneBas; ++y) {
                pixelsRogne[ y * largeur + x ] = Color.rgb(0,0,0);
            }
        }
        Bitmap bmprogne = Bitmap.createBitmap(pixelsRogne, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap rogne = new MaBitmap(bmprogne, filtre);
        return rogne;
    }

    /**
     * fonction qui rogne l'image selon la zone donnée
     * La fonction ne fait que "recalculer" les paramètres donnés en entrée, le rognage est fait
     * grâce à la fonction createBitmap
     * @param zone tableau des 4 valeurs haut, bas, gauche, droite de ce qu'on va rogner
     * @return l'image rognée
     */
    public MaBitmap rogner(int[] zone){
        int RogneHaut = zone[0] * hauteur / 100;
        int RogneBas = hauteur - zone[1] * hauteur / 100;
        int RogneGauche = zone[2] * largeur / 100;
        int RogneDroite = largeur - zone[3] * largeur / 100;
        int nl = RogneDroite - RogneGauche;
        int nh = RogneBas - RogneHaut;
        Bitmap bmpRogne = Bitmap.createBitmap(bmp, RogneGauche,RogneHaut,nl, nh);
        MaBitmap rogne = new MaBitmap(bmpRogne, filtre);
        return rogne;
    }

    /**
     * Code de Pratik sur stackoverflow.com
     * Nous avons voulu essayer l'effet dessin généralement conseillé sur internet, ne comprenant
     * pas le calcul effectué dans densiteCouleur, nous ne pouvons expliqué cette fonction
     * Elle mélange deux images d'une certaine façon
     * @param calque la deuxième image à mélanger
     * @return les deux images "mélangées"
     */
    private MaBitmap melangeDensiteCouleur(MaBitmap calque) {
        Bitmap base = bmp.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap mélange = calque.bmp.copy(Bitmap.Config.ARGB_8888, false);

        IntBuffer buffBase = IntBuffer.allocate(largeur * hauteur);
        base.copyPixelsToBuffer(buffBase);
        buffBase.rewind();

        IntBuffer buffMelange = IntBuffer.allocate(calque.largeur * calque.hauteur);
        mélange.copyPixelsToBuffer(buffMelange);
        buffMelange.rewind();

        IntBuffer buffO = IntBuffer.allocate(largeur * hauteur);
        buffO.rewind();

        while (buffO.position() < buffO.limit()) {

            int filtEntier = buffMelange.get();
            int srcEntier = buffBase.get();

            int filtRouge = Color.red(filtEntier);
            int filtVert = Color.green(filtEntier);
            int filtBleu = Color.blue(filtEntier);

            int srcRouge = Color.red(srcEntier);
            int srcVert = Color.green(srcEntier);
            int srcBleu = Color.blue(srcEntier);

            int finalRouge = densiteCouleur(filtRouge, srcRouge);
            int finalVert = densiteCouleur(filtVert, srcVert);
            int finalBleu = densiteCouleur(filtBleu, srcBleu);


            int pixel = Color.argb(255, finalRouge, finalVert, finalBleu);


            buffO.put(pixel);
        }

        buffO.rewind();

        base.copyPixelsFromBuffer(buffO);
        mélange.recycle();

        return new MaBitmap(base, 17);
    }

    /**
     * Code de Pratik sur stackoverflow.com
     * Mélange de deux valeurs, nous ne pouvons expliquer plus
     * @param in1 première valeur
     * @param in2 deuxième valeur
     * @return mélange des deux valeurs
     */
    private int densiteCouleur(int in1, int in2) {
        float image = (float)in2;
        float masque = (float)in1;
        return ((int) ((image == 255) ? image:Math.min(255, (((long)masque << 8 ) / (255 - image)))));
    }

    /**
     * fonction qui applique un effet crayon sur l'image. On applique successivement les fonctions
     * de niveaux de gris, la fonction pour inverser les couleurs et le filtre gaussien.
     * On applique finalement la fonction melangeDensiteCouleur
     * @return l'image en effet crayon
     */
    public MaBitmap dessinCrayon(){
        MaBitmap gris = enGris();
        MaBitmap inverse = gris.inverser();
        MaBitmap gauss = inverse.gauss();
        return gris.melangeDensiteCouleur(gauss);
    }

    /**
     * fonction qui déforme l'image originale en fonction de deux entiers passés en paramètres.
     * @param nl entier pour déterminer le facteur de modification de la largeur
     * @param nh entier pour déterminer le facteur de modification de la hauteur
     * @return l'image transformée
     */
    public MaBitmap voisinLePlusProche(int nl, int nh){
        float factX = (float) nl/largeur;
        float factY = (float) nh/hauteur;

        int[] pixelsCN = new int [nl*nh];
        for (int x = 0; x < nl; x++) {
            for (int y = 0; y < nh; y++) {
                //on réattribut la position de chaque pixel dans le nouveau tableau
                pixelsCN[y * nl + x] = pixels[(int)(y / factY) * largeur + (int)(x / factX)];
            }
        }
        //création de l'image de type MaBitmap à partir du tableau de pixels précédent
        Bitmap bmpCN = Bitmap.createBitmap(pixelsCN, nl, nh, Bitmap.Config.ARGB_8888);
        MaBitmap cn = new MaBitmap(bmpCN, 19);
        return cn;
    }

    /**
     * fonction récursive qui renvoie sur le pixel à gauche, à droite, au dessus, en dessous
     * si ils existent et qu'ils n'ont pas déja été visités/qu'il ne s'agit pas d'une bordure.
     * Il donne alors aux pixels visités la même valeur pour pouvoir séparer l'image en plusieurs
     * zones de couleurs séparées par les bordures.
     * Elle stocke en même temps dans sommeRGB la somme des valeurs RGB des pixels de la zone ainsi
     * que le nombre de pixels pour déterminer la couleur "moyenne" de la zone.
     * @param x coordonnée horizontale
     * @param y coordonnée verticale
     * @param l largeur de l'image
     * @param h hauteur de l'image
     * @param sommeRGB tableau de la somme des valeurs RGB et nombre de pixels de la zone
     * @param cmpCouleur numéro donné à la zone
     */
    private void trouverAireCouleur(int x, int y, int l, int h, int[] sommeRGB, int cmpCouleur){
        int index = y*l+x;
        bordures[index]=cmpCouleur;
        int pixel = pixels[index];
        sommeRGB[0] += Color.red(pixel);
        sommeRGB[1] += Color.green(pixel);
        sommeRGB[2] += Color.blue(pixel);
        sommeRGB[3] ++;
        if ((x>0) && (bordures[index-1]==0)){
            trouverAireCouleur(x-1, y, l, h, sommeRGB, cmpCouleur);}
        if ((x<l-1)&&(bordures[index+1]==0)){
            trouverAireCouleur(x+1, y, l, h, sommeRGB, cmpCouleur);}
        if ((y>0)&&(bordures[index-l]==0)){
            trouverAireCouleur(x, y-1, l, h, sommeRGB, cmpCouleur);}
        if ((y<h-1)&&(bordures[index+l]==0)){
            trouverAireCouleur(x, y+1, l, h, sommeRGB, cmpCouleur);}
    }

    /**
     * @return image en "cartoon" couleurs unifiées par zone sans contours
     */
    public MaBitmap cartoon() {
        //bordures est un tableau de la même taille de notre image avec -1 lorsqu'il s'agit
        //d'une bordure et un entier positif pour indiquer la zone dans laquelle on est
        bordures = new int[largeur * hauteur];
        //mapSobel est le tableau des pixels de sobel appliqué sur notre image
        mapSobel = this.sobel().pixels.clone();
        int pixel, r, g, b;
        //On définit les 3 pixels au bord de l'image comme des bordures
        for (int i=0; i<3*largeur; i ++){
            bordures[i] = -1;
            bordures[largeur*hauteur - 1 - i] = -1;
        }
        for (int y=3; y<hauteur -3; y++){
            for (int x=0; x<3; x++) {
                bordures[y * largeur + x] = -1;
                bordures[(y+1) * largeur - x - 1] = -1;
            }
        }
        //Pour les autres pixels, on considère qu'il s'agit d'une bordure si la somme des valeurs
        //rgb du pixel en sobel est supérieure à 30, donc si ce n'est pas trop noir
        for (int i = 0; i < largeur * hauteur; i++) {
            pixel = mapSobel[i];
            r = Color.red(pixel);
            g = Color.green(pixel);
            b = Color.blue(pixel);
            if (r + b + g > 30) {
                bordures[i] = -1;
            }
        }
        //couleurs est le tableau des zones de l'image, chaque entier noté dans ce tableau
        //correspond à l'indice de la couleur dans couleurBase
        int[] couleurs = new int[largeur*hauteur];
        //couleurBase stocke la palette de couleurs "basiques" qu'on utilisera
        int[] couleurBase = new int[1000];
        int cmpBaseC = 0;
        //On commence le compteur de couleurs, donc de zones dans l'image à 1, car 0 signifie
        //qu'on a pas encore visité le pixel pour les bordures
        int cmpCouleur = 1;
        int[] sommeRGB = new int[4];
        int couleur;
        int i;
        int dr, dg, db;
        boolean resume;
        //Dès qu'on croise un pixel non visité dans bordures (à 0)
        //On parcourt toute la zone à laquelle il appartient et trouve la couleur moyenne
        //Si cette couleur est suffisamment proche (2,4,3 sont les coefficients les moints
        //coûteux et considérés comme bons pour comparer deux couleurs selon l'oeil humain)
        //d'une des couleurs de bases déja trouvées, on considère que la couleur de la zone
        //est de la couleur de base en question
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                if (bordures[y * largeur + x] == 0) {
                    Arrays.fill(sommeRGB, 0);
                    trouverAireCouleur(x, y, largeur, hauteur, sommeRGB, cmpCouleur);
                    r = sommeRGB[0] / sommeRGB[3];
                    g = sommeRGB[1] / sommeRGB[3];
                    b = sommeRGB[2] / sommeRGB[3];
                    if (cmpBaseC == 0){
                        couleurBase[cmpBaseC] = Color.rgb(r, g, b);
                        couleurs[cmpCouleur] = cmpBaseC;
                        cmpBaseC++;
                    }
                    else {
                        i = 0;
                        resume = true;
                        while ((i < cmpBaseC)&&(resume)){
                            couleur = couleurBase[i];
                            dr = r - Color.red(couleur);
                            dg = g - Color.green(couleur);
                            db = b - Color.blue(couleur);
                            if (2*dr*dr + 4*dg*dg + 3*db*db < 5000) {
                                couleurs[cmpCouleur] = i;
                                resume = false;
                            }
                            i++;
                        }
                        if (resume) {
                            couleurBase[cmpBaseC] = Color.rgb(r, g, b);
                            couleurs[cmpCouleur] = cmpBaseC;
                            cmpBaseC++;
                        }
                    }
                    cmpCouleur++;
                }
            }
        }

        //On parcourt bordures, si on est pas sur une bordure, on prend la couleur de la zone
        //qu'on a trouvée à l'étape précédente
        //Si on est sur une bordure, on cherche à voir si la couleur originelle du pixel n'est
        //pas proche d'une de nos couleurs de base, sinon on lui laisse sa couleur originelle
        int[] pixelsCartoon = new int[largeur* hauteur];
        for (int j = 0; j < largeur * hauteur; j++) {
            if (bordures[j] != -1) {
                pixelsCartoon[j] = couleurBase[couleurs[bordures[j]]];
            }
            else{
                pixel = pixels[j];
                r = Color.red(pixel);
                g = Color.green(pixel);
                b = Color.blue(pixel);
                i = 0;
                resume = true;
                while ((i < cmpBaseC)&&(resume)){
                    couleur = couleurBase[i];
                    dr = r - Color.red(couleur);
                    dg = g - Color.green(couleur);
                    db = b - Color.blue(couleur);
                    if (2*dr*dr + 4*dg*dg + 3*db*db < 5000) {
                        pixelsCartoon[j] = couleur;
                        resume = false;
                    }
                    i++;
                }
                if (resume){
                    pixelsCartoon[j]=pixels[j];
                }
            }
        }
        Bitmap bmpCartoon = Bitmap.createBitmap(pixelsCartoon, largeur, hauteur, Bitmap.Config.ARGB_8888);
        MaBitmap cartoon = new MaBitmap(bmpCartoon, 14);
        cartoon.bordures = this.bordures.clone();
        cartoon.mapSobel = this.mapSobel.clone();
        return cartoon;
    }

    /**
     * fonction qui trace les contours pour le filtre cartoon
     * à utiliser uniquement après cartoon
     * @param lvl seuil accepté donné par la seekbar
     * @return l'image avec des bordures
     */
    public MaBitmap borduresCartoon(float lvl){
        float[] hsv = new float[3];
        int[] pixelsCartoon = pixels.clone();
        //On ne s'intéresse qu'aux pixels appartenant aux bordures détectées dans cartoon
        for (int i = 0; i < largeur * hauteur; i++) {
            if (bordures[i] == -1) {
                Color.colorToHSV(mapSobel[i], hsv);
                //Si la valeur du pixel est au dessus de notre seuil, on donne au pixel
                //noir/grise selon si on considère que c'est une bordure "importante"
                if (hsv[2] > lvl) {
                    int v = (int) (100 * (1 - hsv[2]));
                    pixelsCartoon[i] = Color.rgb(v, v, v);
                }
            }
        }
        Bitmap bmpCartoon = Bitmap.createBitmap(pixelsCartoon, largeur, hauteur, Bitmap.Config.
                ARGB_8888);
        MaBitmap cartoon = new MaBitmap(bmpCartoon, 14);
        cartoon.bordures = this.bordures.clone();
        cartoon.mapSobel = this.mapSobel.clone();
        return cartoon;
    }

    //TODO Finir application avec doigt
    public MaBitmap applicationDoigt(MaBitmap applique, int departX, int departY, int finX, int finY){
        if (departX < finX){
            return applicationDoigt(applique, finX, finY, departX, departY);
        }
        for (int i = departX*departY; i < finX * finY; i++ ) {
            applique.pixels[i] = Color.rgb(255, 255, 255);
        }
        /*int range = Math.min(width,height)/10;
        int[] pixelsFinger = pixels.clone();
        int x = departX;
        int y = departY;
        int startI, startJ;
        if (finX == departX){
            if (x<range){
                startI = x - range;
            }
            else{startI = -range;}
            if (y<range){
                startJ = y - range;
            }
            else{startJ = -range;}
            for (int i=startI; i<Math.min(range,width - x); i++){
                for (int j=startJ; j<Math.min(range, height - y); j++){
                    pixelsFinger[(y+j) * width + (x+i)] = applique.pixels[(y+j) * width + (x+i)];
                }
            }
        }
        else {
            int step = (finY - departY) * range / (finX - departX);
            int maxX = Math.min(width, finX);
            boolean resume = true;
            while (x < maxX) {
                if (x < range) {
                    startI = x - range;
                } else {
                    startI = -range;
                }
                if (y < range) {
                    startJ = y - range;
                } else {
                    startJ = -range;
                }
                for (int i = startI; i < Math.min(range, width - x); i++) {
                    for (int j = startJ; j < Math.min(range, height - y); j++) {
                        pixelsFinger[(y + j) * width + (x + i)] = applique.pixels[(y + j) * width + (x + i)];
                    }
                }
                x += range;
                y += step;
            }
        }
        Bitmap bmpFinger = Bitmap.createBitmap(pixelsFinger, width, height, Bitmap.Config.ARGB_8888);
        MaBitmap finger = new MaBitmap(bmpFinger, 18);
        return finger;*/
        return applique;
    }
}
