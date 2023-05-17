package edu.poh.samsung_project_final.ui.view_models;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.DataLoadCallback;
import edu.poh.samsung_project_final.ui.adapters.data.data_sources.room.entities.UserEntity;
import edu.poh.samsung_project_final.ui.adapters.data.repositories.UserRepository;

public class UserViewModel extends AndroidViewModel {
    public UserEntity userEntity;
    private UserRepository userRepository;
    private Context context;
    private Application application;
    private LiveData<UserEntity> userEntityLiveData;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    public UserViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        userRepository = new UserRepository(application);

        userEntityLiveData = userRepository.getUser();
        userEntity = new UserEntity();
        context = application.getApplicationContext();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public LiveData<UserEntity> getUser(){return userEntityLiveData;}

    public void insertUser(UserEntity userEntity) {
        userRepository.insertUser(userEntity);
    }

    public void update(String login, double money, String password){userRepository.update(login,money,password);}

    public void deleteUser(UserEntity userEntity){
        userRepository.deleteUser(userEntity);
    }

    public void updateMoney(double money){
        userRepository.updateMoney(money);
    }

    public double getMoney(){
        return userRepository.getMoney();
    }

    public void updateLogin(String login) {userRepository.updateLogin(login);}

    public void loadUserDataToFireBase(){
        databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("email").setValue(userEntity.email);
        databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("login").setValue(userEntity.login);
        databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("money").setValue(userEntity.money);
        databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("password").setValue(userEntity.password);
    }

    public void uploadUserDataFromFireBase(DataLoadCallback callback){
        databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userEntity.login = snapshot.child("login").getValue().toString();
                userEntity.email = snapshot.child("email").getValue().toString();
                userEntity.password = snapshot.child("password").getValue().toString();
                userEntity.money = snapshot.child("money").getValue(Double.class);
                //userEntity.money = Double.parseDouble(money_str);
                callback.onDataLoaded();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Не удалось загрузить данные из базы. Будут использованы локальные данные", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
