package edu.poh.samsung_project_final.data.data_sources.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import edu.poh.samsung_project_final.data.data_sources.room.entities.UserEntity;
import edu.poh.samsung_project_final.data.data_sources.room.root.UserDatabase;

@Dao
public interface UserDAO {
    @Insert
    void insert(UserEntity userEntity);
    @Update
    void update(UserEntity userEntity);
    @Delete
    void delete(UserEntity userEntity);
    @Query("SELECT * FROM USERTABLE")
    LiveData<UserEntity> getUser();
}
