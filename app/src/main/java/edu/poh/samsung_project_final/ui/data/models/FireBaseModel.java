package edu.poh.samsung_project_final.ui.data.models;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.poh.samsung_project_final.ui.data.DataLoadCallback;
import edu.poh.samsung_project_final.ui.data.room.entities.UserEntity;

public class FireBaseModel {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    public FireBaseModel(){
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public void loadUserToFireBase(String email, String login, double money, String password){
        databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("email").setValue(email);
        databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("login").setValue(login);
        databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("money").setValue(money);
        databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("password").setValue(password);
    }

    public void uploadUserFromFireBase(UserEntity userEntity, DataLoadCallback callback, Context context){
        databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userEntity.login = snapshot.child("login").getValue().toString();
                userEntity.email = snapshot.child("email").getValue().toString();
                userEntity.password = snapshot.child("password").getValue().toString();
                userEntity.money = snapshot.child("money").getValue(Double.class);
                callback.onDataLoaded();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Не удалось загрузить данные из базы. Будут использованы локальные данные", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
