package com.example.mycash.adapter;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mycash.R;
import com.example.mycash.activities.EditarTransacaoActivity;
import com.example.mycash.database.TransacaoDAO;
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

            int color = transacao.isEntrada()
                    ? itemView.getContext().getResources().getColor(R.color.verde_entrada)
                    : itemView.getContext().getResources().getColor(R.color.vermelho_saida);
            tvValor.setTextColor(color);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), EditarTransacaoActivity.class);
                intent.putExtra("transacao_id", transacao.getId());
                itemView.getContext().startActivity(intent);
            });

            btnExcluir.setOnClickListener(v -> {
                new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Confirmar exclusão")
                        .setMessage("Deseja realmente excluir esta transação?")
                        .setPositiveButton("Excluir", (dialog, which) -> {
                            TransacaoDAO dao = new TransacaoDAO(itemView.getContext());
                            dao.deleteTransacao(transacao.getId());
                            listener.onTransacaoClick(null); // Notifica a Activity para atualizar a lista
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });
        }
    }
}
