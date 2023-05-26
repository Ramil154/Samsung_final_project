package edu.poh.samsung_project_final.ui.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.ui.data.DataLoadCallback;
import edu.poh.samsung_project_final.ui.data.room.entities.StockEntity;
import edu.poh.samsung_project_final.ui.data.room.entities.UserEntity;
import edu.poh.samsung_project_final.ui.data.models.UserInfoModel;
import edu.poh.samsung_project_final.databinding.FragmentLogoBinding;
import edu.poh.samsung_project_final.ui.ui.view_models.StockDataViewModel;
import edu.poh.samsung_project_final.ui.ui.view_models.UserViewModel;
import edu.poh.samsung_project_final.ui.ui.view_models.ValutesViewModel;

public class LogoFragment extends Fragment {
    private FragmentLogoBinding binding;
    private NavHostFragment navHostFragment;
    private NavController navController;
    private UserViewModel userViewModel;
    public static UserInfoModel userInfoModel;
    private ValutesViewModel valutesViewModel;
    private StockDataViewModel stockDataViewModel;
    private final String NOT_LOG = "Not correct log";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLogoBinding.inflate(inflater, container, false);
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        stockDataViewModel = new ViewModelProvider(getActivity()).get(StockDataViewModel.class);
        valutesViewModel = new ViewModelProvider(this).get(ValutesViewModel.class);
        userInfoModel = new UserInfoModel();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isNetworkAvailable()) {
            Toast.makeText(getContext(),"Подключитесь к интернету, чтобы продолжить работу в приложении Chill Invest",Toast.LENGTH_SHORT).show();
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().finish();
                }
            }, 1500);
        }
        else {
            userViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<UserEntity>() {
                @Override
                public void onChanged(UserEntity userEntity) {
                    if (userEntity == null) {
                        navController.navigate(R.id.action_logoFragment_to_enter);
                    } else {
                        userViewModel.uploadUserDataFromFireBase(requireContext(),new DataLoadCallback() {
                            @Override
                            public void onDataLoaded() {
                                try{
                                    if (userViewModel.userEntity.login.equals(NOT_LOG)){
                                        navController.navigate(R.id.action_logoFragment_to_rememberLogin);
                                    }
                                    else {
                                        List<Double> prices_of_stocks_when_bought = stockDataViewModel.getPricesOfAllStocks();
                                        for (int i = 0; i < prices_of_stocks_when_bought.size(); i++) {
                                            userInfoModel.all_stock_price_bought += prices_of_stocks_when_bought.get(i);
                                        }
                                        stockDataViewModel.getIdOfStock().observe(getViewLifecycleOwner(), new Observer<List<StockEntity>>() {
                                            @Override
                                            public void onChanged(List<StockEntity> stockEntities) {
                                                for (int i = 0; i < stockEntities.size(); i++) {
                                                    StockEntity stock = stockEntities.get(i);
                                                    valutesViewModel.getOnlineCost(requireContext(), stock.id_of_stock, stock.quantity_of_stock_ent, new DataLoadCallback() {
                                                        @Override
                                                        public void onDataLoaded() {
                                                            userInfoModel.all_stock_price_online += valutesViewModel.getCostOnlineStocks();
                                                        }
                                                    });
                                                }
                                                new android.os.Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        navController.navigate(R.id.action_logoFragment_to_main_list_of_app);
                                                    }
                                                }, 2000);
                                                //navController.navigate(R.id.action_logoFragment_to_main_list_of_app);
                                            }
                                        });
                                    }
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
        }
    }
    //userInfoModel.all_stock_price_online += cost_final;
    public UserInfoModel getUserInfoModel(){
        return userInfoModel;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}