package com.example.emmaveillat.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import static com.example.emmaveillat.finalproject.R.id.imgView;

public class PhotoLoading extends Activity {

    private static int RESULT_LOAD_IMG = 1;

    static String imgDecodableString;

    Button choice;

    public Bitmap pictureToUse;

    public ImageView imageView;

    private static final int CAMERA_REQUEST = 1888;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_loading);

        choice = (Button) findViewById(R.id.choice);
        choice.setOnClickListener(blistener);

        //Starts camera
        Button photoButton = (Button) this.findViewById(R.id.button1);
        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    public void chargementImage(View view) {
        Intent galerie = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galerie, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                pictureToUse = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                imageView = (ImageView) findViewById(imgView);
                imageView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));


            }

            else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && null != data) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imgDecodableString = "newPhoto" + Math.random();
                MediaStore.Images.Media.insertImage(getContentResolver(), photo, imgDecodableString, "photo from camera");

                chargementImage(imageView);

            } else {
                Toast.makeText(this, "Vous n'avez pas choisi d'image", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Quelque chose a mal fonctionné, veuillez réessayer.", Toast.LENGTH_LONG).show();
        }

    }


    protected static Bitmap scaleImage() {
        Bitmap nad = BitmapFactory.decodeFile(imgDecodableString);
        return nad;
    }


    private View.OnClickListener blistener = new View.OnClickListener(){
        public void onClick(View v){
            switch (v.getId()) {
                // If a picture has been chosen in the gallery, opens general menu
                case R.id.choice :
                    if (pictureToUse != null){
                        Intent second = new Intent(PhotoLoading.this, GeneralMenu.class);
                        startActivity(second);}
                    break;

                default:
                    break;
            }
        }
    };
}

