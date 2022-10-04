package com.peterwayne.peterchess.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import com.peterwayne.peterchess.model.MovePair;
import com.peterwayne.peterchess.pattern.MyObserver;
import java.util.ArrayList;
import java.util.List;

public class MoveLogAdapter extends RecyclerView.Adapter<MoveLogAdapter.ViewHolder> implements MyObserver {
    private Context context;
    private List<MovePair> movePairs;

    public MoveLogAdapter(Context context) {
        this.context = context;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<MovePair> data)
    {
        movePairs = data;
        notifyDataSetChanged();

    }
    @NonNull
    @Override
    public MoveLogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_move_pair,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoveLogAdapter.ViewHolder holder, int position) {
        MovePair movePair = movePairs.get(position);
        String moveOrderText = movePair.getOrder() + ".";
        holder.txtMoveOrder.setText(moveOrderText);
        if(movePair.getWhiteMove()!=null)
        {
            holder.txtWhiteMove.setText(movePair.getWhiteMove().toString());
        }
        if(movePair.getBlackMove()!=null)
        {
            holder.txtBlackMove.setText(movePair.getBlackMove().toString());
        }
        ((MainActivity) context).getMoveHistoryUI().smoothScrollToPosition(getItemCount()-1);
    }

    @Override
    public int getItemCount() {
        if(movePairs!=null) return movePairs.size();
         return 0;
    }

    @Override
    public void update(Object moveLog) {
        if(moveLog instanceof GameUI.MoveLog)
        this.setData(convertMoveLogToMovePairs((GameUI.MoveLog)moveLog));
    }

    private List<MovePair> convertMoveLogToMovePairs(GameUI.MoveLog moveLog) {
        List<MovePair> res = new ArrayList<>();
        List<Move> moveList = moveLog.getMoves();
        for(int i= 0; i<moveList.size(); i=i+2) {
            MovePair pair;
            if(i+1<moveList.size())
            {
                pair = new MovePair((i/2)+1, moveList.get(i),moveList.get(i+1));

            }else
            {
                pair = new MovePair((i/2)+1, moveList.get(i),null);
            }
            res.add(pair);

        }
        return res;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView txtMoveOrder, txtWhiteMove, txtBlackMove;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMoveOrder = itemView.findViewById(R.id.txt_order);
            txtWhiteMove = itemView.findViewById(R.id.txt_white_move);
            txtBlackMove = itemView.findViewById(R.id.txt_black_move);
        }
    }
}
