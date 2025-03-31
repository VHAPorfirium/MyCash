package com.example.mycash.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mycash.R;
import com.example.mycash.database.TransacaoDAO;
import com.example.mycash.model.Transacao;
import java.util.Date;

public class AdicionarEntradaActivity extends AppCompatActivity {

    private EditText etDescricao, etValor;
    private Button btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_transacao);

        etDescricao = findViewById(R.id.etDescricao);
        etValor = findViewById(R.id.etValor);
        btnSalvar = findViewById(R.id.btnSalvar);

        btnSalvar.setOnClickListener(v -> salvarEntrada());
    }

    private void salvarEntrada() {
        String descricao = etDescricao.getText().toString();
        double valor = Double.parseDouble(etValor.getText().toString());

        Transacao entrada = new Transacao();
        entrada.setTipo("Entrada");
        entrada.setDescricao(descricao);
        entrada.setValor(valor);
        entrada.setData(new Date());

        TransacaoDAO dao = new TransacaoDAO(this);
        long novoId = dao.addTransacao(entrada);
        entrada.setId((int) novoId);
        Toast.makeText(this, "Entrada adicionada!", Toast.LENGTH_SHORT).show();
        finish();
    }
}