package com.example.mycash.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.mycash.database.TransacaoDAO;
import com.example.mycash.model.Transacao;
import com.google.android.material.datepicker.MaterialDatePicker;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GerenciarTransacaoActivity extends AppCompatActivity {

    private EditText etDescricao, etValor, etData;
    private Spinner spTipo, spCategoria, spFormaPagamento;
    private CheckBox cbAPagar;
    private Button btnSalvar;

    private Transacao transacaoEditada;
    private boolean isEdicao = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_transacao);

        inicializarComponentes();
        configurarSpinners();
        verificarModoEdicao();
        configurarListeners();
    }

    @SuppressLint("WrongViewCast")
    private void inicializarComponentes() {
        etDescricao = findViewById(R.id.etDescricao);
        etValor = findViewById(R.id.etValor);
        etData = findViewById(R.id.etData);
        spTipo = findViewById(R.id.spTipo);
        spCategoria = findViewById(R.id.spCategoria);
        spFormaPagamento = findViewById(R.id.spFormaPagamento);
        cbAPagar = findViewById(R.id.cbAPagar);
        btnSalvar = findViewById(R.id.btnSalvar);

        etData.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
    }

    private void configurarSpinners() {
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

        spTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                boolean isSaida = parent.getItemAtPosition(pos).toString().equals("Saída");
                findViewById(R.id.layoutSaida).setVisibility(isSaida ? View.VISIBLE : View.GONE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void verificarModoEdicao() {
        int transacaoId = getIntent().getIntExtra("transacao_id", -1);
        if (transacaoId != -1) {
            TransacaoDAO dao = new TransacaoDAO(this);
            transacaoEditada = dao.getTransacaoById(transacaoId);
            if (transacaoEditada != null) {
                isEdicao = true;
                preencherCamposEdicao();
                setTitle("Editar Transação");
                return;
            }
        }
        setTitle("Nova Transação");

        String tipoSelecionado = getIntent().getStringExtra("tipo_selecionado");
        if (tipoSelecionado != null) {
            for (int i = 0; i < spTipo.getCount(); i++) {
                if (spTipo.getItemAtPosition(i).toString().equals(tipoSelecionado)) {
                    spTipo.setSelection(i);
                    spTipo.setEnabled(false);
                    break;
                }
            }
        }
    }

    private void preencherCamposEdicao() {
        etDescricao.setText(transacaoEditada.getDescricao());
        etValor.setText(String.valueOf(Math.abs(transacaoEditada.getValor())));
        etData.setText(transacaoEditada.getDataFormatada());

        selecionarSpinner(spTipo, transacaoEditada.getTipo());
        selecionarSpinner(spCategoria, transacaoEditada.getCategoria());

        if (transacaoEditada.getFormaPagamento() != null) {
            selecionarSpinner(spFormaPagamento, transacaoEditada.getFormaPagamento());
        }

        if (transacaoEditada.isSaida()) {
            cbAPagar.setChecked("A Pagar".equals(transacaoEditada.getCategoria()));
        }
    }

    private void selecionarSpinner(Spinner spinner, String valor) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(valor)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void configurarListeners() {
        etData.setOnClickListener(v -> mostrarDatePicker());
        btnSalvar.setOnClickListener(v -> salvarTransacao());
    }

    private void mostrarDatePicker() {
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
            if (!validarCampos()) {
                return;
            }

            String descricao = etDescricao.getText().toString().trim();
            double valor = Double.parseDouble(etValor.getText().toString());
            Date data = parseDate(etData.getText().toString());
            String tipo = spTipo.getSelectedItem().toString();
            String categoria = spCategoria.getSelectedItem().toString();

            Transacao transacao = isEdicao ? transacaoEditada : new Transacao();
            transacao.setDescricao(descricao);
            transacao.setValor(tipo.equals("Entrada") ? valor : -valor);
            transacao.setTipo(tipo);
            transacao.setCategoria(categoria);
            transacao.setData(data);

            if (tipo.equals("Saída")) {
                String formaPagamento = spFormaPagamento.getSelectedItem().toString();
                transacao.setFormaPagamento(formaPagamento);
                if (cbAPagar.isChecked()) {
                    transacao.setCategoria("A Pagar");
                }
            }

            TransacaoDAO dao = new TransacaoDAO(this);
            if (isEdicao) {
                dao.updateTransacao(transacao);
                Toast.makeText(this, "Transação atualizada com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                long novoId = dao.addTransacao(transacao);
                transacao.setId((int) novoId);
                Toast.makeText(this, "Transação cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
            }
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            Toast.makeText(this, "Data inválida", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarCampos() {
        if (TextUtils.isEmpty(etDescricao.getText())) {
            etDescricao.setError("Informe a descrição");
            return false;
        }

        if (TextUtils.isEmpty(etValor.getText())) {
            etValor.setError("Informe o valor");
            return false;
        }

        try {
            double valor = Double.parseDouble(etValor.getText().toString());
            if (valor <= 0) {
                etValor.setError("Valor deve ser positivo");
                return false;
            }
        } catch (NumberFormatException e) {
            etValor.setError("Valor inválido");
            return false;
        }

        if (TextUtils.isEmpty(etData.getText())) {
            etData.setError("Informe a data");
            return false;
        }

        return true;
    }

    private Date parseDate(String dateStr) throws ParseException {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateStr);
    }
}