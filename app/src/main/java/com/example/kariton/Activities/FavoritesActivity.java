package com.example.kariton.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.kariton.Database.DatabaseHandler;
import com.example.kariton.Models.CartItem;
import com.example.kariton.Models.FavoriteItem;
import com.example.kariton.R;
import com.example.kariton.Tools.FavoritesAdapter;

import java.util.ArrayList;

public class FavoritesActivity extends AppCompatActivity {

    DatabaseHandler handler;
    DrawerLayout drawerLayout;
    GridView favoritesGrid;
    RelativeLayout emptyPrompt;
    Button btnShop;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_favorites_layout);

        drawerLayout = findViewById(R.id.favoritesActivity_DrawerLayout);
        favoritesGrid = findViewById(R.id.gv_favoritesGrid);
        emptyPrompt = findViewById(R.id.emptyPrompt);
        btnShop = findViewById(R.id.btnShop);

        // Function for UI Purposes (Highlighting the Current Page in the Side Menu)
        LinearLayout clickerFavorite = findViewById(R.id.clickerFavorite);
        LinearLayout.LayoutParams clickerFavoriteParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ImageView clickerFavoriteIcon = findViewById(R.id.clickerFavoriteIcon);
        TextView clickerFavoriteLabel= findViewById(R.id.clickerFavoriteLabel);

        clickerFavorite.setBackgroundColor(getResources().getColor(R.color.project_teal));
        clickerFavoriteParams.setMargins(0,10, 0, 10);
        clickerFavorite.setLayoutParams(clickerFavoriteParams);

        clickerFavoriteIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_favorite_white));
        clickerFavoriteLabel.setTextColor(getResources().getColor(R.color.white));

        // If the favorites is empty, this button will be displayed to help the user redirect to the shop activity
        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FavoritesActivity.this, ShopActivity.class);
                startActivity(i);
            }
        });

        // Initializing the Array List that will hold the data retrieved from the database
        ArrayList<FavoriteItem> favoriteItems = new ArrayList<FavoriteItem>();

        // Calling the database handler
        handler = new DatabaseHandler(FavoritesActivity.this);

        // Retrieves all of the items in the user's favorites from the database
        favoriteItems = handler.viewFavorites();

        // Setting up the adapter to display the items liked by the user in the grid view
        FavoritesAdapter favoritesAdapter = new FavoritesAdapter(FavoritesActivity.this, favoriteItems);
        favoritesGrid.setAdapter(favoritesAdapter);

        // Initializes the display depends on the number of items added by the user to their favorites
        if(favoritesAdapter.getCount() == 0){
            favoritesGrid.setVisibility(View.GONE);
            emptyPrompt.setVisibility(View.VISIBLE);
        } else {
            favoritesGrid.setVisibility(View.VISIBLE);
            emptyPrompt.setVisibility(View.GONE);
        }

    }


    // Function if the Menu (Three horizontal lines) button is pressed
    public void ClickMenu(View view){
        openDrawer(drawerLayout);
    }

    // Function that displays the side menu
    private static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    // User is transferred to the Shop Activity
    public void ClickShop(View view){
        Intent i = new Intent(getApplicationContext(), ShopActivity.class);
        startActivity(i);
    }

    // User is transferred to the Cart Activity
    public void ClickCart(View view){
        Intent i = new Intent(getApplicationContext(), CartActivity.class);
        startActivity(i);
    }

    // Function closes the side menu if the button pressed is the current activity
    public void ClickFavorite(View view){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
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

        popupWindow.showAtLocation(findViewById(R.id.favoritesActivity_DrawerLayout), Gravity.CENTER, 0, 0);
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

    // When back button is pressed, it will go back to Shop Activity
    @Override
    public void onBackPressed(){
        Intent i = new Intent(getApplicationContext(), ShopActivity.class);
        startActivity(i);
    }



}
