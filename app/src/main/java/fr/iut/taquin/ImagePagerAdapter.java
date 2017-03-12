package fr.iut.taquin;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by shellcode on 3/6/17.
 */

public class ImagePagerAdapter extends PagerAdapter {
    Context mContext;
    private int [] image_res;
    private ImageView [] images;
    private ImageView currentImage;

    public ImagePagerAdapter(Context context, int [] images_res) {
        this.mContext = context;
        this.image_res = images_res;
    }

    public ImagePagerAdapter(Context context, ImageView [] images) {
        this.mContext = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        if(images != null)
            return images.length;

        return image_res.length;
    }

    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return v == obj;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        ImageView image;

        if(images != null)
            image = images[i];

        else {
            image = new ImageView(mContext);
            image.setImageResource(image_res[i]);
        }

        image.setTag("default_image_view_" + i);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        container.addView(image, 0);
        return image;
    }

    @Override
    public void destroyItem(ViewGroup container, int i, Object obj) {
        container.removeView((ImageView) obj);
    }
}