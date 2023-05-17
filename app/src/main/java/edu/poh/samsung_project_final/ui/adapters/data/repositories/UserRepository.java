package edu.poh.samsung_project_final.ui.adapters.data.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.dao.StockDAO;
import edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.dao.UserDAO;
import edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.entities.StockEntity;
import edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.entities.UserEntity;
import edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.root.UserDatabase;

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
    //User
    public void insertUser(UserEntity userEntity){
        UserDatabase.databaseWriteExecutor.execute(() -> userDAO.insert(userEntity));
    }
    public void deleteUser(UserEntity userEntity){
        UserDatabase.databaseWriteExecutor.execute(() -> userDAO.delete(userEntity));
    }
    public void update(String login, double money, String password){
        UserDatabase.databaseWriteExecutor.execute(() -> userDAO.update(login,money,password));
    }
    public void updateMoney(double money){
        UserDatabase.databaseWriteExecutor.execute(() -> userDAO.updateMoney(money));
    }
    public double getMoney(){
        return userDAO.getMoney();
    }

    public LiveData<UserEntity> getUser(){
        return userEntityLiveData;
    }

    public void updateLogin(String login) {
        UserDatabase.databaseWriteExecutor.execute(() -> userDAO.updateLogin(login));
    }

    //Stocks
    public void insertStock(StockEntity stockEntity){
        UserDatabase.databaseWriteExecutor.execute(() -> stockDAO.insert(stockEntity));
    }
    public void deleteStock(StockEntity stockEntity){
        UserDatabase.databaseWriteExecutor.execute(() -> stockDAO.delete(stockEntity));
    }
    public void deleteById(String id_of_stock){
        UserDatabase.databaseWriteExecutor.execute(() -> stockDAO.deleteById(id_of_stock));
    }
    public void updateById(String id,Integer quantity,double price){
        UserDatabase.databaseWriteExecutor.execute(() -> stockDAO.updateById(id,quantity,price));
    }
    public double getPriceById(String id){
        return stockDAO.getPriceById(id);
    }

    public LiveData<List<StockEntity>> getIdOfStock(){
        return stockEntityLiveData;
    }

    public List<Double> getPricesOfAllStocks(){
        return stockDAO.getPricesOfAllStocks();
    }

    public List<String> getALLID(){
        return stockDAO.getAllID();
    }

    public Integer getQuantityById(String id){
        return stockDAO.getQuantityById(id);
    }
}
