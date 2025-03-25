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

public class SaidasActivity extends AppCompatActivity {

    private Button btnAdicionarSaidas;
    private RecyclerView rvUltimasSaidas;
    private TransacaoAdapter adapter;
    private List<Transacao> listaSaidas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saidas);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, systemBarsInsets.bottom);
            return insets;
        });

        btnAdicionarSaidas = findViewById(R.id.btnAdicionarSaidas);
        rvUltimasSaidas = findViewById(R.id.rvUltimasSaidas);

        // Carrega as saídas do mês atual
        listaSaidas = carregarSaidasDoMesAtual();

        // Configura o RecyclerView
        adapter = new TransacaoAdapter(listaSaidas);
        rvUltimasSaidas.setLayoutManager(new LinearLayoutManager(this));
        rvUltimasSaidas.setAdapter(adapter);

        // Clique em item -> opções de editar/excluir
        adapter.setOnItemClickListener(transacao -> {
            showOptionsDialog(transacao);
        });

        // Botão "Adicionar Saída"
        btnAdicionarSaidas.setOnClickListener(view -> {
            Intent intent = new Intent(SaidasActivity.this, AdicionarSaidaActivity.class);
            startActivity(intent);
        });
    }

    private List<Transacao> carregarSaidasDoMesAtual() {
        List<Transacao> saidasMes = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int mesAtual = cal.get(Calendar.MONTH);

        List<Transacao> todasSaidas = getTodasSaidas();

        for (Transacao t : todasSaidas) {
            Calendar calTransacao = Calendar.getInstance();
            calTransacao.setTime(t.getData());
            if (calTransacao.get(Calendar.MONTH) == mesAtual) {
                saidasMes.add(t);
            }
        }
        return saidasMes;
    }

    private List<Transacao> getTodasSaidas() {
        List<Transacao> todas = TransacaoRepository.getTransacoes();
        List<Transacao> listaSaidas = new ArrayList<>();

        for (Transacao t : todas) {
            if (t.getValor() < 0) {
                listaSaidas.add(t);
            }
        }
        return listaSaidas;
    }

    private void showOptionsDialog(final Transacao transacao) {
        String[] options = {"Editar", "Excluir", "Cancelar"};
        new AlertDialog.Builder(this)
                .setTitle("Opções de Saída")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Editar
                            Intent intent = new Intent(SaidasActivity.this, EditarSaidaActivity.class);
                            intent.putExtra("transacao_id", transacao.getId());
                            startActivity(intent);
                            break;
                        case 1: // Excluir
                            excluirSaida(transacao);
                            break;
                        case 2: // Cancelar
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }

    private void excluirSaida(Transacao transacao) {
        TransacaoRepository.removeTransacao(transacao);
        listaSaidas.remove(transacao);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Saída excluída", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listaSaidas.clear();
        listaSaidas.addAll(carregarSaidasDoMesAtual());
        adapter.notifyDataSetChanged();
    }
}
