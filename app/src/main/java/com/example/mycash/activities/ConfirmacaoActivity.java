package com.example.mycash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.mycash.R;

public class ConfirmacaoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmacao);

        TextView tvMensagem = findViewById(R.id.tvMensagem);
        Button btnConfirmar = findViewById(R.id.btnConfirmar);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        String mensagem = getIntent().getStringExtra("mensagem");
        tvMensagem.setText(mensagem);

        btnConfirmar.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });

        btnCancelar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}