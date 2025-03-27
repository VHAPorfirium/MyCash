package com.example.mycash.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mycash.R;
import com.example.mycash.database.TransacaoRepository;
import com.example.mycash.model.Transacao;
import com.google.android.material.datepicker.MaterialDatePicker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GerenciarTransacaoActivity extends AppCompatActivity {

    private EditText etDescricao, etValor, etData;
    private Spinner spTipo, spCategoria, spFormaPagamento;
    private Button btnSalvar;
    private Transacao transacaoEditada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_transacao);

        try {
            initViews();
            setupSpinners();
            checkEdicao();
            setupListeners();

            // Adicione este tratamento para o tipo_selecionado
            String tipo = getIntent().getStringExtra("tipo_selecionado");
            if (tipo != null) {
                int position = getIndex(spTipo, tipo);
                if (position >= 0) {
                    spTipo.setSelection(position);
                    spTipo.setEnabled(false);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Erro ao inicializar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void configurarTipoInicial() {
        String tipoSelecionado = getIntent().getStringExtra("tipo_selecionado");
        if (tipoSelecionado != null) {
            int posicao = ((ArrayAdapter)spTipo.getAdapter()).getPosition(tipoSelecionado);
            spTipo.setSelection(posicao);

            // Bloqueia alteração se veio de um botão específico
            spTipo.setEnabled(false);

            // Mostra/oculta campos específicos
            findViewById(R.id.layoutSaida).setVisibility(
                    tipoSelecionado.equals("Saída") ? View.VISIBLE : View.GONE);
        }
    }

    @SuppressLint("WrongViewCast")
    private void initViews() {
        etDescricao = findViewById(R.id.etDescricao);
        etValor = findViewById(R.id.etValor);
        etData = findViewById(R.id.etData);
        spTipo = findViewById(R.id.spTipo);
        spCategoria = findViewById(R.id.spCategoria);
        spFormaPagamento = findViewById(R.id.spFormaPagamento);
        btnSalvar = findViewById(R.id.btnSalvar);

        // Configura tipo padrão se veio de um botão específico
        String tipoSelecionado = getIntent().getStringExtra("tipo_selecionado");
        if (tipoSelecionado != null) {
            int posicao = ((ArrayAdapter) spTipo.getAdapter()).getPosition(tipoSelecionado);
            spTipo.setSelection(posicao);
            spTipo.setEnabled(false);
        }

        etData.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> tipoAdapter = ArrayAdapter.createFromResource(
                this, R.array.tipos_transacao, android.R.layout.simple_spinner_item);
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(tipoAdapter);

        ArrayAdapter<CharSequence> categoriaAdapter = ArrayAdapter.createFromResource(
                this, R.array.categorias, android.R.layout.simple_spinner_item);
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoria.setAdapter(categoriaAdapter);

        ArrayAdapter<CharSequence> pagamentoAdapter = ArrayAdapter.createFromResource(
                this, R.array.formas_pagamento, android.R.layout.simple_spinner_item);
        pagamentoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFormaPagamento.setAdapter(pagamentoAdapter);
    }

    private void setupTipoListener() {
        spTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tipo = parent.getItemAtPosition(position).toString();
                findViewById(R.id.layoutSaida).setVisibility(
                        tipo.equals("Saída") ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void checkEdicao() {
        int transacaoId = getIntent().getIntExtra("transacao_id", -1);
        if (transacaoId != -1) {
            transacaoEditada = TransacaoRepository.getTransacaoById(transacaoId);
            if (transacaoEditada != null) {
                preencherCampos();
                setTitle("Editar Transação");
            }
        } else {
            setTitle("Nova Transação");
        }
    }

    private void preencherCampos() {
        etDescricao.setText(transacaoEditada.getDescricao());
        etValor.setText(String.valueOf(Math.abs(transacaoEditada.getValor())));
        etData.setText(transacaoEditada.getDataFormatada());

        spTipo.setSelection(getIndex(spTipo, transacaoEditada.getTipo()));
        spCategoria.setSelection(getIndex(spCategoria, transacaoEditada.getCategoria()));
        if (transacaoEditada.getFormaPagamento() != null) {
            spFormaPagamento.setSelection(getIndex(spFormaPagamento, transacaoEditada.getFormaPagamento()));
        }
    }

    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }

    private void setupListeners() {
        etData.setOnClickListener(v -> showDatePicker());
        btnSalvar.setOnClickListener(v -> salvarTransacao());
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data")
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            String dateStr = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(new Date(selection));
            etData.setText(dateStr);
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void salvarTransacao() {
        try {
            String descricao = etDescricao.getText().toString();
            String valorStr = etValor.getText().toString();
            String dataStr = etData.getText().toString();

            if (descricao.isEmpty() || valorStr.isEmpty() || dataStr.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
                return;
            }

            double valor = Double.parseDouble(valorStr);
            String tipo = spTipo.getSelectedItem().toString();
            String categoria = spCategoria.getSelectedItem().toString();

            // Criar ou editar transação
            Transacao transacao = transacaoEditada != null ? transacaoEditada : new Transacao();
            transacao.setDescricao(descricao);
            transacao.setValor(tipo.equals("Entrada") ? valor : -valor);
            transacao.setTipo(tipo);
            transacao.setCategoria(categoria);

            // Configurar data (implemente o parser adequado)
            transacao.setData(new Date());

            // Campos específicos para Saídas
            if (tipo.equals("Saída")) {
                String formaPagamento = spFormaPagamento.getSelectedItem().toString();
                transacao.setFormaPagamento(formaPagamento);

                CheckBox cbAPagar = findViewById(R.id.cbAPagar);
                if (cbAPagar.isChecked()) {
                    transacao.setCategoria("A Pagar");
                }
            }

            // Salvar no repositório
            if (transacaoEditada != null) {
                TransacaoRepository.atualizarTransacao(transacao);
                Toast.makeText(this, "Transação atualizada!", Toast.LENGTH_SHORT).show();
            } else {
                TransacaoRepository.addTransacao(transacao);
                Toast.makeText(this, "Transação salva!", Toast.LENGTH_SHORT).show();
            }

            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show();
        }
    }
}