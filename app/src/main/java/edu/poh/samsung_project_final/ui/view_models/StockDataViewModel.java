package edu.poh.samsung_project_final.ui.view_models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.io.Closeable;
import java.util.List;

import edu.poh.samsung_project_final.data.data_sources.room.entities.StockEntity;
import edu.poh.samsung_project_final.data.repositories.UserRepository;

public class StockDataViewModel extends AndroidViewModel {
    public StockEntity stockEntity;
    public UserRepository userRepository;
    private Application application;
    private LiveData<List<StockEntity>> listLiveData;
    public  StockDataViewModel(Application application){
        super(application);
        this.application = application;
        userRepository = new UserRepository(application);

        listLiveData = userRepository.getIdOfStock();
        stockEntity = new StockEntity();
    }

    public LiveData<List<StockEntity>> getIdOfStock(){return listLiveData;}

    public void insertStock(StockEntity stockEntity){
        userRepository.insertStock(stockEntity);
    }
    public void deleteStock(StockEntity stockEntity){
        userRepository.deleteStock(stockEntity);
    }
    public void updateStock(StockEntity stockEntity){
        userRepository.updateStock(stockEntity);
    }


}
