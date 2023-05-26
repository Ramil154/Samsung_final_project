package edu.poh.samsung_project_final.ui.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.ui.data.room.entities.StockEntity;
import edu.poh.samsung_project_final.databinding.ActivityMainBinding;
import edu.poh.samsung_project_final.ui.ui.view_models.StockDataViewModel;
import edu.poh.samsung_project_final.ui.ui.view_models.UserViewModel;
import edu.poh.samsung_project_final.ui.ui.view_models.stockSearchViewModel;

public class MainActivity extends AppCompatActivity {
    public ActivityMainBinding binding;
    private UserViewModel userViewModel;
    private StockDataViewModel stockDataViewModel;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private NavController navController;
    private DatabaseReference databaseReference;
    private stockSearchViewModel model;
    private LifecycleOwner lifecycleOwner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        lifecycleOwner = (LifecycleOwner) this;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        stockDataViewModel = new ViewModelProvider(this).get(StockDataViewModel.class);
        model = new ViewModelProvider(this).get(stockSearchViewModel.class);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.main_list_of_app, R.id.favourites_of_character, R.id.stock_search)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuth.getCurrentUser() != null) {
            userViewModel.update(userViewModel.userEntity.password, userViewModel.userEntity.money, userViewModel.userEntity.login);
            if (isNetworkAvailable()) {
                userViewModel.loadUserDataToFireBase(userViewModel.userEntity.email, userViewModel.userEntity.login, userViewModel.userEntity.money, userViewModel.userEntity.password);
                stockDataViewModel.getIdOfStock().observe(this, new Observer<List<StockEntity>>() {
                    @Override
                    public void onChanged(List<StockEntity> stockEntities) {
                        Integer counter = 0;
                        for (StockEntity stockEntity : stockEntities) {
                            databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("favourites").child(stockEntity.id_of_stock).child("id").setValue(stockEntity.id_of_stock);
                            databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("favourites").child(stockEntity.id_of_stock).child("name").setValue(stockEntity.name_of_stock);
                            databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("favourites").child(stockEntity.id_of_stock).child("cost").setValue(stockEntity.stock_price_when_bought);
                            databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("favourites").child(stockEntity.id_of_stock).child("quantity").setValue(stockEntity.quantity_of_stock_ent);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Не удалось загрузить данные в облако. Но они сохранены на вашем устройстве!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("DestriyUser", "Destroy");
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}