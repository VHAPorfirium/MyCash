package com.example.mycash.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycash.R;
import com.example.mycash.database.TransacaoRepository;
import com.example.mycash.model.Transacao;

import java.util.Date;

public class AdicionarSaidaActivity extends AppCompatActivity {

    private EditText etDescricao, etValor;
    private Spinner spFormaPagamento;
    private Button btnSalvar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_transacao);

        etDescricao = findViewById(R.id.etDescricao);
        etValor = findViewById(R.id.etValor);
        spFormaPagamento = findViewById(R.id.spFormaPagamento);
        btnSalvar = findViewById(R.id.btnSalvar);

        // Configurar spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.formas_pagamento, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFormaPagamento.setAdapter(adapter);

        btnSalvar.setOnClickListener(v -> salvarSaida());
    }

    private void salvarSaida() {
        String descricao = etDescricao.getText().toString();
        double valor = Double.parseDouble(etValor.getText().toString());
        String formaPagamento = spFormaPagamento.getSelectedItem().toString();

        Transacao saida = new Transacao();
        saida.setTipo("SAIDA");
        saida.setDescricao(descricao);
        saida.setValor(-valor); // Valor negativo para saídas
        saida.setFormaPagamento(formaPagamento);
        saida.setData(new Date());

        TransacaoRepository.addTransacao(saida);
        Toast.makeText(this, "Saída registrada!", Toast.LENGTH_SHORT).show();
        finish();
    }
}