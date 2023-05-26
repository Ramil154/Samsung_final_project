package edu.poh.samsung_project_final.ui.ui.view_models;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.github.mikephil.charting.data.Entry;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import edu.poh.samsung_project_final.ui.data.DataLoadCallback;
import edu.poh.samsung_project_final.ui.data.models.UserInfoModel;
import edu.poh.samsung_project_final.ui.data.models.parseStockInfoModel;
import edu.poh.samsung_project_final.ui.data.models.stockSearchModel;

public class stockSearchViewModel extends AndroidViewModel implements parseStockInfoModel.OnDataLoadedListener{
    private parseStockInfoModel InfoModel;
    @SuppressLint("StaticFieldLeak")
    public MutableLiveData<ArrayList<parseStockInfoModel>> LiveDataListForStocks;
    public List<parseStockInfoModel> stock_page_list;
    public List<String> Dates_onGraph;
    public List<Entry> graphics;
    public stockSearchViewModel(@NonNull Application application) {
        super(application);
        LiveDataListForStocks = new MutableLiveData<>();
        Dates_onGraph = new ArrayList<>();
        stock_page_list = new ArrayList<>();
        graphics = new ArrayList<>();
        InfoModel = new parseStockInfoModel();
    }

    public MutableLiveData<ArrayList<parseStockInfoModel>> getLiveDataListForStocks() {
        return LiveDataListForStocks;
    }

    public void setLiveDataListForStocks(Context context){
        InfoModel.setListener(this);
        InfoModel.getList(context);
    }

    public void setAllForStockPage(String id, Context context, DataLoadCallback callback){
        InfoModel.setListener(this);
        InfoModel.getAllFroGraph(id,context, callback);
    }

    public void setAllForBuying(String id, Context context, DataLoadCallback callback){
        InfoModel.setListener(this);
        InfoModel.getAllForBuying(id,context, callback);
    }

    @Override
    public void onDataLoaded(ArrayList<parseStockInfoModel> data) {
        LiveDataListForStocks.setValue(data);
    }

    @Override
    public void onDataLoadedStockPage(List<parseStockInfoModel> list_stock_page, DataLoadCallback callback) {
        stock_page_list.clear();
        stock_page_list.addAll(list_stock_page);
        callback.onDataLoaded();
    }

    @Override
    public void onDataLoadedDates(List<String> Dates) {
        Dates_onGraph.clear();
        Dates_onGraph.addAll(Dates);
    }

    @Override
    public void onDataLoadedGraphs(List<Entry> graphics) {
        this.graphics.clear();
        this.graphics.addAll(graphics);
    }

    public void buyingStock(Activity activity, Double cost_d, String name_of_stock, String id_of_stock, List<String> AllID, int count_int, UserViewModel userViewModel, StockDataViewModel stockDataViewModel, UserInfoModel userInfoModel){
        InfoModel.buyingStock(activity,cost_d,name_of_stock,id_of_stock,AllID,count_int,userViewModel,stockDataViewModel,userInfoModel);
    }
}
