package edu.poh.samsung_project_final.ui.ui.view_models;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.io.Closeable;
import java.util.ArrayList;

import edu.poh.samsung_project_final.ui.data.models.parseStockInfoModel;
import edu.poh.samsung_project_final.ui.data.models.stockSearchModel;

public class stockSearchViewModel extends AndroidViewModel implements parseStockInfoModel.OnDataLoadedListener{
    private parseStockInfoModel InfoModel;
    @SuppressLint("StaticFieldLeak")
    public MutableLiveData<ArrayList<parseStockInfoModel>> LiveDataListForStocks;
    public stockSearchViewModel(@NonNull Application application) {
        super(application);
        LiveDataListForStocks = new MutableLiveData<>();
        InfoModel = new parseStockInfoModel();
    }

    public MutableLiveData<ArrayList<parseStockInfoModel>> getLiveDataListForStocks() {
        return LiveDataListForStocks;
    }

    public void setLiveDataListForStocks(Context context){
        InfoModel.setListener(this);
        LiveDataListForStocks.setValue(InfoModel.getList(context));
    }

    @Override
    public void onDataLoaded(ArrayList<parseStockInfoModel> data) {
        LiveDataListForStocks.setValue(data);
    }
}
