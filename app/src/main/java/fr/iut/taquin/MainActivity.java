package fr.iut.taquin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_INTERNET = 42;
    private static final int REQUEST_PICTURES = 1337;

    private View mainLayout;
    private ImagePagerAdapter adapterViewDefault;
    private ViewPager imagesViewPager;
    private ImageView randomImageView, customImageView;

    private ImageView selectedImageView;

    ToggleButton [] sizeButtons = new ToggleButton[5];
    int gridSize = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.main_layout);
        sizeButtons[0] = (ToggleButton) mainLayout.findViewById(R.id.gridSizeButton2);
        sizeButtons[1] = (ToggleButton) mainLayout.findViewById(R.id.gridSizeButton3);
        sizeButtons[2] = (ToggleButton) mainLayout.findViewById(R.id.gridSizeButton4);
        sizeButtons[3] = (ToggleButton) mainLayout.findViewById(R.id.gridSizeButton5);
        sizeButtons[4] = (ToggleButton) mainLayout.findViewById(R.id.gridSizeButton6);

        //Lorsque qu'on clique sur un bouton, tous les autres sont déselectionnés
        for(final ToggleButton toggleButton : sizeButtons) {
            toggleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(ToggleButton button : sizeButtons)
                        button.setChecked(false);

                    toggleButton.setChecked(true);
                    gridSize = Integer.parseInt(""+toggleButton.getText().charAt(0));
                    Log.d("GRID_SIZE", "gridSize = " + gridSize);
                }
            });
        }

        imagesViewPager = (ViewPager) findViewById(R.id.viewPageDefaultImages);
        adapterViewDefault = new ImagePagerAdapter(this, new int[] {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5, R.drawable.image6});
        imagesViewPager.setAdapter(adapterViewDefault);

        final Button randomButton = (Button) findViewById(R.id.randomButton);
        Button browseButton = (Button) findViewById(R.id.browseButton);

        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomImageView = new ImageView(getApplicationContext());
                selectedImageView = randomImageView;

                randomImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imagesViewPager.setAdapter(adapterViewDefault);
                        randomImageView = null;
                    }
                });

                askInternetPermissionForRandomPictures();
            }
        });

        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customImageView = new ImageView(getApplicationContext());
                selectedImageView = customImageView;

                customImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imagesViewPager.setAdapter(adapterViewDefault);
                        customImageView = null;
                    }
                });

                askWritePermissionForCustomPictures();
            }
        });

        Button buttonPlay = (Button) findViewById(R.id.buttonPlay);
        Button buttonAbout = (Button) findViewById(R.id.buttonAbout);

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent gameIntent = new Intent(getApplicationContext(), GameActivity.class);

            ImageView imageToPass = ((ImagePagerAdapter)imagesViewPager.getAdapter()).getCurrentImage();
            imageToPass.buildDrawingCache();
            Bitmap bitmap = imageToPass.getDrawingCache();
            GameCore.setGameImage(bitmap); //Bypass Bundle size limitations

            gameIntent.putExtra("grid_size", gridSize);

            Log.d("MAIN", "Starting game !");
            startActivity(gameIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableFullScreen();
    }

    private void enableFullScreen() {

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null)
            actionBar.hide();

        mainLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void switchAdapterToRandom() {
        DownloadImageTask task = new DownloadImageTask(randomImageView);
        task.execute("http://lorempixel.com/500/500/");
        imagesViewPager.setAdapter(new ImagePagerAdapter(getApplicationContext(), new ImageView[] {randomImageView}));
        Toast.makeText(getApplicationContext(), R.string.info_toast1, Toast.LENGTH_SHORT).show();
    }

    private void switchAdapterToCustom() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);
    }

    private void askInternetPermissionForRandomPictures() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET);
        }

        else {
            Log.d("PERMISSION", "Already have the permission go !");
            switchAdapterToRandom();
        }
    }

    private void askWritePermissionForCustomPictures() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PICTURES);
        }

        else {
            Log.d("PERMISSION", "Already have the permission go !");
            switchAdapterToCustom();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_INTERNET: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    switchAdapterToRandom();
                }

                break;
            }

            case REQUEST_PICTURES: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    switchAdapterToCustom();
                }

                break;
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();

                    Bitmap bitmap = null;

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //crop picture to be a square
                    if(bitmap.getWidth() < bitmap.getHeight())
                        bitmap = Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight()-bitmap.getWidth()) / 2, bitmap.getWidth(), bitmap.getWidth());
                    else
                        bitmap = Bitmap.createBitmap(bitmap, (bitmap.getWidth()-bitmap.getHeight()) / 2, 0, bitmap.getHeight(), bitmap.getHeight());

                    customImageView.setImageBitmap(bitmap);
                    imagesViewPager.setAdapter(new ImagePagerAdapter(this, new ImageView[] {customImageView}));
                    Toast.makeText(getApplicationContext(), R.string.info_toast1, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
