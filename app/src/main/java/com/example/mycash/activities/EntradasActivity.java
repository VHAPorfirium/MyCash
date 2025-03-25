package com.example.mycash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mycash.R;
import com.example.mycash.adapter.TransacaoAdapter;
import com.example.mycash.database.TransacaoRepository;
import com.example.mycash.model.Transacao;
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

        // Configura os insets (assegure que o root view do layout tenha id="main")
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return windowInsets;
        });

        btnAdicionarEntrada = findViewById(R.id.btnAdicionarEntrada);
        rvUltimasEntradas = findViewById(R.id.rvUltimasEntradas);

        transacoes = carregarTransacoesDoMesAtual();

        adapter = new TransacaoAdapter(transacoes);
        rvUltimasEntradas.setLayoutManager(new LinearLayoutManager(this));
        rvUltimasEntradas.setAdapter(adapter);

        adapter.setOnItemClickListener(transacao -> {
            showOptionsDialog(transacao);
        });

        btnAdicionarEntrada.setOnClickListener(view -> {
            Intent intent = new Intent(EntradasActivity.this, AdicionarEntradaActivity.class);
            startActivity(intent);
        });
    }

    private List<Transacao> carregarTransacoesDoMesAtual() {
        List<Transacao> lista = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int mesAtual = cal.get(Calendar.MONTH);
        for (Transacao t : TransacaoRepository.getTransacoes()) {
            Calendar calTransacao = Calendar.getInstance();
            calTransacao.setTime(t.getData());
            // Filtra somente transações do mês atual com tipo "Entrada"
            if (calTransacao.get(Calendar.MONTH) == mesAtual && "Entrada".equalsIgnoreCase(t.getTipo())) {
                lista.add(t);
            }
        }
        return lista;
    }

    private void showOptionsDialog(final Transacao transacao) {
        String[] options = {"Editar", "Excluir", "Cancelar"};
        new AlertDialog.Builder(this)
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
                        case 2:
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }

    private void excluirTransacao(Transacao transacao) {
        TransacaoRepository.removeTransacao(transacao);
        Toast.makeText(this, "Transação excluída", Toast.LENGTH_SHORT).show();
        transacoes = carregarTransacoesDoMesAtual();
        adapter = new TransacaoAdapter(transacoes);
        rvUltimasEntradas.setAdapter(adapter);
        adapter.setOnItemClickListener(t -> showOptionsDialog(t));
    }

    @Override
    protected void onResume() {
        super.onResume();
        transacoes = carregarTransacoesDoMesAtual();
        adapter = new TransacaoAdapter(transacoes);
        rvUltimasEntradas.setAdapter(adapter);
        adapter.setOnItemClickListener(t -> showOptionsDialog(t));
    }
}
