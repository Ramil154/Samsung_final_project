package edu.poh.samsung_project_final.ui.ui.view_models;

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

import edu.poh.samsung_project_final.ui.data.DataLoadCallback;
import edu.poh.samsung_project_final.ui.data.models.FireBaseModel;
import edu.poh.samsung_project_final.ui.data.room.entities.UserEntity;
import edu.poh.samsung_project_final.ui.data.repositories.UserRepository;

public class UserViewModel extends AndroidViewModel {
    public UserEntity userEntity;
    private UserRepository userRepository;
    private Context context;
    private Application application;
    private LiveData<UserEntity> userEntityLiveData;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FireBaseModel fireBaseModel;
    private DatabaseReference databaseReference;
    public UserViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        userRepository = new UserRepository(application);

        userEntityLiveData = userRepository.getUser();
        userEntity = new UserEntity();
        context = application.getApplicationContext();
        fireBaseModel = new FireBaseModel();

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

    public void loadUserDataToFireBase(String email, String login, double money, String password){
        fireBaseModel.loadUserToFireBase(email,login, money,password);
    }

    public void uploadUserDataFromFireBase(Context context , DataLoadCallback callback){
        fireBaseModel.uploadUserFromFireBase(userEntity, callback, context);
    }
}
