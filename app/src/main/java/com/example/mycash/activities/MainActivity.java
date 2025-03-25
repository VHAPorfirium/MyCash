package com.example.mycash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.mycash.R;

public class MainActivity extends AppCompatActivity {

    private Button btnEntradas, btnSaidas, btnAPagar, btnEstruturaFinanceira, btnResumoFinanceiro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        btnEntradas = findViewById(R.id.btnEntradas);
        btnSaidas = findViewById(R.id.btnSaidas);
        btnAPagar = findViewById(R.id.btnAPagar);
        btnEstruturaFinanceira = findViewById(R.id.btnEstruturaFinanceira);
        btnResumoFinanceiro = findViewById(R.id.btnResumoFinanceiro);

        // Botão: Entradas
        btnEntradas.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, EntradasActivity.class);
            startActivity(intent);
        });

        // Botão: Saídas
        btnSaidas.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SaidasActivity.class);
            startActivity(intent);
        });

        // Botão: A Pagar
        btnAPagar.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, APagarActivity.class);
            startActivity(intent);
        });

        // Botão: Estrutura Financeira
        btnEstruturaFinanceira.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, EstruturaFinanceiraActivity.class);
            startActivity(intent);
        });

        // Botão: Resumo Financeiro
        btnResumoFinanceiro.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ResumoFinanceiroActivity.class);
            startActivity(intent);
        });
    }
}
