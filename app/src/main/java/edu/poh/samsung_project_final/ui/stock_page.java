package edu.poh.samsung_project_final.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.data.models.stockSearchModel;
import edu.poh.samsung_project_final.databinding.FragmentStockPageBinding;
import edu.poh.samsung_project_final.databinding.FragmentStockSearchBinding;
import edu.poh.samsung_project_final.ui.adapters.StockAdapter;

public class stock_page extends Fragment implements StockAdapter.Listener {
    private FragmentStockPageBinding binding;
    private final String KEY_ID = "1";
    private NavHostFragment navHostFragment;
    private NavController navController;
    public static stock_page newInstance() {
        return null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStockPageBinding.inflate(inflater, container, false);
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        Bundle args = getArguments();
        String id = args.getString(KEY_ID);
        parseStockDataCost(id);
        binding.ButtonForChoseToByuingStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_stock_page_to_buying_a_stock);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @SuppressLint("SetTextI18n")
    private void setStockAndCost(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject obj = jsonObject.getJSONObject("securities");
        JSONArray data = obj.getJSONArray("data");
        JSONArray data_next = data.getJSONArray(0);
        String name = data_next.getString(9);
        Double cost_d = data_next.optDouble(3);
        String cost_str = cost_d.toString();
        binding.stockSPageName.setText(name);
        binding.stockSPageCost.setText(cost_str + " руб");
    }

    private void parseStockDataCost(String sid) {
        Log.d("MyLog",sid);
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
    public void onClickNow(stockSearchModel item) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ID,item.id_of_stock);
        Log.d("MyLog",item.id_of_stock);
        stock_page fragment = new stock_page();
        fragment.setArguments(bundle);
        navController.navigate(R.id.action_stock_search_to_stock_page, bundle);
    }
}