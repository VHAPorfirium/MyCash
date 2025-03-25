package com.example.mycash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mycash.R;
import com.example.mycash.adapter.TransacaoAdapter;
import com.example.mycash.model.Transacao;
import com.example.mycash.database.TransacaoRepository;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EntradasActivity extends AppCompatActivity {

    private Button btnAdicionarEntrada;
    private RecyclerView rvUltimasEntradas;
    private TransacaoAdapter adapter;
    private List<Transacao> transacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entradas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnAdicionarEntrada = findViewById(R.id.btnAdicionarEntrada);
        rvUltimasEntradas = findViewById(R.id.rvUltimasEntradas);

        // Carrega a lista de transações do mês atual
        transacoes = carregarTransacoesDoMesAtual();

        // Configura RecyclerView
        adapter = new TransacaoAdapter(transacoes);
        rvUltimasEntradas.setLayoutManager(new LinearLayoutManager(this));
        rvUltimasEntradas.setAdapter(adapter);

        // Configura clique para abrir popup de opções na transação
        adapter.setOnItemClickListener(transacao -> {
            showOptionsDialog(transacao);
        });

        // Configura clique do botão para abrir a tela de Adicionar Entrada
        btnAdicionarEntrada.setOnClickListener(view -> {
            Intent intent = new Intent(EntradasActivity.this, AdicionarEntradaActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Atualiza a lista de transações ao retornar da tela de cadastro ou edição
        transacoes = carregarTransacoesDoMesAtual();
        adapter = new TransacaoAdapter(transacoes);
        rvUltimasEntradas.setAdapter(adapter);
        adapter.setOnItemClickListener(t -> showOptionsDialog(t));
    }

    private List<Transacao> carregarTransacoesDoMesAtual() {
        List<Transacao> lista = new ArrayList<>();
        List<Transacao> todasTransacoes = TransacaoRepository.getTransacoes();
        Calendar cal = Calendar.getInstance();
        int mesAtual = cal.get(Calendar.MONTH);
        for (Transacao t : todasTransacoes) {
            Calendar calTransacao = Calendar.getInstance();
            calTransacao.setTime(t.getData());
            if (calTransacao.get(Calendar.MONTH) == mesAtual) {
                lista.add(t);
            }
        }
        return lista;
    }

    private void showOptionsDialog(final Transacao transacao) {
        String[] options = {"Editar", "Excluir", "Cancelar"};
        new MaterialAlertDialogBuilder(this)
                .setTitle("Opções")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Editar
                            Intent intent = new Intent(EntradasActivity.this, EditarEntradaActivity.class);
                            intent.putExtra("transacao_id", transacao.getId());
                            startActivity(intent);
                            break;
                        case 1: // Excluir
                            excluirTransacao(transacao);
                            break;
                        case 2: // Cancelar
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }

    private void excluirTransacao(Transacao transacao) {
        TransacaoRepository.removeTransacao(transacao);
        Toast.makeText(this, "Transação excluída", Toast.LENGTH_SHORT).show();
        // Atualiza a lista
        transacoes = carregarTransacoesDoMesAtual();
        adapter = new TransacaoAdapter(transacoes);
        rvUltimasEntradas.setAdapter(adapter);
        adapter.setOnItemClickListener(t -> showOptionsDialog(t));
    }
}
