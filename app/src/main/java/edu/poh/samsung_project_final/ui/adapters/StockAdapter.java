package edu.poh.samsung_project_final.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import edu.poh.samsung_project_final.R;
import edu.poh.samsung_project_final.data.models.stockSearchModel;
import edu.poh.samsung_project_final.databinding.ListItemOfStockBinding;

public class StockAdapter extends ListAdapter<stockSearchModel, StockAdapter.StockHolder> {
    Listener listener;
    public StockAdapter(@NonNull StockComparator stockComparator, Listener listener) {
        super(stockComparator);
        this.listener = listener;
    }

    @NonNull
    @Override
    public StockHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_of_stock, parent, false);
        return new StockHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StockHolder holder, int position) {
        holder.bind(getItem(position),listener);

    }

    static class StockHolder extends RecyclerView.ViewHolder {
        public StockHolder(@NonNull View view) {
            super(view);
        }
        ListItemOfStockBinding binding = ListItemOfStockBinding.bind(itemView);
        @SuppressLint("SetTextI18n")
        private void bind(stockSearchModel item, Listener listener){
            if (item.name_of_stock.length() > 12){
                binding.whichStockIsHere.setText(item.name_of_stock.substring(0,12)+"...");
            }else{
                binding.whichStockIsHere.setText(item.name_of_stock);
            }
            binding.costOfStockWhichIsHere.setText(item.cost_of_stock+" руб");
            binding.cardViewRecycler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickNow(item);
                }
            });
        }
    }
    public static class StockComparator extends DiffUtil.ItemCallback<stockSearchModel>{

        @Override
        public boolean areItemsTheSame(@NonNull stockSearchModel oldItem, @NonNull stockSearchModel newItem) {
            return oldItem == newItem;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull stockSearchModel oldItem, @NonNull stockSearchModel newItem) {
            return oldItem == newItem;
        }
    }
    public interface Listener{
        void onClickNow(stockSearchModel item);
    }


}
