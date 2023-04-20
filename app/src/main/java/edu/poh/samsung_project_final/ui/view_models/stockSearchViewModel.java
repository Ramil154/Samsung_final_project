package edu.poh.samsung_project_final.ui.view_models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import edu.poh.samsung_project_final.data.models.stockSearchModel;

public class stockSearchViewModel extends ViewModel {
    MutableLiveData<List<stockSearchModel>> LiveDataListForStocks;
}
