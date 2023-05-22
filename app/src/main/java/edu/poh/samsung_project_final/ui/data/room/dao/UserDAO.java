package edu.poh.samsung_project_final.ui.data.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import edu.poh.samsung_project_final.ui.data.room.entities.UserEntity;

@Dao
public interface UserDAO {
    @Insert
    void insert(UserEntity userEntity);
    @Delete
    void delete(UserEntity userEntity);
    @Query("UPDATE UserTable SET login = :login, money = :money, password = :password")
    void update(String login, double money, String password);
    @Query("SELECT * FROM UserTable")
    LiveData<UserEntity> getUser();
    @Query("UPDATE UserTable SET money = :money")
    void updateMoney(double money);
    @Query("SELECT money FROM UserTable")
    double getMoney();
    @Query("UPDATE UserTable SET login = :newlogin")
    void updateLogin(String newlogin);
}
