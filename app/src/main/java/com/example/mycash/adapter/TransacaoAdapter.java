package com.example.mycash.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mycash.R;
import com.example.mycash.model.Transacao;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransacaoAdapter extends RecyclerView.Adapter<TransacaoAdapter.ViewHolder> {

    private List<Transacao> transacaoList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transacao transacao);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TransacaoAdapter(List<Transacao> transacaoList) {
        this.transacaoList = transacaoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transacao, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transacao transacao = transacaoList.get(position);

        // Configuração do formato de data
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        holder.tvDescricao.setText(transacao.getDescricao());
        holder.tvValor.setText(String.format("R$ %.2f", transacao.getValor()));
        holder.tvTipo.setText(transacao.getTipo());
        holder.tvData.setText(sdf.format(transacao.getData()));

        holder.itemView.setOnClickListener(v -> {
            if(listener != null) {
                listener.onItemClick(transacao);
            }
        });
    }


    @Override
    public int getItemCount() {
        return transacaoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescricao, tvValor, tvTipo, tvData;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescricao = itemView.findViewById(R.id.tvDescricao);
            tvValor = itemView.findViewById(R.id.tvValor);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvData = itemView.findViewById(R.id.tvData);
        }
    }
}
