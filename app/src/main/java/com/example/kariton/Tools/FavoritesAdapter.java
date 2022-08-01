package com.example.kariton.Tools;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.kariton.Activities.FavoritesActivity;
import com.example.kariton.Activities.ShopActivity;
import com.example.kariton.Database.DatabaseHandler;
import com.example.kariton.Models.FavoriteItem;
import com.example.kariton.Models.Product;
import com.example.kariton.Models.Size;
import com.example.kariton.R;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class FavoritesAdapter extends ArrayAdapter<FavoriteItem> {

    Context context;
    ArrayList<FavoriteItem> items;
    DatabaseHandler handler;

    // Constructor for the adapter
    public FavoritesAdapter(@NonNull Context context, ArrayList<FavoriteItem> items){
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    // Gets the number of items in the user's favorites
    @Override
    public int getCount() {
        return items.size();
    }


    // Inflating the view to be seen at the grid view
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int itemIndex = position;

        // Calling the database handler
        handler = new DatabaseHandler(context);

        // Inflating the view
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_item_thumbnail_layout, parent, false);

        ImageView iv_productImage = convertView.findViewById(R.id.iv_productImage);
        ImageButton ib_btnFavorite = convertView.findViewById(R.id.ib_btnFavorite);
        TextView tv_discount = convertView.findViewById(R.id.tv_discount);
        TextView tv_productName = convertView.findViewById(R.id.tv_productName);
        TextView tv_salePrice = convertView.findViewById(R.id.tv_salePrice);
        TextView tv_originalPrice = convertView.findViewById(R.id.tv_originalPrice);

        // Assigning the tag for the item signifying it is stored in the user's favorites
        ib_btnFavorite.setTag("R.drawable.ic_baseline_favorite_filled");
        ib_btnFavorite.setImageResource(R.drawable.ic_baseline_favorite_filled);

        // Setting the image bitmap
        String bitmapString = getItem(position).getBitmap();
        byte[] decodeStream = Base64.decode(bitmapString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodeStream, 0, decodeStream.length);

        // Setting the product's image bitmap to the image view
        iv_productImage.setImageBitmap(bitmap);

        // Setting the item's available sizes
        JSONObject xs = new JSONObject();
        JSONObject s = new JSONObject();
        JSONObject m = new JSONObject();
        JSONObject l = new JSONObject();
        JSONObject xl = new JSONObject();

        try {
            xs.put("isAvailable", false);
            xs.put("label", "XS");

            s.put("isAvailable", true);
            s.put("label", "S");

            m.put("isAvailable", true);
            m.put("label", "M");

            l.put("isAvailable", false);
            l.put("label", "L");

            xl.put("isAvailable", true);
            xl.put("label", "XL");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Creating an array holding the created JSON Objects
        JSONObject[] sizes = new JSONObject[]{xs, s, m, l, xl};
        String description = getItem(position).getDescription();

        // Setting up the Text Views
        tv_discount.setText(getItem(position).getPercentOff());
        tv_productName.setText(getItem(position).getName());
        tv_salePrice.setText(getItem(position).getSalePrice());
        tv_originalPrice.setText(getItem(position).getOriginalPrice());
        tv_originalPrice.setPaintFlags(tv_originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // Constructing a FavoriteItem object to be used in opening its product info
        FavoriteItem favoriteItem = new FavoriteItem(tv_productName.getText().toString(), description, tv_salePrice.getText().toString(), tv_originalPrice.getText().toString(), tv_discount.getText().toString(), bitmapString);
        favoriteItem.setSizes(sizes);
        favoriteItem.setIndex(position);

        // Opening the item page when the view is clicked
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openItemPage(context, favoriteItem, ((BitmapDrawable)iv_productImage.getDrawable()).getBitmap());
            }
        });

        // Action after the "Favorite" button is pressed
        ib_btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnFavoriteAction(favoriteItem);
            }
        });

        // Returns the view
        return convertView;
    }

    // If button is pressed, this deletes the item from the favorites
    private void btnFavoriteAction(FavoriteItem item){
        handler.deleteItemFromFavorite(item);
        Toast.makeText(context, item.getName() + " removed from wishlist.", Toast.LENGTH_SHORT).show();

    }

    // Opening the favorite's page for user to view it's information and select the size to be added to the cart
    private void openItemPage(Context context, FavoriteItem item, Bitmap bitmap){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View productPageView = inflater.inflate(R.layout.activity_product_layout, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(productPageView, width, height, focusable);

        popupWindow.showAtLocation(((FavoritesActivity) context).findViewById(R.id.favoritesActivity_DrawerLayout), Gravity.CENTER, 0, 0);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        final String[] selectedSize = {new String()};

        ImageView iv_productImage = productPageView.findViewById(R.id.iv_productImage);
        TextView tv_discount = productPageView.findViewById(R.id.tv_discount);
        TextView tv_productName = productPageView.findViewById(R.id.tv_productName);
        TextView tv_salePrice = productPageView.findViewById(R.id.tv_salePrice);
        TextView tv_originalPrice = productPageView.findViewById(R.id.tv_originalPrice);
        TextView tv_description = productPageView.findViewById(R.id.tv_description);

        // Setting up the button for the item's sizes
        MaterialButton btnXS = productPageView.findViewById(R.id.btnXS);
        MaterialButton btnS = productPageView.findViewById(R.id.btnS);
        MaterialButton btnM = productPageView.findViewById(R.id.btnM);
        MaterialButton btnL = productPageView.findViewById(R.id.btnL);
        MaterialButton btnXL = productPageView.findViewById(R.id.btnXL);

        ArrayList<MaterialButton> btnArray = new ArrayList<MaterialButton>();
        btnArray.add(btnXS);
        btnArray.add(btnS);
        btnArray.add(btnM);
        btnArray.add(btnL);
        btnArray.add(btnXL);

        Button btnAddToCart = productPageView.findViewById(R.id.btnAddToCart);

        for(int i = 0; i < btnArray.size(); i++){
            try {
                if(item.getSizes()[i].getBoolean("isAvailable") == true){ // If the specific size is available, it enables the button
                    int index = i;
                    enableButton(btnArray.get(i));

                    btnArray.get(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setSelectedSize(btnArray, index);
                            selectedSize[0] = btnArray.get(index).getText().toString();
                            btnAddToCart.setEnabled(true);
                            btnAddToCart.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.project_teal));
                        }
                    });
                } else { // If the specific size is unavailable, it disables the button
                    disableButton(btnArray.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        // Programming the "Add to Cart" button
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handler.addFavoriteToCart(item, selectedSize[0], bitmap);
                Log.e("success", item.getName() + " with size " + selectedSize[0].toString() + " was added to cart.");
                Toast.makeText(context, item.getName() + " with size " + selectedSize[0].toString() + " was added to cart.", Toast.LENGTH_SHORT).show();

            }
        });

        // Setting the product's image bitmap to the image view
        iv_productImage.setImageBitmap(bitmap);

        // Setting up the Text Views
        tv_discount.setText("-" + item.getPercentOff() + "%");
        tv_productName.setText(item.getName());
        tv_salePrice.setText(item.getSalePrice());
        tv_originalPrice.setText(item.getOriginalPrice());
        tv_originalPrice.setPaintFlags(tv_originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tv_description.setText(item.getDescription());

        // Programming the back button
        ImageView btnBack = productPageView.findViewById(R.id.btnClickBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

    }

    // Function that disables the button if the specific size for an item is unavailable
    private void disableButton(MaterialButton button){
        button.setEnabled(false);
        button.setStrokeWidth(1);
        button.setStrokeColor(ColorStateList.valueOf(Color.LTGRAY));
        button.setTextColor(Color.LTGRAY);
    }

    // Function that enables the button if the specific size for an item is available
    private void enableButton(MaterialButton button){
        button.setEnabled(true);
        button.setStrokeWidth(1);
        button.setStrokeColor(ColorStateList.valueOf(Color.GRAY));
        button.setTextColor(Color.BLACK);
    }

    // Sets the size variable based on the button last pressed by the user
    private void setSelectedSize(ArrayList<MaterialButton> materialBtnArray, int index){
        for(int i = 0; i < materialBtnArray.size(); i++){
            if(index == i){
                materialBtnArray.get(i).setPressed(true);
                materialBtnArray.get(i).setStrokeWidth(4);
                materialBtnArray.get(i).setStrokeColor(ColorStateList.valueOf(Color.BLACK));
            } else {
                materialBtnArray.get(i).setPressed(false);
                materialBtnArray.get(i).setStrokeWidth(1);
                materialBtnArray.get(i).setStrokeColor(ColorStateList.valueOf(Color.GRAY));
            }
        }
    }


}
