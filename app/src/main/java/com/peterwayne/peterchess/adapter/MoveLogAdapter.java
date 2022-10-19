package com.peterwayne.peterchess.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.peterwayne.peterchess.R;
import com.peterwayne.peterchess.activities.MainActivity;
import com.peterwayne.peterchess.engine.board.Move;
import com.peterwayne.peterchess.gui.GameUI;
import com.peterwayne.peterchess.pattern.MyObserver;
import java.util.List;

public class MoveLogAdapter extends RecyclerView.Adapter<MoveLogAdapter.ViewHolder> implements MyObserver {
    private final Context context;
    private List<Move> moveLog;
    private int selectedPosition = 0;
    public MoveLogAdapter(Context context) {
        this.context = context;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Move> data)
    {
        moveLog = data;
        notifyDataSetChanged();
        ((MainActivity) context).getMoveHistoryUI().scrollToPosition(getItemCount()-1);
    }
    @NonNull
    @Override
    public MoveLogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_move,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoveLogAdapter.ViewHolder holder, int position) {
        Move move = moveLog.get(position);
        if(position%2==0)
        {
            String moveOrderText = (position/2 +1) + ".";
            holder.txtOrder.setText(moveOrderText);
            holder.txtOrder.setVisibility(View.VISIBLE);
        }else {
            holder.txtOrder.setVisibility(View.GONE);
        }
        holder.txtMove.setText(move.toString());
        holder.itemView.setBackgroundColor(selectedPosition == position ? Color.GRAY : Color.TRANSPARENT);
    }

    @Override
    public int getItemCount() {
        if(moveLog!=null) return moveLog.size();
         return 0;
    }

    @Override
    public void update(Object moveLog) {
        if(moveLog instanceof GameUI.MoveLog)
        {
            this.setData(((GameUI.MoveLog) moveLog).getMoves());
            selectedPosition = getItemCount()-1;
        }

    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final TextView txtOrder;
        private final TextView txtMove;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrder = itemView.findViewById(R.id.txt_order);
            txtMove = itemView.findViewById(R.id.txt_move);
        }

        @Override
        public void onClick(View view) {
        }
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }
    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public List<Move> getMoveLog() {
        return moveLog;
    }
}
