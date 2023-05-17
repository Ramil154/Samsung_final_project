package edu.poh.samsung_project_final.ui.view_models;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.entities.StockEntity;
import edu.poh.samsung_project_final.ui.adapters.data.repositories.UserRepository;

public class StockDataViewModel extends AndroidViewModel {
    public StockEntity stockEntity;
    public UserRepository userRepository;
    private Application application;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private LiveData<List<StockEntity>> listLiveData;
    public  StockDataViewModel(Application application){
        super(application);
        this.application = application;
        userRepository = new UserRepository(application);

        listLiveData = userRepository.getIdOfStock();
        stockEntity = new StockEntity();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public LiveData<List<StockEntity>> getIdOfStock(){return listLiveData;}

    public void insertStock(StockEntity stockEntity){
        userRepository.insertStock(stockEntity);
    }
    public void deleteStock(StockEntity stockEntity){
        userRepository.deleteStock(stockEntity);
    }
    public void deleteById(String id_of_stock){
        userRepository.deleteById(id_of_stock);
    }
    public void updateById(String id,Integer quantity,double price){
        userRepository.updateById(id,quantity,price);
    }
    public double getPriceById(String id){
        return userRepository.getPriceById(id);
    }

    public List<Double> getPricesOfAllStocks(){
        return userRepository.getPricesOfAllStocks();
    }

    public List<String> getALlID(){
        return userRepository.getALLID();
    }

    public Integer getQuantityById(String id){
        return userRepository.getQuantityById(id);
    }

    public void loadUserDataToFireBase(){
        Log.d("Firebasexxx",mAuth.getCurrentUser().getUid());
        databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("favourites");
    }
}
