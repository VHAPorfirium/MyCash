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

public class APagarActivity extends AppCompatActivity {

    private Button btnAdicionarAPagar;
    private RecyclerView rvAPagar;
    private TransacaoAdapter adapter;
    private List<Transacao> listaAPagarMes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_apagar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnAdicionarAPagar = findViewById(R.id.btnAdicionarAPagar);
        rvAPagar = findViewById(R.id.rvAPagar);

        listaAPagarMes = carregarAPagarDoMesAtual();

        adapter = new TransacaoAdapter(listaAPagarMes);
        rvAPagar.setLayoutManager(new LinearLayoutManager(this));
        rvAPagar.setAdapter(adapter);

        adapter.setOnItemClickListener(transacao -> showOptionsDialog(transacao));

        btnAdicionarAPagar.setOnClickListener(view -> {
            Intent intent = new Intent(APagarActivity.this, AdicionarAPagarActivity.class);
            startActivity(intent);
        });
    }

    private List<Transacao> carregarAPagarDoMesAtual() {
        List<Transacao> lista = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        int mesAtual = cal.get(Calendar.MONTH);
        for (Transacao t : TransacaoRepository.getTransacoes()) {
            Calendar c = Calendar.getInstance();
            c.setTime(t.getData());
            if (c.get(Calendar.MONTH) == mesAtual && "A Pagar".equalsIgnoreCase(t.getTipo())) {
                lista.add(t);
            }
        }
        return lista;
    }

    private void showOptionsDialog(Transacao transacao) {
        String[] options = {"Editar", "Excluir", "Cancelar"};
        new AlertDialog.Builder(this)
                .setTitle("Opções A Pagar")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(APagarActivity.this, EditarAPagarActivity.class);
                        intent.putExtra("transacao_id", transacao.getId());
                        startActivity(intent);
                    } else if (which == 1) {
                        excluirAPagar(transacao);
                    } else {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void excluirAPagar(Transacao transacao) {
        TransacaoRepository.removeTransacao(transacao);
        listaAPagarMes.remove(transacao);
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Item 'A Pagar' excluído", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listaAPagarMes.clear();
        listaAPagarMes.addAll(carregarAPagarDoMesAtual());
        adapter.notifyDataSetChanged();
    }
}
