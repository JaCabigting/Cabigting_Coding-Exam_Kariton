package com.example.kariton.Models;

import java.util.ArrayList;

public class Shop {
   private ArrayList<Product> d; // This is necessary since the response contains an object "d" that holds the array of products

   // Getter and Setter
   public ArrayList<Product> getD() {
      return d;
   }

   public void setD(ArrayList<Product> d) {
      this.d = d;
   }
}
