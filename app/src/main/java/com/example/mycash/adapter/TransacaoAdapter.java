package com.example.mycash.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mycash.R;
import com.example.mycash.database.TransacaoRepository;
import com.example.mycash.model.Transacao;
import java.util.List;

public class TransacaoAdapter extends RecyclerView.Adapter<TransacaoAdapter.TransacaoViewHolder> {

    private List<Transacao> transacoes;
    private final OnTransacaoClickListener listener;

    public interface OnTransacaoClickListener {
        void onTransacaoClick(Transacao transacao);
    }

    public TransacaoAdapter(OnTransacaoClickListener listener) {
        this.listener = listener;
    }

    public void setTransacoes(List<Transacao> transacoes) {
        this.transacoes = transacoes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransacaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transacao, parent, false);
        return new TransacaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransacaoViewHolder holder, int position) {
        Transacao transacao = transacoes.get(position);
        holder.bind(transacao, listener);
    }

    @Override
    public int getItemCount() {
        return transacoes != null ? transacoes.size() : 0;
    }

    static class TransacaoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDescricao, tvData, tvValor, tvCategoria;
        private final ImageButton btnExcluir;

        public TransacaoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescricao = itemView.findViewById(R.id.tvDescricao);
            tvData = itemView.findViewById(R.id.tvData);
            tvValor = itemView.findViewById(R.id.tvValor);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }

        public void bind(Transacao transacao, OnTransacaoClickListener listener) {
            tvDescricao.setText(transacao.getDescricao());
            tvData.setText(transacao.getDataFormatada());
            tvValor.setText(transacao.getValorFormatado());
            tvCategoria.setText(transacao.getCategoria());

            // Cor baseada no tipo de transação
            int color = transacao.isEntrada()
                    ? itemView.getContext().getResources().getColor(R.color.verde_entrada)
                    : itemView.getContext().getResources().getColor(R.color.vermelho_saida);

            tvValor.setTextColor(color);

            // Clique no item para editar
            itemView.setOnClickListener(v -> listener.onTransacaoClick(transacao));

            // Botão excluir
            btnExcluir.setOnClickListener(v -> {
                TransacaoRepository.removeTransacao(transacao);
                listener.onTransacaoClick(null); // Notifica para atualizar
            });
        }
    }
}