package edu.poh.samsung_project_final.data.data_sources.room.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "UserTable")
public class UserEntity {
    @NonNull @PrimaryKey
    public String login;
    public String password;
    public double money;

    public UserEntity(){}

    public UserEntity(@NonNull String login, String password,double money){
        this.login = login;
        this.password = password;
        this.money = money;
    }
}
