package com.example.kariton.Tools;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.kariton.Activities.CartActivity;
import com.example.kariton.Activities.ShopActivity;
import com.example.kariton.Database.DatabaseHandler;
import com.example.kariton.Models.Product;
import com.example.kariton.Models.Shop;
import com.example.kariton.Models.Size;
import com.example.kariton.R;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class GridAdapter extends ArrayAdapter<Product> {

    Context context;
    ArrayList<Product> products;
    DatabaseHandler handler;

    // Constructing the adapter
    public GridAdapter(@NonNull Context context, ArrayList<Product> products){
        super(context, 0, products);
        this.context = context;
        this.products = products;
    }

    // Gets the number of products
    @Override
    public int getCount() {
        return products.size();
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

        // Loading the image from the URL retrieved from the response
        try {
            Picasso.get().load(String.valueOf(getItem(position).getImageUrl().toURI())).into(iv_productImage);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String description = getItem(position).getDescription();
        Size[] sizes = getItem(position).getSizes();

        // Setting the product/item's info to their respective Text Views
        tv_discount.setText("-" + getItem(position).getPercentOff() + "%");

        tv_productName.setText(getItem(position).getName());

        tv_salePrice.setText(getItem(position).getSalePrice());

        tv_originalPrice.setText(getItem(position).getOriginalPrice());
        tv_originalPrice.setPaintFlags(tv_originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // Constructing the Product object to be used on other functions
        Product currentProduct = new Product();
        currentProduct.setName(tv_productName.getText().toString());
        currentProduct.setDescription(description);
        currentProduct.setOriginalPrice(tv_originalPrice.getText().toString());
        currentProduct.setSalePrice(tv_salePrice.getText().toString());
        currentProduct.setPercentOff(tv_discount.getText().toString());
        currentProduct.setSizes(sizes);
        currentProduct.setIndex(position);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProductPage(context, currentProduct, ((BitmapDrawable)iv_productImage.getDrawable()).getBitmap());
            }
        });

        // Assigning current tag to "Favorite" button to track if item is liked by user or not
        ib_btnFavorite.setTag("R.drawable.ic_baseline_favorite_unfilled");

        ib_btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnFavoriteAction(currentProduct, ((BitmapDrawable)iv_productImage.getDrawable()).getBitmap(), ib_btnFavorite);
            }
        });



        return convertView;
    }

    // Function when the favorite button is pressed
    private void btnFavoriteAction(Product product, Bitmap bitmap, ImageButton button){
        // Convert bitmap into a string
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] bytes = outputStream.toByteArray();
        String byteString = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);

        if (((String) button.getTag()).equals("R.drawable.ic_baseline_favorite_unfilled")) {
            button.setTag("R.drawable.ic_baseline_favorite_filled");
            button.setImageResource(R.drawable.ic_baseline_favorite_filled);
            handler.addProductToFavorite(product, bitmap);
            Toast.makeText(context, product.getName() + " added to Favorites", Toast.LENGTH_SHORT).show();
        } else {
            button.setTag("R.drawable.ic_baseline_favorite_unfilled");
            button.setImageResource(R.drawable.ic_baseline_favorite_unfilled);
            handler.deleteProductFromFavorite(product, bitmap);
            Toast.makeText(context, product.getName() + " removed from Favorites", Toast.LENGTH_SHORT).show();
        }
    }

    // Function that opens the page containing the information about the product
    private void openProductPage(Context context, Product product, Bitmap bitmap){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View productPageView = inflater.inflate(R.layout.activity_product_layout, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(productPageView, width, height, focusable);

        popupWindow.showAtLocation(((ShopActivity) context).findViewById(R.id.shopActivity_DrawerLayout), Gravity.CENTER, 0, 0);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        final String[] selectedSize = {new String()};

        ImageView iv_productImage = productPageView.findViewById(R.id.iv_productImage);
        TextView tv_discount = productPageView.findViewById(R.id.tv_discount);
        TextView tv_productName = productPageView.findViewById(R.id.tv_productName);
        TextView tv_salePrice = productPageView.findViewById(R.id.tv_salePrice);
        TextView tv_originalPrice = productPageView.findViewById(R.id.tv_originalPrice);
        TextView tv_description = productPageView.findViewById(R.id.tv_description);

        MaterialButton btnXS = productPageView.findViewById(R.id.btnXS);
        MaterialButton btnS = productPageView.findViewById(R.id.btnS);
        MaterialButton btnM = productPageView.findViewById(R.id.btnM);
        MaterialButton btnL = productPageView.findViewById(R.id.btnL);
        MaterialButton btnXL = productPageView.findViewById(R.id.btnXL);

        // Setting up the buttons and whether they are clickable or not based on the item's availability on each size
        ArrayList<MaterialButton> btnArray = new ArrayList<MaterialButton>();
        btnArray.add(btnXS);
        btnArray.add(btnS);
        btnArray.add(btnM);
        btnArray.add(btnL);
        btnArray.add(btnXL);

        TextView btnSeeMore = productPageView.findViewById(R.id.btnSeeMore);

        Button btnAddToCart = productPageView.findViewById(R.id.btnAddToCart);

        for(int i = 0; i < btnArray.size(); i++){
            if(product.getSizes()[i].isAvailable() == true){ // If item is available in that specific size
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
            } else { // If item is not available in that specific size
                disableButton(btnArray.get(i));
            }
        }

        // Setting up the display where the products/items recommended will be displayed
        LinearLayout recommendedView = productPageView.findViewById(R.id.recommendedView);
        GridView recommendedGrid = productPageView.findViewById(R.id.recommendedGrid);
        GridAdapter gridAdapter = new GridAdapter(context, generateRecommendations(product, product.getIndex(), products));
        recommendedGrid.setAdapter(gridAdapter);

        // If there is no products being recommended, the "Recommended For You" section will be hidden
        if(generateRecommendations(product, product.getIndex(), products).size() == 0){
            recommendedView.setVisibility(View.GONE);
        } else {
            recommendedView.setVisibility(View.VISIBLE);
        }

        // Function when the "See More" in the "Recommended For You" section is pressed
        btnSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRecommendationsPage(context, generateRecommendations(product, product.getIndex(), products));
            }
        });

        // Adds the item to the cart
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handler.addProductToCart(product, selectedSize[0], bitmap);
                Log.e("success", product.getName() + " with size " + selectedSize[0].toString() + " was added to cart.");
                Toast.makeText(context, product.getName() + " with size " + selectedSize[0].toString() + " was added to cart.", Toast.LENGTH_SHORT).show();

            }
        });

        // Setting the product's image bitmap to the image view
        iv_productImage.setImageBitmap(bitmap);

        // Setting the product/item's info to their respective Text Views
        tv_discount.setText(product.getPercentOff());
        tv_productName.setText(product.getName());
        tv_salePrice.setText(product.getSalePrice());
        tv_originalPrice.setText(product.getOriginalPrice());
        tv_originalPrice.setPaintFlags(tv_originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tv_description.setText(product.getDescription());

        // Setting up the back button
        ImageView btnBack = productPageView.findViewById(R.id.btnClickBack);

        // Function when clicked, closes the pop-up product/item page
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });


    }

    // Opens a pop-up window that shows all of the recommended products
    private void openRecommendationsPage(Context context, ArrayList<Product> products){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View productPageView = inflater.inflate(R.layout.activity_view_recommendations_layout, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(productPageView, width, height, focusable);

        popupWindow.showAtLocation(((ShopActivity) context).findViewById(R.id.shopActivity_DrawerLayout), Gravity.CENTER, 0, 0);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        ImageView btnBack = productPageView.findViewById(R.id.btnClickBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        // Sets up the adapter and implement it to the grid view
        GridView recommendedGrid = productPageView.findViewById(R.id.gvRecommendationsGrid);
        GridAdapter gridAdapter = new GridAdapter(context, products);
        recommendedGrid.setAdapter(gridAdapter);

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

    // Generates the recommendations based on the product's name
    private ArrayList<Product> generateRecommendations(Product product, int index, ArrayList<Product> shop){
        ArrayList<Product> recommendations = new ArrayList<Product>();

        for(int i = 0; i < shop.size(); i++){
            if(product.getName().equals(shop.get(i).getName()) && index != i){
                recommendations.add(shop.get(i));
            }
        }

        return recommendations;
    }



}
