package fr.iut.taquin;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by shellcode on 3/7/17.
 */

public class GameCore {

    private int grid_size;
    private static Bitmap originalBitmap; //Utilisé en static afin de pouvoir passer l'image sur laquelle se base le jeu sans uiliser un Bundle (qui ne peut pas contenir une image trop grosse)
    private Item [] items;

    //Case vide = -1
    private int [][] item_ids_win_matrix; //Position des items pour que le jeu soit terminé (et donc gagné)
    private int [][] item_ids_current; //Position des items actuelle
    private int movesCount = 0;

    public GameCore(int grid_size) {

        if(originalBitmap == null)
            throw new NullPointerException("Game image not initialized");

        this.grid_size = grid_size;
        items = new Item[grid_size * grid_size];
        Item.resetIds();
        splitImageIntoItems();

        item_ids_win_matrix = new int[grid_size][grid_size];
        item_ids_current = new int[grid_size][grid_size];

        for(int item_index = 0, i = 0; i < grid_size; i++) {
            for(int j = 0; j < grid_size; j++, item_index++) {
                item_ids_win_matrix[i][j] = items[item_index].getId();
                item_ids_current[i][j] = items[item_index].getId();
            }
        }

        Log.d("GAMECORE", "Initial state : " + Arrays.deepToString(item_ids_current));
        shuffle();
    }

    public static void setGameImage(Bitmap image) {
        originalBitmap = image;
    }

    public static Bitmap getGameImage() {
        return originalBitmap;
    }

    private void splitImageIntoItems() {

        int sub_image_size = originalBitmap.getWidth() / grid_size;
        int item_index = 0;

        for(int i = 0; i < grid_size; i++) {
            for(int j = 0; j < grid_size; j++, item_index++) {
                Bitmap subImage = Bitmap.createBitmap(originalBitmap, j * sub_image_size, i * sub_image_size, sub_image_size, sub_image_size);

                items[item_index] = new Item(subImage);

                if(j == grid_size-1 && i == grid_size-1) {
                    subImage.eraseColor(Color.TRANSPARENT); //On met l'image transparente
                    items[item_index].setAsHoleInGrid();
                }
            }
        }
    }

    public int getSubImageSize() {
        return originalBitmap.getWidth() / grid_size;
    }

    public int getGridSize() {
        return grid_size;
    }

    public Item[] getItems() {
        for(int i = 0; i < items.length; i++)
            if(items[i] == null)
                Log.d("GAMECORE", "Item already null here...");
        return items;
    }

    //retourne true si le coup est joué, sinon false si le coup est impossible
    public boolean play(long id) {

        if(id == -1)
            return false;

        //cordonnées de l'item cliqué
        int i_clicked = -1, j_clicked = -1, i_empty_cell = -1, j_empty_cell = -1;

        for(int i = 0; i < item_ids_current.length && (i_clicked == -1 || i_empty_cell == -1); i++) {
            for(int j = 0; j < item_ids_current[i].length; j++) {
                if(item_ids_current[i][j] == id) {
                    i_clicked = i;
                    j_clicked = j;
                    //break;
                }

                else if(item_ids_current[i][j] == -1) {
                    i_empty_cell = i;
                    j_empty_cell = j;
                }
            }
        }

        boolean moveOnLine = i_clicked == i_empty_cell;
        boolean moveOnColumn = j_clicked == j_empty_cell;

        if(moveOnLine) {
            Log.d("GAMECORE", "can move on line");
            if(j_clicked - j_empty_cell > 0) {

                for(int index = j_empty_cell; index < j_clicked; index++) {
                    item_ids_current[i_clicked][index] = item_ids_current[i_clicked][index+1];
                }
            }

            else {
                for(int index = j_empty_cell; index > j_clicked; index--) {
                    item_ids_current[i_clicked][index] = item_ids_current[i_clicked][index-1];
                }
            }
        }

        else if(moveOnColumn) {
            Log.d("GAMECORE", "can move on column");
            if(i_clicked - i_empty_cell > 0) {

                for(int index = i_empty_cell; index < i_clicked; index++) {
                    item_ids_current[index][j_clicked] = item_ids_current[index+1][j_clicked];
                }
            }

            else {
                for(int index = i_empty_cell; index > i_clicked; index--) {
                    item_ids_current[index][j_clicked] = item_ids_current[index-1][j_clicked];
                }
            }
        }

        if(moveOnLine || moveOnColumn) {
            item_ids_current[i_clicked][j_clicked] = -1;
            movesCount++;
        }

        return moveOnLine || moveOnColumn;
    }

    public int[][] getActualState() {
        Log.d("GAMECORE", "Actual state : " + Arrays.deepToString(item_ids_current));
        return item_ids_current;
    }

    public int[][] getWinMatrix() {
        return item_ids_win_matrix;
    }

    public void shuffle() {

        Random random = new Random();

        int i_empty_cell = -1;
        int j_empty_cell = -1;

        for(int i = 0; i < grid_size && i_empty_cell == -1; i++)
            for(int j = 0; j < grid_size; j++)
                if(item_ids_current[i][j] == -1) {
                    i_empty_cell = i;
                    j_empty_cell = j;
                    break;
                }

        int new_i = i_empty_cell, new_j = j_empty_cell;

        //On fait grid_size*grid_size déplacements aléatoire afin de mélanger !
        for(int i = 0; i < Math.pow(grid_size, 4); i++) {

            //4 déplacements possibles
            switch(random.nextInt(4)) {
                case 0: if(i_empty_cell-1 >= 0) { new_i--; break;}
                case 1: if(i_empty_cell+1 < grid_size) { new_i++; break;}
                case 2: if(j_empty_cell-1 >= 0) { new_j--; break;}
                case 3: if(j_empty_cell+1 < grid_size) { new_j++; break;}
            }

            int test = item_ids_current[new_i][new_j];
            item_ids_current[i_empty_cell][j_empty_cell] = test;
            item_ids_current[new_i][new_j] = -1;
            i_empty_cell = new_i;
            j_empty_cell = new_j;
        }

        //On remet la case vide en bas à droite
        for(; j_empty_cell < grid_size-1; j_empty_cell++) {
            item_ids_current[i_empty_cell][j_empty_cell] = item_ids_current[i_empty_cell][j_empty_cell+1];
        }

        for(; i_empty_cell < grid_size-1; i_empty_cell++) {
            item_ids_current[i_empty_cell][j_empty_cell] = item_ids_current[i_empty_cell+1][j_empty_cell];
        }

        item_ids_current[grid_size-1][grid_size-1] = -1;

        //Si le mélange abouti à une partie déjà gagnée, on relance le mélange
        if(isGameWon())
            shuffle();

        else
            Log.d("GAMECORE", "Shuffled : " + Arrays.deepToString(item_ids_current) + "\n");
    }

    public boolean isGameWon() {
        boolean equal = true;

        for(int i = 0; i < grid_size && equal; i++)
            for(int j = 0; j < grid_size && equal; j++)
                if(item_ids_current[i][j] != item_ids_win_matrix[i][j])
                    equal = false;

        return equal;
    }

    public int getMovesCount() {
        return movesCount;
    }
}
