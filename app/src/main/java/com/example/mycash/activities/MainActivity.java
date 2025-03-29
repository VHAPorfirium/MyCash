package com.example.mycash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mycash.R;
import com.example.mycash.adapter.TransacaoAdapter;
import com.example.mycash.database.TransacaoRepository;
import com.example.mycash.model.Transacao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvSaldo, tvEntradas, tvSaidas, tvAPagar;
    private RecyclerView rvTransacoes;
    private TransacaoAdapter transacaoAdapter;
    private NumberFormat currencyFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        initViews();
        setupRecyclerView();
        setupFloatingActionButton();
        setupButtons();
        updateResumo();
    }

    private void initViews() {
        tvSaldo = findViewById(R.id.tvSaldo);
        tvEntradas = findViewById(R.id.tvEntradas);
        tvSaidas = findViewById(R.id.tvSaidas);
        tvAPagar = findViewById(R.id.tvAPagar);
        rvTransacoes = findViewById(R.id.rvTransacoes);
    }

    private void setupRecyclerView() {
        rvTransacoes.setLayoutManager(new LinearLayoutManager(this));
        transacaoAdapter = new TransacaoAdapter(transacao -> {
            Intent intent = new Intent(this, GerenciarTransacaoActivity.class);
            intent.putExtra("transacao_id", transacao.getId());
            startActivity(intent);
        });
        rvTransacoes.setAdapter(transacaoAdapter);
    }

    private void setupFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fabAdicionar);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(this, GerenciarTransacaoActivity.class);
            startActivity(intent);
        });
    }

    private void setupButtons() {
        Button btnNovaEntrada = findViewById(R.id.btnNovaEntrada);
        Button btnNovaSaida = findViewById(R.id.btnNovaSaida);

        btnNovaEntrada.setOnClickListener(v -> {
            startActivity(new Intent(this, AdicionarEntradaActivity.class));
        });

        btnNovaSaida.setOnClickListener(v -> {
            startActivity(new Intent(this, AdicionarSaidaActivity.class));
        });
    }

    private void abrirGerenciadorTransacao(String tipo) {
        try {
            Intent intent = new Intent(MainActivity.this, GerenciarTransacaoActivity.class);
            intent.putExtra("tipo_selecionado", tipo);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao abrir: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void updateResumo() {
        List<Transacao> transacoes = TransacaoRepository.getTransacoes();
        Collections.sort(transacoes, (t1, t2) -> t2.getData().compareTo(t1.getData()));

        double totalEntradas = 0;
        double totalSaidas = 0;
        double totalAPagar = 0;

        for (Transacao t : transacoes) {
            // Substitua por:
            totalEntradas = TransacaoRepository.getTotalEntradas();
            totalSaidas = TransacaoRepository.getTotalSaidas();
            double saldo = totalEntradas - totalSaidas;
        }

        double saldo = totalEntradas - totalSaidas;

        tvSaldo.setText(currencyFormat.format(saldo));
        tvEntradas.setText(currencyFormat.format(totalEntradas));
        tvSaidas.setText(currencyFormat.format(totalSaidas));
        tvAPagar.setText(currencyFormat.format(totalAPagar));
        transacaoAdapter.setTransacoes(transacoes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateResumo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_filtrar) {
            // Implementar filtro
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) { // Código da GerenciarTransacaoActivity
            updateResumo();
        }
    }
}