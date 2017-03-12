package fr.iut.taquin;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Created by shellcode on 3/7/17.
 */

public class ItemAdapter extends BaseAdapter {

    private Context context;
    private Item [] items;

    public ItemAdapter(Context context, Item[] items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return items[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView picturesView;

        if(convertView == null)
            picturesView = new ImageView(context);
        else
            picturesView = (ImageView) convertView;

        picturesView.setImageBitmap(items[position].getImage());

        if(items[position].getId() == -1)
            picturesView.setVisibility(View.INVISIBLE);
        else
            picturesView.setVisibility(View.VISIBLE);

        return picturesView;
    }

    public void setNewPositions(int [][] item_ids_current) {

        int [] actual_ids_order = new int[item_ids_current.length * item_ids_current.length];

        for(int i = 0, index = 0; i < item_ids_current.length; i++) {
            for(int j = 0; j < item_ids_current[i].length; j++, index++) {
                actual_ids_order[index] = item_ids_current[i][j];
            }
        }

        Item [] newPositions = new Item[items.length];

        for(int i = 0; i < actual_ids_order.length; i++) {
            Item item_found = null;

            for(Item item : items) {
                if (item.getId() == actual_ids_order[i]) {
                    item_found = item;
                    break;
                }
            }

            newPositions[i] = item_found;
        }

        items = newPositions;

        Log.d("ADAPTER", "ids order : " + Arrays.toString(actual_ids_order));
    }
}
