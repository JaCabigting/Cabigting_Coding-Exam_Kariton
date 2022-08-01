package com.example.kariton.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.kariton.Database.DatabaseHandler;
import com.example.kariton.Models.CartItem;
import com.example.kariton.R;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    DatabaseHandler handler;
    DrawerLayout drawerLayout;
    LinearLayout cartView;
    ArrayList<CartItem> cartItems = new ArrayList<CartItem>();
    RelativeLayout emptyPrompt;
    Button btnShop;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cart_layout);

        drawerLayout = findViewById(R.id.CartActivity_DrawerLayout);
        cartView = findViewById(R.id.cartView);

        emptyPrompt = findViewById(R.id.emptyPrompt);
        btnShop = findViewById(R.id.btnShop);

        // Calling the database handler
        handler = new DatabaseHandler(CartActivity.this);

        // FUnction for UI Purposes (Highlighting the Current Page in the Side Menu)
        LinearLayout clickerCart = findViewById(R.id.clickerCart);
        LinearLayout.LayoutParams clickerCartParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ImageView clickerCartIcon = findViewById(R.id.clickerCartIcon);
        TextView clickerCartLabel= findViewById(R.id.clickerCartLabel);

        clickerCart.setBackgroundColor(getResources().getColor(R.color.project_teal));
        clickerCartParams.setMargins(0,10, 0, 10);
        clickerCart.setLayoutParams(clickerCartParams);

        clickerCartIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_shopping_cart_white));
        clickerCartLabel.setTextColor(getResources().getColor(R.color.white));

        // If the cart is empty, this button will be displayed to help the user redirect to the shop activity
        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CartActivity.this, ShopActivity.class);
                startActivity(i);
            }
        });

        // Initializes the cart
        refreshCart();

    }

    // Function that refreshes the cart view everytime an item is removed from the cart
    public void refreshCart(){
        cartView.removeAllViews();

        cartItems = handler.viewCart();

        if(cartItems.size() == 0){
            cartView.setVisibility(View.GONE);
            emptyPrompt.setVisibility(View.VISIBLE);
        } else {
            cartView.setVisibility(View.VISIBLE);
            emptyPrompt.setVisibility(View.GONE);

            for(int i = 0; i < cartItems.size(); i++){
                final int index = i;
                final CartItem cartItem = cartItems.get(i);
                View cartItemView = getLayoutInflater().inflate(R.layout.view_item_in_cart_layout, null, false);

                ImageView iv_productImage = cartItemView.findViewById(R.id.iv_productImage);
                TextView tv_productName = cartItemView.findViewById(R.id.tv_productName);
                TextView tv_quantity = cartItemView.findViewById(R.id.tv_quantity);
                TextView tv_salePrice = cartItemView.findViewById(R.id.tv_salePrice);
                TextView tv_originalPrice = cartItemView.findViewById(R.id.tv_originalPrice);
                TextView tv_percentageOff = cartItemView.findViewById(R.id.tv_discount);
                TextView tv_size = cartItemView.findViewById(R.id.tv_size);

                MaterialButton btnIncrease = cartItemView.findViewById(R.id.btnIncrease);
                MaterialButton btnDecrease = cartItemView.findViewById(R.id.btnDecrease);

                ImageButton btnRemove = cartItemView.findViewById(R.id.btnRemoveItem);

                tv_productName.setText(cartItems.get(i).getName());
                tv_quantity.setText(String.valueOf(cartItems.get(i).getQuantity()));
                if(cartItems.get(i).getQuantity() == 1){
                    btnDecrease.setEnabled(false);
                } else {
                    btnDecrease.setEnabled(true);
                }

                tv_originalPrice.setText(cartItems.get(i).getOriginalPrice());
                tv_originalPrice.setPaintFlags(tv_originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tv_salePrice.setText(cartItems.get(i).getSalePrice());
                tv_percentageOff.setText(cartItems.get(i).getPercentOff());
                tv_size.setText(cartItems.get(i).getSize());

                byte[] decodeStream = Base64.decode(cartItems.get(i).getBitmap(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodeStream, 0, decodeStream.length);

                iv_productImage.setImageBitmap(bitmap);

                ArrayList<CartItem> finalCartItems = cartItems;
                btnIncrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int newQty = Integer.valueOf(tv_quantity.getText().toString()) + 1;
                        tv_quantity.setText(String.valueOf(newQty));
                        handler.increaseQuantity(finalCartItems.get(index), finalCartItems.get(index).getSize());
                        btnDecrease.setEnabled(true);
                    }
                });

                btnDecrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int newQty = Integer.valueOf(tv_quantity.getText().toString()) - 1;
                        tv_quantity.setText(String.valueOf(newQty));
                        handler.decreaseQuantity(finalCartItems.get(index), finalCartItems.get(index).getSize());
                        if(tv_quantity.getText().equals("1")){
                            btnDecrease.setEnabled(false);
                        }
                    }
                });

                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDeleteItemPrompt(cartItem);
                    }
                });

                cartItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openItemPage(cartItems.get(index));
                    }
                });

                cartView.addView(cartItemView);
            }
        }



    }

    // Function that opens the pop-up window shoeing information about the item and gives the user the opportunity to add the item to their cart
    public void openItemPage(CartItem item){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View productPageView = inflater.inflate(R.layout.activity_product_layout, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(productPageView, width, height, focusable);

        popupWindow.showAtLocation(findViewById(R.id.CartActivity_DrawerLayout), Gravity.CENTER, 0, 0);
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

        ArrayList<MaterialButton> btnArray = new ArrayList<MaterialButton>();
        btnArray.add(btnXS);
        btnArray.add(btnS);
        btnArray.add(btnM);
        btnArray.add(btnL);
        btnArray.add(btnXL);

        Button btnAddToCart = productPageView.findViewById(R.id.btnAddToCart);

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

        JSONObject[] sizes = new JSONObject[]{xs, s, m, l, xl};

        for(int i = 0; i < btnArray.size(); i++){
            try {
                if(sizes[i].getBoolean("isAvailable") == true){
                    int index = i;
                    enableButton(btnArray.get(i));

                    btnArray.get(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setSelectedSize(btnArray, index);
                            selectedSize[0] = btnArray.get(index).getText().toString();
                            btnAddToCart.setEnabled(true);
                            btnAddToCart.setBackgroundTintList(ContextCompat.getColorStateList(CartActivity.this, R.color.project_teal));
                        }
                    });
                } else {
                    disableButton(btnArray.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Setting the image bitmap
        String bitmapString = item.getBitmap();
        byte[] decodeStream = Base64.decode(bitmapString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodeStream, 0, decodeStream.length);

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handler.addItemToCart(item, selectedSize[0], bitmap);
                Log.e("success", item.getName() + " with size " + selectedSize[0].toString() + " was added to cart.");
                Toast.makeText(CartActivity.this, item.getName() + " with size " + selectedSize[0].toString() + " was added to cart.", Toast.LENGTH_SHORT).show();
                refreshCart();
            }
        });


        iv_productImage.setImageBitmap(bitmap);

        tv_discount.setText("-" + item.getPercentOff() + "%");
        tv_productName.setText(item.getName());
        tv_salePrice.setText(item.getSalePrice());
        tv_originalPrice.setText(item.getOriginalPrice());
        tv_originalPrice.setPaintFlags(tv_originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        tv_description.setText(item.getDescription());

        ImageView btnBack = productPageView.findViewById(R.id.btnClickBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupWindow.dismiss();
                refreshCart();
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

    // Function that shows the prompt when the user wants to delete an item from the cart
    public void showDeleteItemPrompt(CartItem item){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.prompt_delete_item_layout, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(view, width, height, focusable);

        popupWindow.showAtLocation(findViewById(R.id.CartActivity_DrawerLayout), Gravity.CENTER, 0, 0);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        // Programming the buttons in the pop-up window
        Button btnYes = view.findViewById(R.id.btnYes);
        Button btnNo = view.findViewById(R.id.btnNo);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.deleteItemFromCart(item);
                popupWindow.dismiss();
                Toast.makeText(CartActivity.this, "Item has been deleted from cart.", Toast.LENGTH_SHORT).show();
                refreshCart();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
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

    // User is transferred to the Shop Activity
    public void ClickShop(View view){
        Intent i = new Intent(getApplicationContext(), ShopActivity.class);
        startActivity(i);
    }

    // Function closes the side menu if the button pressed is the current activity
    public void ClickCart(View view){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
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

        popupWindow.showAtLocation(findViewById(R.id.CartActivity_DrawerLayout), Gravity.CENTER, 0, 0);
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
