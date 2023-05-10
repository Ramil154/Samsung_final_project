package edu.poh.samsung_project_final.ui.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.data.data_sources.room.entities.StockEntity;
import edu.poh.samsung_project_final.data.models.ValutesModel;

public class ValutesAdapter extends RecyclerView.Adapter<ValutesAdapter.ValuteDataHolder> {
    private List<ValutesModel> list = new ArrayList();
    @NonNull
    @Override
    public ValutesAdapter.ValuteDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_of_valutes, parent, false);
        return new ValuteDataHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ValutesAdapter.ValuteDataHolder holder, int position) {
        ValutesModel model = this.list.get(position);
        holder.name_valute.setText(model.name_valute);
        holder.cost_valute.setText(model.cost_valute + " руб");
        holder.id_valute.setText(model.id_valute);
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public class ValuteDataHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView name_valute;
        private final TextView id_valute;
        private final TextView cost_valute;
        public ValuteDataHolder(@NonNull View itemView) {
            super(itemView);
            name_valute = (TextView) itemView.findViewById(R.id.which_valute_is_here);
            id_valute = (TextView) itemView.findViewById(R.id.ID_of_valutes_which_is_here);
            cost_valute = (TextView) itemView.findViewById(R.id.cost_of_valutes_which_is_here);

        }

        @Override
        public void onClick(View v) {

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<ValutesModel> list){
        Log.d("MyLon",list.toString());
        this.list = list;
        notifyDataSetChanged();
    }
}
