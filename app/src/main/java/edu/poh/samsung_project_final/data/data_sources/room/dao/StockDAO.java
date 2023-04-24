package edu.poh.samsung_project_final.data.data_sources.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

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
}
