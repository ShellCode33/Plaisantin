package fr.iut.taquin;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.Arrays;

/**
 * Created by shellcode on 3/7/17.
 */

public class ItemAdapter extends BaseAdapter {

    private Context context;
    private Item [] items;
    private ImageView [] itemImages;
    private int [] actual_ids_order;
    private int empty_item_position;

    public ItemAdapter(Context context, Item[] items) {
        this.context = context;

        this.items = items;


        itemImages = new ImageView[items.length];
        actual_ids_order = new int[items.length];

        //Transform bitmaps in imageviews
        for(int i = 0; i < items.length; i++) {

            itemImages[i] = new ImageView(context);

            if(items[i].getImage() == null) {
                actual_ids_order[i] = -1;
                empty_item_position = i;
            }

            else {
                actual_ids_order[i] = items[i].getId();
                itemImages[i].setImageBitmap(items[i].getImage());
            }
        }

        Log.d("ADAPTER", "ids order : " + Arrays.toString(actual_ids_order));
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        int index = actual_ids_order[position];

        if(index != -1)
            return items[index];

        Log.d("ADAPTER", "Asking empty item at : " + empty_item_position);
        return items[empty_item_position];
    }

    @Override
    public long getItemId(int position) {
        int index = actual_ids_order[position];

        if(index == -1) {
            Log.d("ADAPTER", "Asking empty item id at : " + empty_item_position);
            return items[empty_item_position].getId();
        }


        return items[index].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int index = actual_ids_order[position];

        if(index == -1) {
            return itemImages[position]; //Empty image
        }

        return itemImages[index];
    }

    public void setNewPositions(int [][] item_ids_current) {

        for(int i = 0, index = 0; i < item_ids_current.length; i++) {
            for(int j = 0; j < item_ids_current[i].length; j++, index++) {
                actual_ids_order[index] = item_ids_current[i][j];

                if(actual_ids_order[index] == -1)
                    empty_item_position = index;
            }
        }

        Log.d("ADAPTER", "ids order : " + Arrays.toString(actual_ids_order));
        notifyDataSetChanged();
    }
}
