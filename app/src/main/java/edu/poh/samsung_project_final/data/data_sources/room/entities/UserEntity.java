package edu.poh.samsung_project_final.data.data_sources.room.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "UserTable")
public class UserEntity {
    @NonNull @PrimaryKey
    public String login;
    public String password;

    public UserEntity(){}

    public UserEntity(@NonNull String login, String password){
        this.login = login;
        this.password = password;
    }
}
