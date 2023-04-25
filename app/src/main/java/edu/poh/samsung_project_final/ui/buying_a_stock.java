package edu.poh.samsung_project_final.ui;

import android.annotation.SuppressLint;
import android.app.Application;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.EmptyStackException;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.data.data_sources.room.entities.StockEntity;
import edu.poh.samsung_project_final.databinding.FragmentBuyingAStockBinding;
import edu.poh.samsung_project_final.ui.view_models.StockDataViewModel;


public class buying_a_stock extends Fragment {
    private FragmentBuyingAStockBinding binding;
    private final String KEY_ID = "1";
    private NavHostFragment navHostFragment;
    private NavController navController;
    private StockDataViewModel stockDataViewModel;
    private String name_of_stock;
    private String cost_of_stock;
    private String id_of_stock;
    public static buying_a_stock newInstance() {
        return null;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBuyingAStockBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setStockAndCost(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject obj = jsonObject.getJSONObject("securities");
        JSONArray data = obj.getJSONArray("data");
        JSONArray data_next = data.getJSONArray(0);
        name_of_stock = data_next.getString(9);
        Double cost_d = data_next.optDouble(3);
        if(cost_d < 1.0){
            cost_of_stock = String.format("%.3f",cost_d);
        }
        else if (cost_d < 10.0){
            cost_of_stock = String.format("%.2f",cost_d);
        }
        else{
            cost_of_stock = String.format("%.1f",cost_d);
        }
        binding.stockSBuyingName.setText(name_of_stock);
        binding.stockSBuyingCost.setText(cost_of_stock + " руб");
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        buying_a_stock.super.onViewCreated(view, savedInstanceState);
        this.stockDataViewModel = new ViewModelProvider(this).get(StockDataViewModel.class);
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        Bundle args = getArguments();
        id_of_stock= args.getString(KEY_ID);
        parseStockDataCost(id_of_stock);
        binding.GoToFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String count = binding.gettingCountOfStocks.getText().toString();
                    char symbol = count.charAt(0);
                    if (symbol == '0' || symbol == '.' || symbol == ','){
                        throw new NumberFormatException();
                    }
                    int count_int = Integer.parseInt(count);
                    if (count_int > 99){
                        throw new EmptyStackException();
                    }
                    Log.d("MyLog",id_of_stock +"  " + count_int);
                    stockDataViewModel.insertStock(new StockEntity(id_of_stock,Integer.parseInt(count),name_of_stock));
                    navController.navigate(R.id.action_buying_a_stock_to_favourites_of_character);
                } catch (NumberFormatException e) {
                    Toast.makeText(buying_a_stock.this.getActivity(), "В поле «Введите количество акций» вы ввели не число или не целое число", Toast.LENGTH_SHORT).show();
                }
                catch (StringIndexOutOfBoundsException exception){
                    Toast.makeText(buying_a_stock.this.getActivity(), "В поле «Введите количество акций» вы ничего не ввели", Toast.LENGTH_SHORT).show();
                }
                catch (EmptyStackException except){
                    Toast.makeText(buying_a_stock.this.getActivity(), "В поле «Введите количество акций» вы ввели слишком большое число", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}