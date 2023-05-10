package edu.poh.samsung_project_final.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.List;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.data.data_sources.room.entities.StockEntity;
import edu.poh.samsung_project_final.data.data_sources.room.entities.UserEntity;
import edu.poh.samsung_project_final.databinding.FragmentBuyingAStockBinding;
import edu.poh.samsung_project_final.databinding.FragmentFavouritesOfCharacterBinding;
import edu.poh.samsung_project_final.ui.adapters.FavouritesAdapter;
import edu.poh.samsung_project_final.ui.adapters.StockAdapter;
import edu.poh.samsung_project_final.ui.view_models.StockDataViewModel;
import edu.poh.samsung_project_final.ui.view_models.UserViewModel;


public class favourites_of_character extends Fragment implements FavouritesAdapter.ListenerFavourites {
    private NavHostFragment navHostFragment;
    private NavController navController;
    private FavouritesAdapter adapter;
    private final String KEY_ID = "1";
    private final String CHECK = "2";
    private final String COUNT_ID = "3";
    private final String CHECK_STRING = "true";
    private StockDataViewModel viewModel;
    private FragmentFavouritesOfCharacterBinding binding;
    private UserViewModel userViewModel;
    private StockDataViewModel stockDataViewModel;
    private double balance;

    public static favourites_of_character newInstance() { return null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouritesOfCharacterBinding.inflate(inflater, container, false);
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        adapter = new FavouritesAdapter(this);
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        stockDataViewModel = new ViewModelProvider(getActivity()).get(StockDataViewModel.class);
        balance = userViewModel.getMoney();
        userViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<UserEntity>() {
            @Override
            public void onChanged(UserEntity userEntity) {
                stockDataViewModel.getIdOfStock().observe(getViewLifecycleOwner(), new Observer<List<StockEntity>>() {
                    @Override
                    public void onChanged(List<StockEntity> stockEntities) {
                    }
                });
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initStockRecyclerView();
        updateData();
    }


    private void initStockRecyclerView(){
        binding.stockRecyclerViewFavourites.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.stockRecyclerViewFavourites.setAdapter(adapter);
        StockDataViewModel stockDataViewModel = new ViewModelProvider(this).get(StockDataViewModel.class);
        viewModel = stockDataViewModel;
    }
    private void updateData(){
        viewModel.getIdOfStock().observe(getViewLifecycleOwner(), new Observer<List<StockEntity>>() {
            @SuppressLint("SetTextI18n")
            public void onChanged(List<StockEntity> stockEntities) {
                adapter.setBalance(balance);
                adapter.setContext(getContext());
                adapter.setList(stockEntities);
            }
        });
    }

    @Override
    public void OnClickFavourites(StockEntity stockEntity) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ID,stockEntity.id_of_stock);
        bundle.putString(CHECK,CHECK_STRING);
        bundle.putString(COUNT_ID, String.valueOf(stockEntity.quantity_of_stock_ent));
        stock_page fragment = new stock_page();
        fragment.setArguments(bundle);
        navController.navigate(R.id.action_favourites_of_character_to_stock_page, bundle);
    }
}