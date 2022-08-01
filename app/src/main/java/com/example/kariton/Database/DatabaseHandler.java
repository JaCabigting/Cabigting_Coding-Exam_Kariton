package com.example.kariton.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.example.kariton.Models.CartItem;
import com.example.kariton.Models.FavoriteItem;
import com.example.kariton.Models.Product;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "kariton_db";
    private static final int DB_VERSION = 1;
    private static final String CART_TABLE_NAME = "cartProducts";
    private static final String FAVORITE_TABLE_NAME = "favoriteProducts";
    private static final String NAME_COL = "name";
    private static final String PERCENTAGE_COL = "percentOff";
    private static final String ORIGINAL_PRICE_COL = "originalPrice";
    private static final String SALE_PRICE_COL = "salePrice";
    private static final String DESCRIPTION_COL = "description";
    private static final String SIZE_COL = "size";
    private static final String  BITMAP_COL = "bitmap";
    private static final String QUANTITY_COL = "quantity";

    // Constructor for the Database Handler
    public DatabaseHandler(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Creating the tables to be used by the application
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Query for creating the table for the user's cart
        String createCartTableQuery = "CREATE TABLE " + CART_TABLE_NAME + " ("
                + NAME_COL + " TEXT,"
                + ORIGINAL_PRICE_COL + " TEXT,"
                + SALE_PRICE_COL + " TEXT, "
                + PERCENTAGE_COL + " TEXT,"
                + DESCRIPTION_COL + " TEXT,"
                + SIZE_COL + " TEXT,"
                + QUANTITY_COL + " INTEGER,"
                + BITMAP_COL + " TEXT)";

        // Query for creating the table for the user's favorites
        String createFavoriteTable = "CREATE TABLE " + FAVORITE_TABLE_NAME + " ("
                + NAME_COL + " TEXT,"
                + ORIGINAL_PRICE_COL + " TEXT,"
                + SALE_PRICE_COL + " TEXT, "
                + PERCENTAGE_COL + " TEXT,"
                + DESCRIPTION_COL + " TEXT,"
                + BITMAP_COL + " TEXT)";

        sqLiteDatabase.execSQL(createCartTableQuery);
        sqLiteDatabase.execSQL(createFavoriteTable);
    }

    // Gets the list of items currently in the user's cart
    public ArrayList<CartItem> viewCart(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + CART_TABLE_NAME, null);

        ArrayList<CartItem> cart = new ArrayList<CartItem>();

        if(cursor.moveToFirst()){
            do{
                cart.add(new CartItem(cursor.getString(0),
                        cursor.getString(4),
                        cursor.getString(2),
                        cursor.getString(1),
                        cursor.getString(3),
                        cursor.getString(5),
                        cursor.getString(8),
                        cursor.getInt(7)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return cart;
    }

    // Gets the list of items currently in the user's favorites
    public  ArrayList<FavoriteItem> viewFavorites(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + FAVORITE_TABLE_NAME, null);

        ArrayList<FavoriteItem> favorites = new ArrayList<FavoriteItem>();

        if(cursor.moveToFirst()){
            do{
                favorites.add(new FavoriteItem(cursor.getString(0),
                        cursor.getString(4),
                        cursor.getString(2),
                        cursor.getString(1),
                        cursor.getString(3),
                        cursor.getString(7)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return favorites;
    }

    // Function that adds an item to the cart
    public void addProductToCart(Product product, String size, Bitmap bitmap){
        SQLiteDatabase sqLiteDatabaseWriter = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        // Checks if the item exists in the cart already
        if(getItemCount(product, size) == 0){ // If does not exist, create a new entry
            values.put(NAME_COL, product.getName());
            values.put(ORIGINAL_PRICE_COL, product.getOriginalPrice());
            values.put(SALE_PRICE_COL, product.getSalePrice());
            values.put(PERCENTAGE_COL, product.getPercentOff());
            values.put(DESCRIPTION_COL, product.getDescription());
            values.put(QUANTITY_COL, 1);
            values.put(SIZE_COL, size);

            // Convert bitmap to string
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] bytes = outputStream.toByteArray();
            String byteString = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
            values.put(BITMAP_COL, byteString);

            sqLiteDatabaseWriter.insert(CART_TABLE_NAME, null, values);

        } else { // Update entry by adding 1 to the quantity
            sqLiteDatabaseWriter.execSQL("UPDATE " + CART_TABLE_NAME + " SET " + QUANTITY_COL + "=" + QUANTITY_COL + "+1" + " WHERE name = ? AND percentOff = ? AND originalPrice = ? AND salePrice = ? AND description = ? AND size = ?", new String[]{product.getName(), product.getPercentOff(), product.getOriginalPrice(), product.getSalePrice(), product.getDescription(), size});
        }
    }

    // Adds a product to favorites
    public void addProductToFavorite(Product product, Bitmap bitmap){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(NAME_COL, product.getName());
        values.put(ORIGINAL_PRICE_COL, product.getOriginalPrice());
        values.put(SALE_PRICE_COL, product.getSalePrice());
        values.put(PERCENTAGE_COL, product.getPercentOff());
        values.put(DESCRIPTION_COL, product.getDescription());

        // Convert bitmap to string
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] bytes = outputStream.toByteArray();
        String byteString = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
        values.put(BITMAP_COL, byteString);

        sqLiteDatabase.insert(FAVORITE_TABLE_NAME, null, values);

    }

    // Adding an item found in favorites to the user's cart
    public void addFavoriteToCart(FavoriteItem item, String size, Bitmap bitmap){
        SQLiteDatabase sqLiteDatabaseWriter = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        // Checks if the item exists in the cart already
        if(getFavoriteItemInCartCount(item, size) == 0){
            values.put(NAME_COL, item.getName());
            values.put(ORIGINAL_PRICE_COL, item.getOriginalPrice());
            values.put(SALE_PRICE_COL, item.getSalePrice());
            values.put(PERCENTAGE_COL, item.getPercentOff());
            values.put(DESCRIPTION_COL, item.getDescription());
            values.put(QUANTITY_COL, 1);
            values.put(SIZE_COL, size);

            // Convert bitmap to string
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] bytes = outputStream.toByteArray();
            String byteString = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
            values.put(BITMAP_COL, byteString);

            sqLiteDatabaseWriter.insert(CART_TABLE_NAME, null, values);
        } else {
            sqLiteDatabaseWriter.execSQL("UPDATE " + CART_TABLE_NAME + " SET " + QUANTITY_COL + "=" + QUANTITY_COL + "+1" + " WHERE name = ? AND percentOff = ? AND originalPrice = ? AND salePrice = ? AND description = ? AND size = ?", new String[]{item.getName(), item.getPercentOff(), item.getOriginalPrice(), item.getSalePrice(), item.getDescription(), size});
        }
    }

    // Function that adds a quantity to the user's cart if a product page is open from the Cart Activity
    public void addItemToCart(CartItem item, String size, Bitmap bitmap){
        SQLiteDatabase sqLiteDatabaseWriter = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        // Checks if the item exists in the cart already
        if(getItemInCartCount(item, size) == 0){
            values.put(NAME_COL, item.getName());
            values.put(ORIGINAL_PRICE_COL, item.getOriginalPrice());
            values.put(SALE_PRICE_COL, item.getSalePrice());
            values.put(PERCENTAGE_COL, item.getPercentOff());
            values.put(DESCRIPTION_COL, item.getDescription());
            values.put(QUANTITY_COL, 1);
            values.put(SIZE_COL, size);

            // Convert bitmap to string
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] bytes = outputStream.toByteArray();
            String byteString = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
            values.put(BITMAP_COL, byteString);

            sqLiteDatabaseWriter.insert(CART_TABLE_NAME, null, values);
        } else {
            sqLiteDatabaseWriter.execSQL("UPDATE " + CART_TABLE_NAME + " SET " + QUANTITY_COL + "=" + QUANTITY_COL + "+1" + " WHERE name = ? AND percentOff = ? AND originalPrice = ? AND salePrice = ? AND description = ? AND size = ?", new String[]{item.getName(), item.getPercentOff(), item.getOriginalPrice(), item.getSalePrice(), item.getDescription(), size});
        }
    }

    // Counts the item of product in the cart based on a specific size
    private long getItemCount(Product product, String size){
        SQLiteDatabase sqLiteDatabaseReader = this.getReadableDatabase();
        long itemCount = DatabaseUtils.queryNumEntries(sqLiteDatabaseReader, CART_TABLE_NAME, "name = ? AND percentOff = ? AND originalPrice = ? AND salePrice = ? AND description = ? AND size = ?", new String[]{product.getName(), product.getPercentOff(), product.getOriginalPrice(), product.getSalePrice(), product.getDescription(), size});

        return itemCount;
    }

    // Counts the number of items in the cart under the CartItem type
    private long getItemInCartCount(CartItem item, String size){
        SQLiteDatabase sqLiteDatabaseReader = this.getReadableDatabase();
        long itemCount = DatabaseUtils.queryNumEntries(sqLiteDatabaseReader, CART_TABLE_NAME, "name = ? AND percentOff = ? AND originalPrice = ? AND salePrice = ? AND description = ? AND size = ?", new String[]{item.getName(), item.getPercentOff(), item.getOriginalPrice(), item.getSalePrice(), item.getDescription(), size});

        return itemCount;
    }


    // Counts the number of items found in user's favorites that is also in the user's cart
    private long getFavoriteItemInCartCount(FavoriteItem item, String size){
        SQLiteDatabase sqLiteDatabaseReader = this.getReadableDatabase();
        long itemCount = DatabaseUtils.queryNumEntries(sqLiteDatabaseReader, CART_TABLE_NAME, "name = ? AND percentOff = ? AND originalPrice = ? AND salePrice = ? AND description = ? AND size = ?", new String[]{item.getName(), item.getPercentOff(), item.getOriginalPrice(), item.getSalePrice(), item.getDescription(), size});

        return itemCount;
    }


    // Deletes an item from the cart
    public void deleteItemFromCart(CartItem item){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(CART_TABLE_NAME, "name = ? AND percentOff = ? AND originalPrice = ? AND salePrice = ? AND description = ? AND size = ? AND bitmap = ?", new String[]{item.getName(), item.getPercentOff(), item.getOriginalPrice(), item.getSalePrice(),  item.getDescription(), item.getSize(), item.getBitmap()});
    }

    // Deletes favorite item from favorites
    public void deleteItemFromFavorite(FavoriteItem favoriteItem){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(FAVORITE_TABLE_NAME, "name = ? AND percentOff = ? AND originalPrice = ? AND salePrice = ? AND description = ? AND bitmap = ?", new String[]{favoriteItem.getName(), favoriteItem.getPercentOff(), favoriteItem.getOriginalPrice(), favoriteItem.getSalePrice(),  favoriteItem.getDescription(), favoriteItem.getBitmap()});
    }


    // Deletes a product from favorites
    public void deleteProductFromFavorite(Product product, Bitmap bitmap){
        // Convert bitmap to string
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] bytes = outputStream.toByteArray();
        String byteString = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);


        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(FAVORITE_TABLE_NAME, "name = ? AND percentOff = ? AND originalPrice = ? AND salePrice = ? AND description = ? AND bitmap = ?", new String[]{product.getName(), product.getPercentOff(), product.getOriginalPrice(), product.getSalePrice(), product.getDescription(), byteString});
    }


    // Increasing the quantity of an item from the Cart Activity itself
    public void increaseQuantity(CartItem product, String size){
        SQLiteDatabase sqLiteDatabaseWriter = this.getWritableDatabase();
        sqLiteDatabaseWriter.execSQL("UPDATE " + CART_TABLE_NAME + " SET " + QUANTITY_COL + "=" + QUANTITY_COL + "+1" + " WHERE name = ? AND percentOff = ? AND originalPrice = ? AND salePrice = ? AND description = ? AND size = ?", new String[]{product.getName(), product.getPercentOff(), product.getOriginalPrice(), product.getSalePrice(), product.getDescription(), size});
    }

    // Decreases the quantity of an item from the Cart Activity itself
    public void decreaseQuantity(CartItem product, String size){
        SQLiteDatabase sqLiteDatabaseWriter = this.getWritableDatabase();
        sqLiteDatabaseWriter.execSQL("UPDATE " + CART_TABLE_NAME + " SET " + QUANTITY_COL + "=" + QUANTITY_COL + "-1" + " WHERE name = ? AND percentOff = ? AND originalPrice = ? AND salePrice = ? AND description = ? AND size = ?", new String[]{product.getName(), product.getPercentOff(), product.getOriginalPrice(), product.getSalePrice(), product.getDescription(), size});
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CART_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FAVORITE_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
