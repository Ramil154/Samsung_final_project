package edu.poh.samsung_project_final.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.data.models.stockSearchModel;
import edu.poh.samsung_project_final.databinding.FragmentStockPageBinding;
import edu.poh.samsung_project_final.databinding.FragmentStockSearchBinding;

public class stock_page extends Fragment {
    private FragmentStockPageBinding binding;

    public static stock_page newInstance() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStockPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String id = getArguments().getString("1");
        if (id != null) {
            parseStockDataCost(id);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setStockAndCost(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject obj = jsonObject.getJSONObject("securities");
        JSONArray data = obj.getJSONArray("data");
        JSONArray data_next = data.getJSONArray(0);
        String name = data_next.getString(9);
        Double cost_d = data_next.optDouble(15);
        String cost_str = cost_d.toString();
        binding.stockSPageName.setText(name);
        binding.stockSPageCost.setText(cost_str + " руб");
    }

    private void parseStockDataCost(String id) {
        String url = "https://iss.moex.com/iss/engines/stock/markets/shares/securities/"+id+".json";
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
}