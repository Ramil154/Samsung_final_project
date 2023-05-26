package edu.poh.samsung_project_final.ui.ui;

import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.ui.data.DataLoadCallback;
import edu.poh.samsung_project_final.ui.data.room.entities.StockEntity;
import edu.poh.samsung_project_final.databinding.FragmentEnterBinding;
import edu.poh.samsung_project_final.ui.data.room.entities.UserEntity;
import edu.poh.samsung_project_final.ui.ui.view_models.StockDataViewModel;
import edu.poh.samsung_project_final.ui.ui.view_models.UserViewModel;

public class enter extends Fragment {
    private FragmentEnterBinding binding;
    private NavHostFragment navHostFragment;
    private NavController navController;
    private FirebaseAuth mAuth;
    private UserViewModel userViewModel;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private StockDataViewModel stockDataViewModel;
    public static enter newInstance() {return null;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEnterBinding.inflate(inflater, container, false);
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        stockDataViewModel = new ViewModelProvider(getActivity()).get(StockDataViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.progressBarEnter.setVisibility(View.GONE);
        binding.RegistrationNextToApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_enter_to_registration);
            }
        });
        binding.GoNextToApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.EmailEnterCheck.getText().toString();
                String password = binding.PasswordEnterCheck.getText().toString();
                if (email.isEmpty()){
                    Toast.makeText(getContext(), "Вы не ввели email в поле 'Введите email'", Toast.LENGTH_SHORT).show();
                }
                else if (password.isEmpty()){
                    Toast.makeText(getContext(), "Вы не ввели пароль в поле 'Введите пароль'", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                binding.GoNextToApp.setVisibility(View.GONE);
                                binding.RegistrationNextToApp.setVisibility(View.GONE);
                                binding.progressBarEnter.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(requireContext(), R.color.Blue_400), PorterDuff.Mode.SRC_IN);
                                binding.progressBarEnter.setVisibility(View.VISIBLE);
                                userViewModel.uploadUserDataFromFireBase(requireContext(),new DataLoadCallback() {
                                    @Override
                                    public void onDataLoaded() {
                                        userViewModel.insertUser(new UserEntity(email,password,userViewModel.userEntity.money,userViewModel.userEntity.login));
                                        databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("favourites").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                    String id = dataSnapshot.child("id").getValue(String.class);
                                                    String name = dataSnapshot.child("name").getValue(String.class);
                                                    Integer quantity = dataSnapshot.child("quantity").getValue(Integer.class);
                                                    Double cost = dataSnapshot.child("cost").getValue(Double.class);
                                                    stockDataViewModel.insertStock(new StockEntity(id, name, quantity, cost));
                                                }
                                                navController.navigate(R.id.action_enter_to_main_list_of_app);
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                });
                            }else{
                                Toast.makeText(getContext(), "Неправильно введен email или пароль", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}