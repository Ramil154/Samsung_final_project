package edu.poh.samsung_project_final.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.data.data_sources.room.entities.UserEntity;
import edu.poh.samsung_project_final.databinding.ActivityMainBinding;
import edu.poh.samsung_project_final.ui.view_models.StockDataViewModel;
import edu.poh.samsung_project_final.ui.view_models.UserViewModel;

public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding binding;
    private UserViewModel userViewModel;
    private StockDataViewModel stockDataViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        stockDataViewModel = new ViewModelProvider(this).get(StockDataViewModel.class);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.main_list_of_app, R.id.favourites_of_character, R.id.stock_search)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        // Hide BottomNavigationView if not main layouts
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId() == R.id.main_list_of_app || destination.getId() == R.id.favourites_of_character || destination.getId() == R.id.stock_search) {
                    binding.navView.setVisibility(View.VISIBLE);
                } else {
                    binding.navView.setVisibility(View.GONE);
                }
            }
        });

//        //Check first enter or nonfirst enter
//        userViewModel.getUser().observe(this, new Observer<UserEntity>() {
//            @Override
//            public void onChanged(UserEntity userEntity) {
//                if (userEntity == null){
//                    navController.popBackStack(R.id.my_navigation,true);
//                    navController.navigate(R.id.enter);
//                }
//            }
//        });
    }
}