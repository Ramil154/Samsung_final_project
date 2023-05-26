package edu.poh.samsung_project_final.ui.ui;

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
import edu.poh.samsung_project_final.databinding.FragmentRememberLoginBinding;
import edu.poh.samsung_project_final.ui.ui.view_models.UserViewModel;

public class RememberLogin extends Fragment {
    private FragmentRememberLoginBinding binding;
    private NavHostFragment navHostFragment;
    private NavController navController;
    private UserViewModel userViewModel;

    public static RememberLogin newInstance() {return null;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRememberLoginBinding.inflate(inflater, container, false);
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.NextToApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.LoginMemorise.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Вы не ввели имя в поле 'Ввод имени'", Toast.LENGTH_SHORT).show();
                }
                else{
                    userViewModel.updateLogin(binding.LoginMemorise.getText().toString());
                    userViewModel.userEntity.login = binding.LoginMemorise.getText().toString();
                    Log.d("LoginUser", userViewModel.userEntity.login);
                    navController.navigate(R.id.action_rememberLogin_to_main_list_of_app);
                }
            }
        });
    }
}