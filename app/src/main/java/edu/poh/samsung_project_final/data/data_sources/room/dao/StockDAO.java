package edu.poh.samsung_project_final.data.data_sources.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import edu.poh.samsung_project_final.data.data_sources.room.entities.StockEntity;

@Dao
public interface StockDAO {
    @Insert
    void insert(StockEntity stockEntity);

    @Delete
    void delete(StockEntity stockEntity);
    @Query("SELECT * FROM favouritesStocks")
    LiveData<List<StockEntity>> getIdOfStock();
    @Query("DELETE FROM favouritesStocks WHERE id_of_stock = :id ")
    void deleteById(String id);
    @Query("UPDATE favouritesStocks SET quantity_of_stock_ent = :quantity, stock_price_when_bought = :price WHERE id_of_stock = :id")
    void updateById(String id,Integer quantity, double price);
    @Query("SELECT stock_price_when_bought FROM favouritesStocks WHERE id_of_stock = :id")
    double getPriceById(String id);

}
