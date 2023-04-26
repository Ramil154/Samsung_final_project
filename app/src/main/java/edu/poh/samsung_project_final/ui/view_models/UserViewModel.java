package edu.poh.samsung_project_final.ui.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import edu.poh.samsung_project_final.data.data_sources.room.entities.UserEntity;
import edu.poh.samsung_project_final.data.repositories.UserRepository;

public class UserViewModel extends AndroidViewModel {
    private UserEntity userEntity;
    private UserRepository userRepository;
    private Application application;
    private LiveData<UserEntity> userEntityLiveData;
    public UserViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        userRepository = new UserRepository(application);

        userEntityLiveData = userRepository.getUser();
        userEntity = new UserEntity();
    }

    public LiveData<UserEntity> getUser(){return userEntityLiveData;}

    public void insertUser(UserEntity userEntity) {
        userRepository.insertUser(userEntity);
    }

    public void deleteUser(UserEntity userEntity){
        userRepository.deleteUser(userEntity);
    }
}
