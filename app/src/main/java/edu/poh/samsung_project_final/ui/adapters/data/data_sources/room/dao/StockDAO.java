package edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.entities.StockEntity;

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
    @Query ("SELECT stock_price_when_bought FROM favouritesStocks")
    List<Double> getPricesOfAllStocks();
    @Query ("SELECT id_of_stock FROM favouritesStocks")
    List<String> getAllID();
    @Query("SELECT quantity_of_stock_ent FROM favouritesStocks WHERE id_of_stock = :id")
    Integer getQuantityById(String id);
//    @Query("SELECT id_of_stock FROM favouritesStocks")
//    List<String> getAllId();

}
