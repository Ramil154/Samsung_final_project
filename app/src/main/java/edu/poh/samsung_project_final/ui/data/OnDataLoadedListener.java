package edu.poh.samsung_project_final.ui.data;

import java.util.ArrayList;

import edu.poh.samsung_project_final.ui.data.models.parseStockInfoModel;

public interface OnDataLoadedListener {
    void onDataLoaded(ArrayList<parseStockInfoModel> data);
}
