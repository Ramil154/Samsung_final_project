package edu.poh.samsung_project_final.ui.data.room.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "UserTable")
public class UserEntity {
    @NonNull @PrimaryKey
    public String email;
    public String login;
    public String password;
    public double money;

    public UserEntity(){}

    public UserEntity(@NonNull String email, String password,double money, String login){
        this.email = email;
        this.password = password;
        this.money = money;
        this.login = login;
    }
}
