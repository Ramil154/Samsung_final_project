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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.data.data_sources.room.entities.StockEntity;
import edu.poh.samsung_project_final.data.data_sources.room.entities.UserEntity;
import edu.poh.samsung_project_final.databinding.FragmentBuyingAStockBinding;
import edu.poh.samsung_project_final.databinding.FragmentMainListOfAppBinding;
import edu.poh.samsung_project_final.ui.view_models.StockDataViewModel;
import edu.poh.samsung_project_final.ui.view_models.UserViewModel;


public class main_list_of_app extends Fragment {
    private FragmentMainListOfAppBinding binding;
    private NavHostFragment navHostFragment;
    private UserViewModel userViewModel;
    private NavController navController;
    public static main_list_of_app newInstance() {return null;}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainListOfAppBinding.inflate(inflater, container, false);
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<UserEntity>() {
            @SuppressLint("SetTextI18n")
            public void onChanged(UserEntity userEntity) {
                if(userEntity == null){
                    binding.userProfileMain.setText("Здравствуйте, ");
                }
                else{
                    binding.userProfileMain.setText("Здравствуйте, " + userEntity.login);
                    binding.sumOfStocks.setText(userEntity.money + " руб");
                    Log.d("UserMoney",String.valueOf(userEntity.money)+" main");
                }
            }
        });

//        stockDataViewModel.getIdOfStock().observe(getViewLifecycleOwner(),new Observer<List<StockEntity>>() {
//            @SuppressLint("SetTextI18n")
//            public void onChanged(List<StockEntity> stockEntities) {
//                if (stockEntities.isEmpty()){
//                    binding.sumOfStocks.setText("0 руб");
//                }
//                else{
//                    for(int i = 0;i < stockEntities.size(); i++){
//                        StockEntity this_stock_entity = stockEntities.get(i);
//                        all_cost_stock += this_stock_entity.stock_price_when_bought;
//
//                    }
//                    binding.sumOfStocks.setText(all_cost_stock + " руб");
//                }
//
//            }
//        });

        binding.GoToFavouritesFromMainByTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_main_list_of_app_to_favourites_of_character);
            }
        });

    }
}