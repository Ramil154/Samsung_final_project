package edu.poh.samsung_project_final.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.data.data_sources.room.entities.UserEntity;
import edu.poh.samsung_project_final.databinding.FragmentRegistrationBinding;
import edu.poh.samsung_project_final.ui.view_models.UserViewModel;

public class registration extends Fragment {
    private FragmentRegistrationBinding binding;
    private NavHostFragment navHostFragment;
    private NavController navController;
    private UserViewModel userViewModel;
    private final double MONEY = 20000;
    private final String NOT_LOG = "Not correct log";
    public static registration newInstance() {return null;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.RegistrationCharacterMemorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.RegistrationEmailMemorize.getText().toString();
                if (email.length() == 0){
                    Toast.makeText(getContext(), "Вы не ввели email в поле 'Введите email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.length() < 4){
                    Toast.makeText(getContext(), "Минимальное число символов в поле 'Введите логин' 4", Toast.LENGTH_SHORT).show();
                    return;
                }
                String password_first = binding.RegistrationPasswordCheck.getText().toString();
                String password_two = binding.RegistrationPasswordMemorize.getText().toString();
                if (password_first.length() < 4 && password_two.length() < 4){
                    Toast.makeText(getContext(), "Минимальное число символов в поле 'Введите пароль' и 'Повторите пароль' 4", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password_first.equals(password_two)){
                    Toast.makeText(getContext(), "Ваши пароли не совпадают!", Toast.LENGTH_SHORT).show();
                    return;
                }
                userViewModel.insertUser(new UserEntity(email,password_two,MONEY,NOT_LOG));
                userViewModel.userEntity.email = email;
                userViewModel.userEntity.login = NOT_LOG;
                userViewModel.userEntity.money = MONEY;
                navController.navigate(R.id.action_registration_to_rememberLogin);
            }
        });

    }
}