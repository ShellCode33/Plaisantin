package fr.iut.taquin;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by shellcode on 3/7/17.
 */

public class Item {
    private static int nb_items;
    private int id;
    private Bitmap image;

    public Item(Bitmap image) {
        id = nb_items++;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public void setAsHoleInGrid() {
        id = -1;
    }

    public static void resetIds() {
        nb_items = 0;
    }
}
