package edu.poh.samsung_project_final.ui.ui;

import static java.lang.Math.abs;
import static java.lang.Math.log;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import edu.poh.samsung_project_final.ui.data.DataLoadCallback;
import edu.poh.samsung_project_final.ui.data.models.parseStockInfoModel;
import edu.poh.samsung_project_final.ui.ui.view_models.stockSearchViewModel;

public class stock_page extends Fragment{
    private FragmentStockPageBinding binding;
    private final String KEY_ID = "1";
    private final String CHECK = "2";
    private final String COUNT_ID = "3";
    private final String CHECK_STRING = "true";
    private NavHostFragment navHostFragment;
    private stockSearchViewModel model;
    private NavController navController;
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
        model = new ViewModelProvider(getActivity()).get(stockSearchViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.cardOfGraphics.setVisibility(View.INVISIBLE);
        binding.progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(requireContext(), R.color.Blue_400), PorterDuff.Mode.SRC_IN);
        binding.progressBar.setVisibility(View.VISIBLE);
        graphics.clear();
        Dates_onGraph.clear();
        final String[] cost = {" "};
        Bundle args = getArguments();
        String id = args.getString(KEY_ID);
        model.setAllForStockPage(id, requireContext(), new DataLoadCallback() {
            @Override
            public void onDataLoaded() {
                for (parseStockInfoModel parse: model.stock_page_list){
                    Dates_onGraph.addAll(model.Dates_onGraph);
                    graphics.addAll(model.graphics);
                    cost[0] = parse.stock_page_cost_of_stock;
                    Log.d("StockPage","this");
                    binding.stockCostInGraphicsDate.setText(parse.stock_page_cost_of_stock);
                    System.out.println("BINDING   " + parse.stock_page_name_of_stock);
                    binding.stockSPageName.setText(parse.stock_page_name_of_stock);
                    binding.plusOfStockDateAndPercent.setTextColor(Color.parseColor(parse.stock_page_colour));
                    binding.plusOfStockDateAndPercent.setText(parse.stock_page_persent_str);


                }
            }
        });
        String check = args.getString(CHECK);
        String quantity = args.getString(COUNT_ID);
        if(!check.equals(CHECK_STRING)){
            binding.ButtonForChoseToDeleteStock.setVisibility(View.GONE);
        }
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setGraph(binding.graphicOfStockMonth,graphics,"Цена акций за месяц", cost[0]);
                binding.progressBar.setVisibility(View.GONE);
                binding.cardOfGraphics.setVisibility(View.VISIBLE);
            }
        },800);
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
    private void setGraph(LineChart graph, List<Entry> data, String label, String cost){
        // Настройка данных графика
        LineDataSet dataSet = new LineDataSet(data, label);
        dataSet.setColor(R.color.Blue_400);
        dataSet.setCircleColor(R.color.Blue_400);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);
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
                float xValue = e.getX();
                int x = Math.round(xValue);
                float yValue = e.getY();
                binding.plusOfStockDateAndPercent.setVisibility(View.INVISIBLE);
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


}