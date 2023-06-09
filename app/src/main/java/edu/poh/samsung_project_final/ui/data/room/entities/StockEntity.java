package edu.poh.samsung_project_final.ui.data.room.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "favouritesStocks")
public class StockEntity {
    @NonNull @PrimaryKey
    public String id_of_stock;
    public String name_of_stock;
    public Integer quantity_of_stock_ent;
    public double stock_price_when_bought;

    public StockEntity(){}

    public StockEntity(@NonNull String id_of_stock, String name_of_stock, int quantity_of_stock_ent,double stock_price_when_bought){
        this.id_of_stock = id_of_stock;
        this.quantity_of_stock_ent = quantity_of_stock_ent;
        this.name_of_stock = name_of_stock;
        this.stock_price_when_bought = stock_price_when_bought;
    }
}
