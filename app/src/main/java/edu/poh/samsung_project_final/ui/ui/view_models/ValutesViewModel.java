package edu.poh.samsung_project_final.ui.ui.view_models;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import edu.poh.samsung_project_final.ui.data.DataLoadCallback;
import edu.poh.samsung_project_final.ui.data.models.ValutesModel;

public class ValutesViewModel extends ViewModel implements ValutesModel.OnDataLoadedListenerMainFr {
    public MutableLiveData<List<ValutesModel>> LiveDataListForValutes;
    private ValutesModel InfoModel;
    private double cost_of_all_stock_online;

    public ValutesViewModel() {
        LiveDataListForValutes = new MutableLiveData<>();
        InfoModel = new ValutesModel();
    }

    public MutableLiveData<List<ValutesModel>> getLiveDataListForStocks() {
        return LiveDataListForValutes;
    }

    @Override
    public void onDataLoadedValutes(List<ValutesModel> list_stock_page, DataLoadCallback callback) {
        LiveDataListForValutes.setValue(list_stock_page);
        callback.onDataLoaded();
    }

    public void getOnlineCost(Context context,String sid,Integer quantity, DataLoadCallback callback){
        InfoModel.setListener(this);
        InfoModel.parseStockDataCostMain(context,sid,quantity,callback);
    }
    @Override
    public void onDataLoadedCostOnline(double cost_of_all_stock_online, DataLoadCallback callback) {
        this.cost_of_all_stock_online = cost_of_all_stock_online;
        callback.onDataLoaded();
    }

    public void parseValutes(Context context, DataLoadCallback callback){
        InfoModel.setListener(this);
        InfoModel.parseValuteData(context, callback);
    }
    public double getCostOnlineStocks(){
        return cost_of_all_stock_online;
    }
}
