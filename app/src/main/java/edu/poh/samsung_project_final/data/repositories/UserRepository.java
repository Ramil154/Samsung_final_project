package edu.poh.samsung_project_final.data.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import edu.poh.samsung_project_final.data.data_sources.room.dao.StockDAO;
import edu.poh.samsung_project_final.data.data_sources.room.dao.UserDAO;
import edu.poh.samsung_project_final.data.data_sources.room.entities.StockEntity;
import edu.poh.samsung_project_final.data.data_sources.room.entities.UserEntity;
import edu.poh.samsung_project_final.data.data_sources.room.root.UserDatabase;

public class UserRepository {
    StockDAO stockDAO;
    UserDAO userDAO;
    LiveData<List<StockEntity>> stockEntityLiveData;
    LiveData<UserEntity> userEntityLiveData;
    public UserRepository(Application application){
        UserDatabase userDatabase = UserDatabase.getDatabase(application);
        stockDAO = userDatabase.stockDAO();
        userDAO = userDatabase.userDAO();
        userEntityLiveData = userDAO.getUser();
        stockEntityLiveData = stockDAO.getIdOfStock();
    }
    public void insertUser(UserEntity userEntity){
        UserDatabase.databaseWriteExecutor.execute(() -> userDAO.insert(userEntity));
    }
    public void deleteUser(UserEntity userEntity){
        UserDatabase.databaseWriteExecutor.execute(() -> userDAO.delete(userEntity));
    }
    public void updateUser(UserEntity userEntity){
        UserDatabase.databaseWriteExecutor.execute(() -> userDAO.update(userEntity));
    }
    public LiveData<UserEntity> getUser(){
        return userEntityLiveData;
    }
    public void insertStock(StockEntity stockEntity){
        UserDatabase.databaseWriteExecutor.execute(() -> stockDAO.insert(stockEntity));
    }
    public void deleteStock(StockEntity stockEntity){
        UserDatabase.databaseWriteExecutor.execute(() -> stockDAO.delete(stockEntity));
    }
    public LiveData<List<StockEntity>> getIdOfStock(){
        return stockEntityLiveData;
    }
}
