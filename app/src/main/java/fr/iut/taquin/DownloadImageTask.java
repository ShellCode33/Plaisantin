package fr.iut.taquin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

/**
 * Created by shellcode on 3/6/17.
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ViewPager viewPager;
    MainActivity mainActivity;
    ImageView randomImageView;

    public DownloadImageTask(final ViewPager viewPager, ImageView randomImageView) {
        this.viewPager = viewPager;
        mainActivity = (MainActivity) viewPager.getContext();
        this.randomImageView = randomImageView;
    }

    protected Bitmap doInBackground(String... urls) {

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mainActivity, mainActivity.getResources().getString(R.string.loading), Toast.LENGTH_SHORT).show();
            }
        });

        String urldisplay = urls[0];
        Bitmap image = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            image = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        return image;
    }

    protected void onPostExecute(final Bitmap result) {
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (result != null) {
                    randomImageView.setImageBitmap(result);
                    viewPager.setAdapter(new ImagePagerAdapter(mainActivity.getApplicationContext(), new ImageView[]{randomImageView}));
                    Toast.makeText(mainActivity, R.string.info_toast1, Toast.LENGTH_SHORT).show();
                    mainActivity.hideArrows();
                    Log.d("DOWNLOAD", "Image downloaded ;)");
                } else {
                    Toast.makeText(mainActivity, mainActivity.getResources().getString(R.string.download_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}