package edu.poh.samsung_project_final.ui;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.databinding.FragmentStockPageBinding;

public class stock_page extends Fragment{
    private FragmentStockPageBinding binding;
    private final String KEY_ID = "1";
    private final String CHECK = "2";
    private final String COUNT_ID = "3";
    private final String CHECK_STRING = "true";
    private NavHostFragment navHostFragment;
    private String id;
    private float cost_first_in_data;
    private NavController navController;
    private boolean flag_graph = false;
    private String cost;
    private List<Entry> graphics = new ArrayList<>();
    private List<String> Dates_onGraph = new ArrayList<>();
    public static stock_page newInstance() {
        return null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentStockPageBinding.inflate(inflater, container, false);
        navHostFragment = (NavHostFragment) requireActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        graphics.clear();
        cost_first_in_data = 0;
        Dates_onGraph.clear();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // Обратите внимание, что месяцы начинаются с 0
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String searchDate = year + "-0" + month + "-" + day;
        Bundle args = getArguments();
        String check = args.getString(CHECK);
        String quantity = args.getString(COUNT_ID);
        if(!check.equals(CHECK_STRING)){
            binding.ButtonForChoseToDeleteStock.setVisibility(View.GONE);
        }
        id = args.getString(KEY_ID);
        getResult(id,searchDate);
        parseStockDataCost(id);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("MyWay",graphics.toString());
                setGraph(binding.graphicOfStockMonth,graphics,"Цена акций за месяц");
            }
        },500);
        binding.ButtonForChoseToByuingStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(KEY_ID,id);
                buying_a_stock fragment = new buying_a_stock();
                fragment.setArguments(bundle);
                navController.navigate(R.id.action_stock_page_to_buying_a_stock,bundle);
            }
        });
        binding.ButtonForChoseToDeleteStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(KEY_ID,id);
                bundle.putString(COUNT_ID,quantity);
                DeleteStockFromFav fragment = new DeleteStockFromFav();
                fragment.setArguments(bundle);
                navController.navigate(R.id.action_stock_page_to_delete_stock_from_fav,bundle);
            }
        });
    }
    private void setGraph(LineChart graph, List<Entry> data, String label){
        // Настройка данных графика
        LineDataSet dataSet = new LineDataSet(data, label);
        dataSet.setColor(R.color.Blue_400); // Задаем цвет линии
        dataSet.setCircleColor(R.color.Blue_400); // Задаем цвет точек
        dataSet.setLineWidth(2f); // Задаем толщину линии
        dataSet.setCircleRadius(4f); // Задаем радиус точек
        dataSet.setDrawValues(false); // Отключаем отображение значений точек
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        graph.setData(lineData);

        // Настройка отображения графика
        graph.getLegend().setEnabled(false);
        graph.getDescription().setEnabled(false);
        graph.animateX(500);
        graph.setScaleEnabled(false); // Запрещаем изменение масштаба графика
        graph.setPinchZoom(false);

        graph.getXAxis().setDrawLabels(false);
        graph.getAxisRight().setDrawLabels(false);

        // Добавление надписи
        Description description = new Description();
        description.setText("График цены за месяц");
        description.setTextColor(Color.BLACK);
        description.setTextSize(18f);
        description.setTextAlign(Paint.Align.CENTER);
        description.setPosition(graph.getWidth() / 2f, graph.getHeight() - 20f); // Размещаем надпись внизу по центру
        graph.setDescription(description);

        graph.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // Получаем информацию о выбранной точке
                float xValue = e.getX();
                int x = Math.round(xValue);
                float yValue = e.getY();
                //String date = Dates_onGraph.get(xValue);
                //Toast.makeText(getContext(), "Значение: " + yValue + " "+ x, Toast.LENGTH_SHORT).show();
                binding.plusOfStockDateAndPercent.setVisibility(View.GONE);
                binding.stockCostInGraphicsDate.setText(yValue + "руб - " + Dates_onGraph.get(x));
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onNothingSelected() {
                binding.plusOfStockDateAndPercent.setVisibility(View.VISIBLE);
                binding.stockCostInGraphicsDate.setText(cost + " руб");
            }
        });

        graph.invalidate();
    }

    private void getResult(String id, String searchDate){
        String url = "https://iss.moex.com/iss/history/engines/stock/markets/shares/securities/"+id+".json?from="+searchDate;
        RequestQueue queque = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            parseHistory(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("MyWay","Error");
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

    private void parseHistory(String response) throws JSONException{
        JSONObject obj = new JSONObject(response);
        JSONObject history = obj.getJSONObject("history");
        JSONArray data = history.getJSONArray("data");
        int counter = 0;
        for (int i = 0; i < data.length(); i++){
            JSONArray mas = data.getJSONArray(i);
            Double cost_history = mas.optDouble(9);
            String date = mas.getString(1);
            if(Double.isNaN(cost_history)){}
            else{
                String cost =  cost_history.toString();
                float cost_f = Float.parseFloat(cost);
                if (!flag_graph){
                    cost_first_in_data = cost_f;
                    flag_graph = true;
                }
                Log.d("MyWay", String.valueOf(cost_f));
                graphics.add(new Entry(counter,cost_f));
                counter++;
                Dates_onGraph.add(date);
            }
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setStockAndCost(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONObject obj = jsonObject.getJSONObject("securities");
        JSONArray data = obj.getJSONArray("data");
        for (int i = 0;i < data.length(); i++){
            JSONArray data_next = data.getJSONArray(i);
            String boardid = data_next.getString(1);
            if (!boardid.equals("TQBR")){
                continue;
            }
            String name = data_next.getString(9);
            Double cost_d = data_next.optDouble(3);
            cost = cost_d.toString();
            binding.stockSPageName.setText(name);
            binding.stockCostInGraphicsDate.setText(cost + " руб");
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    double ans = cost_d - cost_first_in_data;
                    String answer;
                    if (cost_d < 5){
                        answer = String.format("%.4f",abs(ans));
                    }
                    else{
                        answer = String.format("%.2f",abs(ans));
                    }
                    double percent_of_diff = ((ans) / cost_d) * 100.0;
                    if (percent_of_diff < 0.0){
                        binding.plusOfStockDateAndPercent.setTextColor(Color.parseColor("#D41307"));
                        String persent_str = String.format("%.2f",abs(percent_of_diff));
                        binding.plusOfStockDateAndPercent.setText("- " + answer + " руб" + " " + persent_str+" %");
                    }
                    else{
                        binding.plusOfStockDateAndPercent.setTextColor(Color.parseColor("#07D434"));
                        String persent_str = String.format("%.2f",percent_of_diff);
                        binding.plusOfStockDateAndPercent.setText("+ " + answer + " руб" + " " + persent_str+" %");
                    }
                }
            },500);
            //binding.plusOfStockDateAndPercent.setText(String.format("%.2f",cost_first_in_data - cost_d) + "руб");
        }
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


}