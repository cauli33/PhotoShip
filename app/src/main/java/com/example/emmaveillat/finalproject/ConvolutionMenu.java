package com.example.emmaveillat.finalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
    EditText facteurLongueur;

    /**
     * Buttons to apply functions, to save or reset a bitmap
     */
    Button save, reset, moy, sobel, gauss, lapla;

    /**
     * The bitmap displayed in the menu
     */
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convolution_menu);

        // Creates textzone to choose matrix length for moyenne
        facteurLongueur = (EditText) findViewById(R.id.newLength);
        facteurLongueur.setHint("Notez ici la taille de la matrice");

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
        int[][] Gx = sobelConvolutionAux(bmp,hx);

        int[][] hy = {{-1,-2,-1},{0,0,0},{1,2,1}};
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
                    length = Integer.valueOf(facteurLongueur.getText().toString());
                    moyenne(picture, length);
                    imgView.setImageBitmap(picture);
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
