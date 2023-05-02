package edu.poh.samsung_project_final.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.databinding.DeleteStockFromFavBinding;
import edu.poh.samsung_project_final.databinding.FragmentBuyingAStockBinding;
import edu.poh.samsung_project_final.ui.view_models.StockDataViewModel;
import edu.poh.samsung_project_final.ui.view_models.UserViewModel;

public class DeleteStockFromFav extends Fragment {
    DeleteStockFromFavBinding binding;
    private NavHostFragment navHostFragment;
    private NavController navController;
    private UserViewModel userViewModel;
    private StockDataViewModel stockDataViewModel;
    private String id_of_stock;
    private final String KEY_ID = "1";
    private String name_of_stock;
    private String cost_of_stock;
    private double cost_d;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DeleteStockFromFavBinding.inflate(inflater, container, false);
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        userViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DeleteStockFromFav.super.onViewCreated(view, savedInstanceState);
        this.stockDataViewModel = new ViewModelProvider(this).get(StockDataViewModel.class);
        Bundle args = getArguments();
        id_of_stock = args.getString(KEY_ID);
        parseStockDataCost(id_of_stock);
    }

    private void parseStockDataCost(String sid) {
        String url = "https://iss.moex.com/iss/engines/stock/markets/shares/securities/"+sid+".json";
        RequestQueue queque = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            setStockAndCost(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("MyWay", "VolleyError:" + error.toString());
            }
        });
        queque.add(stringRequest);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setStockAndCost(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject obj = jsonObject.getJSONObject("securities");
        JSONArray data = obj.getJSONArray("data");
        for (int i = 0; i < data.length();i++){
            JSONArray data_next = data.getJSONArray(i);
            String boardid = data_next.getString(1);
            if (!boardid.equals("TQBR")){
                continue;
            }
            name_of_stock = data_next.getString(9);
            cost_d = data_next.optDouble(3);
            if(cost_d < 10.0){
                cost_of_stock = String.format("%.3f",cost_d);
            }
            else if (cost_d < 100.0){
                cost_of_stock = String.format("%.2f",cost_d);
            }
            else{
                cost_of_stock = String.format("%.1f",cost_d);
            }
            binding.stocksDeleteName.setText(name_of_stock);
            binding.stocksDeleteCost.setText(cost_of_stock + " руб");
        }

    }

}