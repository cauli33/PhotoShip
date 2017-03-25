package com.example.emmaveillat.finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Arrays;

/**
 * This class applies convolutions and blurs to a bitmap thanks to some matrix.
 * @author caulihonore
 */
public class ConvolutionMenu extends AppCompatActivity {

    /**
     * Bitmaps used to be modified
     */
    Bitmap pictureToUse, picture;

    /**
     * lenght
     */
    int length;

    /**
     * Text choosen by the user
     */
    EditText lengthText;

    /**
     * Buttons to apply functions, to save or reset a bitmap
     */
    Button save, reset, moy, sobel, gauss, lapla, cartoon;

    /**
     * The bitmap displayed in the menu
     */
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convolution_menu);

        // Creates textzone to choose matrix length for moyenne
        lengthText = (EditText) findViewById(R.id.newLength);
        lengthText.setHint("Notez ici la taille de la matrice (impair)");

        // Gets picture Bitmap chosen in the gallery
        pictureToUse = PhotoLoading.scaleImage();

        picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);

        imgView = (ImageView) findViewById(R.id.picture);
        imgView.setImageBitmap(picture);

        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(blistener);

        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(blistener);

        moy = (Button) findViewById(R.id.moy);
        moy.setOnClickListener(blistener);

        sobel = (Button) findViewById(R.id.sobel);
        sobel.setOnClickListener(blistener);

        gauss = (Button) findViewById(R.id.gauss);
        gauss.setOnClickListener(blistener);

        lapla = (Button) findViewById(R.id.lapla);
        lapla.setOnClickListener(blistener);

        cartoon = (Button) findViewById(R.id.cartoon);
        cartoon.setOnClickListener(blistener);
    }


    public Bitmap reduce(Bitmap bmp){
        int oldWidth = bmp.getWidth();
        int oldHeight = bmp.getHeight();
        if (oldWidth*oldHeight> 1000000) {
            int newWidth, newHeight;
            float fact;
            if (oldWidth > oldHeight) {
                newWidth = 800;
                newHeight = newWidth * oldHeight / oldWidth;
                fact = oldWidth / 800F;
            } else {
                newHeight = 800;
                newWidth = newHeight * oldWidth / oldHeight;
                fact = oldHeight / 800F;
            }
            int[] mapSrc = new int[oldWidth * oldHeight];
            bmp.getPixels(mapSrc, 0, oldWidth, 0, 0, oldWidth, oldHeight);
            int[] mapDest = new int[newWidth * newHeight];
            int oldx, oldy;
            for (int y = 0; y < newHeight; y++) {
                for (int x = 0; x < newWidth; x++) {
                    oldx = (int) (x * fact);
                    oldy = (int) (y * fact);
                    mapDest[y * newWidth + x] = mapSrc[ oldy * oldWidth + oldx];
                }
            }
            Bitmap dest = Bitmap.createBitmap(newWidth,newHeight, Bitmap.Config.ARGB_8888);
            dest.setPixels(mapDest, 0, newWidth, 0, 0, newWidth, newHeight);
            return dest;
        }
        return bmp;
    }

    public void moyenne(Bitmap bmp, int n) {
        //Creates a matrix full of ones and applicates convolution (and divides by the number of pixels used)
        int[][] mask = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                mask[i][j] = 1;
            }
        }
        convolution(bmp, mask, n*n);
    }

    public void gauss(Bitmap bmp){
        //Creates gaussian matrix and applicates convolution
        int[][] mask ={{1,2,3,2,1},{2,6,8,6,2},{3,8,10,8,3},{2,6,8,6,2},{1,2,3,2,1}};
        convolution(bmp, mask, 98);
    }

    public void sobel(Bitmap bmp){
        int R,G,B;
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        //applicates convolution with hx et hy matrix
        int[][] hx = {{-1,0,1},{-2,0,2},{-1,0,1}};
        //int[][] hx = {{0,0,0},{-1,0,1},{0,0,0}};
        int[][] Gx = sobelConvolutionAux(bmp,hx);

        int[][] hy = {{-1,-2,-1},{0,0,0},{1,2,1}};
        //int[][] hy = {{0,-1,0},{0,0,0},{0,1,0}};
        int[][] Gy = sobelConvolutionAux(bmp,hy);

        int[] map = new int[width*height];

        for (int i=0; i<height*width; i++){
            R = (int) Math.sqrt(Math.pow(Gx[0][i],2)+Math.pow(Gy[0][i],2));
            G = (int) Math.sqrt(Math.pow(Gx[1][i],2)+Math.pow(Gy[1][i],2));
            B = (int) Math.sqrt(Math.pow(Gx[2][i],2)+Math.pow(Gy[2][i],2));

            if(R < 0) { R = 0; }
            else if(R > 255) { R = 255; }

            if(G < 0) { G = 0; }
            else if(G > 255) { G = 255; }

            if(B < 0) { B = 0; }
            else if(B > 255) { B = 255; }
            map[i] = Color.rgb(R, G, B);
        }
        bmp.setPixels(map, 0, width, 0, 0, width, height);
    }

    public int[][] sobelConvolutionAux(Bitmap bmp, int[][] mask) {
        int n = mask.length / 2;
        if (n!=0) {
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int[] pixels = new int[height * width];
            int[][] pixelsConvRGB = new int[3][height*width];
            //Gets array of every pixels from the Bitmap
            bmp.getPixels(pixels, 0, width, 0, 0, width, height);
            int sumR, sumG, sumB;
            int pixel;
            //Keeps original values for the borders
            for (int y = 0; y < n; y++) {
                for (int x = 0; x < width; x++) {
                    pixel = pixels[y*width + x];
                    pixelsConvRGB[0][y * width + x] = Color.red(pixel);
                    pixelsConvRGB[1][y * width + x] = Color.green(pixel);
                    pixelsConvRGB[2][y * width + x] = Color.blue(pixel);
                }
            }
            for (int y = height - n; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pixel = pixels[y*width + x];
                    pixelsConvRGB[0][y * width + x] = Color.red(pixel);
                    pixelsConvRGB[1][y * width + x] = Color.green(pixel);
                    pixelsConvRGB[2][y * width + x] = Color.blue(pixel);
                }
            }
            for (int x = 0; x < n; x++) {
                for (int y = n; y < height - n; y++) {
                    pixel = pixels[y*width + x];
                    pixelsConvRGB[0][y * width + x] = Color.red(pixel);
                    pixelsConvRGB[1][y * width + x] = Color.green(pixel);
                    pixelsConvRGB[2][y * width + x] = Color.blue(pixel);
                }
            }
            for (int x = width - n; x < height; x++) {
                for (int y = n; y < height - n; y++) {
                    pixel = pixels[y*width + x];
                    pixelsConvRGB[0][y * width + x] = Color.red(pixel);
                    pixelsConvRGB[1][y * width + x] = Color.green(pixel);
                    pixelsConvRGB[2][y * width + x] = Color.blue(pixel);
                }
            }
            //Convolution avoiding borders
            float coef_mask;
            for (int y = n; y < height - n; y++) {
                for (int x = n; x < width - n; x++) {
                    sumR = 0;
                    sumG = 0;
                    sumB = 0;
                    for (int j = -n; j <= n; j++) {
                        for (int i = -n; i <= n; i++) {
                            coef_mask = mask[j + n][i + n];
                            pixel = pixels[(y + j) * width + x + i];
                            //For every RGB componants, multiplies by convolution matrix coefficient
                            sumR += coef_mask * Color.red(pixel);
                            sumG += coef_mask * Color.green(pixel);
                            sumB += coef_mask * Color.blue(pixel);
                        }
                    }
                    pixelsConvRGB[0][y*width + x]=sumR;

                    pixelsConvRGB[1][y*width + x]=sumG;

                    pixelsConvRGB[2][y*width + x]=sumB;
                }
            }
            return pixelsConvRGB;
        }
        return null;
    }

    public void laplacien(Bitmap bmp){
        //Applicates convolution with Laplacien matrix
        int[][] mask = {{1,1,1},{1,-8,1},{1,1,1}};
        convolution(bmp, mask, 1);
    }


    public void convolution(Bitmap bmp, int[][] mask, int factor) {
        int n = mask.length / 2;
        if (n!=0) {
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int[] pixels = new int[height * width];
            int[] pixelsConv = new int[height * width];
            //Gets array of every pixels from the Bitmap
            bmp.getPixels(pixels, 0, width, 0, 0, width, height);
            int A, R, G, B;
            int sumR, sumG, sumB;
            //Keeps original values for the borders
            for (int y = 0; y < n; y++) {
                for (int x = 0; x < width; x++) {
                    pixelsConv[y * width + x] = pixels[y * width + x];
                }
            }
            for (int y = height - n; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pixelsConv[y * width + x] = pixels[y * width + x];
                }
            }
            for (int x = 0; x < n; x++) {
                for (int y = n; y < height - n; y++) {
                    pixelsConv[y * width + x] = pixels[y * width + x];
                }
            }
            for (int x = width - n; x < height; x++) {
                for (int y = n; y < height - n; y++) {
                    pixelsConv[y * width + x] = pixels[y * width + x];
                }
            }
            //Convolution avoiding borders
            for (int y = n; y < height - n; y++) {
                for (int x = n; x < width - n; x++) {
                    sumR = 0;
                    sumG = 0;
                    sumB = 0;
                    A = Color.alpha(pixels[y*width+x]);
                    for (int j = -n; j <= n; j++) {
                        for (int i = -n; i <= n; i++) {
                            float coef_mask = mask[j + n][i + n];
                            int pixel = pixels[(y + j) * width + x + i];
                            //For every RGB componants, multiplies by convolution matrix coefficient
                            sumR += coef_mask * Color.red(pixel);
                            sumG += coef_mask * Color.green(pixel);
                            sumB += coef_mask * Color.blue(pixel);
                        }
                    }

                    //If value is too low, sets to 0, if too up, sets to 255

                    R = sumR/factor;
                    if(R < 0) { R = 0; }
                    else if(R > 255) { R = 255; }

                    G = sumG/factor;
                    if(G < 0) { G = 0; }
                    else if(G > 255) { G = 255; }

                    B = sumB/factor;
                    if(B < 0) { B = 0; }
                    else if(B > 255) { B = 255; }
                    pixelsConv[y * width + x] = Color.argb(A, R, G, B);
                }
            }
            bmp.setPixels(pixelsConv, 0, width, 0, 0, width, height);
        }
    }

    public void findAreaColor(int[] src, int[] borders, int x, int y, int width, int height, int[] sumRGB){
        int index = y*width+x;
        borders[index]=1;
        int pixel = src[index];
        sumRGB[0] += Color.red(pixel);
        sumRGB[1] += Color.green(pixel);
        sumRGB[2] += Color.blue(pixel);
        sumRGB[3] ++;
        if ((x>0) && (borders[index-1]==0)){findAreaColor(src, borders, x-1, y, width, height, sumRGB);}
        if ((x<width-1)&&(borders[index+1]==0)){findAreaColor(src, borders, x+1, y, width, height, sumRGB);}
        if ((y>0)&&(borders[index-width]==0)){findAreaColor(src, borders, x, y-1, width, height, sumRGB);}
        if ((y<height-1)&&(borders[index+width]==0)){findAreaColor(src, borders, x, y+1, width, height, sumRGB);}
    }

    public void paintArea(int[] src, int[] borders, int x, int y, int width, int height, int color){
        if (borders[y*width + x]==1){
            src[y*width+x] = color;
            borders[y*width + x] = 2;
            if ((x>0)){paintArea(src, borders, x-1, y, width, height, color);}
            if (x<width-1){paintArea(src, borders, x+1, y, width, height, color);}
            if (y>0){paintArea(src, borders, x, y-1, width, height, color);}
            if (y<height-1){paintArea(src, borders, x, y+1, width, height, color);}
        }
    }

    public void cartoon(Bitmap bmp, Bitmap sobel) {
        try {
            int w = bmp.getWidth();

            int h = bmp.getHeight();
            int mapBorders[] = new int[w * h];
            Arrays.fill(mapBorders, 0);
            int mapSrc[] = new int[w * h];
            bmp.getPixels(mapSrc, 0, w, 0, 0, w, h);
            int mapSobel[] = new int[w * h];
            sobel.getPixels(mapSobel, 0, w, 0, 0, w, h);
            int pixel, red, green, blue;
            //Fills mapBorders with -1 at borders
            for (int i = 0; i < w * h; i++) {
                pixel = mapSobel[i];
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);
                if (red + blue + green > 50) {
                    mapBorders[i] = -1;
                }
            }
            int color;
            int[] sumRGB = new int[4];
            float[] pixelHSV = new float[3];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (mapBorders[y * w + x] == 0) {
                        Arrays.fill(sumRGB, 0);
                        findAreaColor(mapSrc, mapBorders, x, y, w, h, sumRGB);
                        red = sumRGB[0] / sumRGB[3];
                        green = sumRGB[1] / sumRGB[3];
                        blue = sumRGB[2] / sumRGB[3];
                        color = Color.rgb(red, green, blue);
                        paintArea(mapSrc, mapBorders, x, y, w, h, color);
                    }
                }
            }
            for (int i = 0; i < w * h; i++) {
                if (mapBorders[i] == -1) {
                    Color.colorToHSV(mapSobel[i], pixelHSV);
                    if (pixelHSV[2] > 0.5) {
                        int v = (int) (100 * (1 - pixelHSV[2]));
                        mapSrc[i] = Color.rgb(v, v, v);
                    }
                }
            }
            bmp.setPixels(mapSrc, 0, w, 0, 0, w, h);
        } catch (StackOverflowError e) {
            AlertDialog.Builder reducedialog = new AlertDialog.Builder(ConvolutionMenu.this);
            reducedialog.setTitle("Échec")
                    .setMessage("Il se peut que la qualité de l'image soit trop élevée. Voulez-vous réduire la qualité?")
                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            picture = reduce(picture);
                            imgView.setImageBitmap(picture);
                        }
                    });

            AlertDialog alert = reducedialog.create();
            alert.show();
        }
    }

    private View.OnClickListener blistener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {

                //Undoes changes by getting the original picture back
                case R.id.reset:
                    picture = pictureToUse.copy(Bitmap.Config.ARGB_8888, true);
                    imgView.setImageBitmap(picture);
                    break;

                //Applicates 'moyenne' filter
                case R.id.moy:
                    try {
                        length = Integer.valueOf(lengthText.getText().toString());

                        if (length % 2 == 0) {
                            Toast evennotodd = Toast.makeText(getApplicationContext(), "Le paramètre du filtre moyenneur doit être impair", Toast.LENGTH_LONG);
                            evennotodd.show();
                        } else {
                            moyenne(picture, length);
                            imgView.setImageBitmap(picture);
                        }
                    }
                    catch(Exception e){
                        Toast novalue = Toast.makeText(getApplicationContext(), "Aucune valeur saisie", Toast.LENGTH_LONG);
                        novalue.show();
                    }
                    break;

                //Applicates Gaussian blur
                case R.id.gauss:
                    gauss(picture);
                    imgView.setImageBitmap(picture);
                    break;

                //Applicates Sobel filter
                case R.id.sobel:
                    sobel(picture);
                    imgView.setImageBitmap(picture);
                    break;

                //Applicates Laplacien filter
                case R.id.lapla:
                    laplacien(picture);
                    imgView.setImageBitmap(picture);
                    break;

                case R.id.cartoon:
                    Bitmap sobel = picture.copy(Bitmap.Config.ARGB_8888, true);
                    sobel(sobel);
                    cartoon(picture,sobel);
                    imgView.setImageBitmap(picture);
                    break;

                //Saves image in the gallery
                case R.id.save:
                    try {
                        MediaStore.Images.Media.insertImage(getContentResolver(), picture, PhotoLoading.imgDecodableString + "_convolution", "");
                        Intent second = new Intent(ConvolutionMenu.this, PhotoLoading.class);
                        startActivity(second);
                        break;
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                default:
                    break;
            }
        }
    };
}
