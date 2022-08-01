package com.example.kariton.Activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.example.kariton.Models.Product;
import com.example.kariton.Models.Shop;
import com.example.kariton.R;

import com.example.kariton.Retrofit.APIService;
import com.example.kariton.Retrofit.RetrofitClient;
import com.example.kariton.Tools.GridAdapter;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ShopActivity extends AppCompatActivity {

    GridView productGrid;
    APIService apiService;

    DrawerLayout drawerLayout;


    // Authentication provided in the coding exam instructions
    final String TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkNvZGVyIn0.B1QyKzKxzpxay1__A8B85ij32rqFoOIAFGDqBmqXhvs";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_layout); // Setting the view of the Shop Activity

        // Initializing the Retrofit service
        Retrofit retrofitClient = RetrofitClient.getInstance();
        apiService = retrofitClient.create(APIService.class);

        productGrid = findViewById(R.id.gvProductGrid);
        drawerLayout = (DrawerLayout) findViewById(R.id.shopActivity_DrawerLayout);

        // Function for UI Purposes (Highlighting the Current Page in the Side Menu)
        LinearLayout clickerShop = findViewById(R.id.clickerShop);
        LinearLayout.LayoutParams clickerShopParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ImageView clickerShopIcon = findViewById(R.id.clickerShopIcon);
        TextView clickerShopLabel= findViewById(R.id.clickerShopLabel);

        clickerShop.setBackgroundColor(getResources().getColor(R.color.project_teal));
        clickerShopParams.setMargins(0,10, 0, 10);
        clickerShop.setLayoutParams(clickerShopParams);

        clickerShopIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_shopping_bag_white));
        clickerShopLabel.setTextColor(getResources().getColor(R.color.white));


        // Calling the function that requests the list of products
        Call<Shop> products = apiService.getProducts(TOKEN);
        products.enqueue(new Callback<Shop>() {
            @Override
            public void onResponse(Call<Shop> call, Response<Shop> response) {
                //Array List containing all of the products listed on the response
                ArrayList<Product> productList = new ArrayList<Product>();
                ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();

                // Adding the products from the response to the array list
                for(int i = 0; i < response.body().getD().size(); i++){
                    productList.add(response.body().getD().get(i));
                }

                // Setting up the adapter to display the items in the grid view
                GridAdapter gridAdapter = new GridAdapter(ShopActivity.this, productList);
                productGrid.setAdapter(gridAdapter);

            }

            @Override
            public void onFailure(Call<Shop> call, Throwable t) {

            }
        });


    }

    // Function if the Menu (Three horizontal lines) button is pressed
    public void ClickMenu(View view){
        openDrawer(drawerLayout);
    }

    // Function that displays the side menu
    private static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    // Function closes the side menu if the button pressed is the current activity
    public void ClickShop(View view){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    // User is transferred to the Cart Activity
    public void ClickCart(View view){
        Intent i = new Intent(getApplicationContext(), CartActivity.class);
        startActivity(i);
    }

    // User is transferred to the Favorites Activity
    public void ClickFavorite(View view){
        Intent i = new Intent(getApplicationContext(), FavoritesActivity.class);
        startActivity(i);
    }

    // Function shows a prompt if user wants to exit from the app
    public void ClickExit(View view){
        showExitPrompt();
    }

    // Shows a prompt if user is sure they want to exit the application
    public void showExitPrompt(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.prompt_exit_layout, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(view, width, height, focusable);

        popupWindow.showAtLocation(findViewById(R.id.shopActivity_DrawerLayout), Gravity.CENTER, 0, 0);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        Button btnYes = view.findViewById(R.id.btnYes);
        Button btnNo = view.findViewById(R.id.btnNo);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                finishAffinity();
                System.exit(0);
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });


    }

    // When back button is pressed, it will trigger showExitPrompt() function
    @Override
    public void onBackPressed(){
        showExitPrompt();
    }



}
