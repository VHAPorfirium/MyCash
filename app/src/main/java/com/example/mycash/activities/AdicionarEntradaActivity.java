package com.example.mycash.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.mycash.R;
import com.example.mycash.model.Transacao;
import com.example.mycash.database.TransacaoRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdicionarEntradaActivity extends AppCompatActivity {

    private EditText etDescricao, etValor, etTipo, etData;
    private Button btnSalvarEntrada;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adicionar_entrada);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Vincula as views
        etDescricao = findViewById(R.id.etDescricao);
        etValor = findViewById(R.id.etValor);
        etTipo = findViewById(R.id.etTipo);
        etData = findViewById(R.id.etData);
        btnSalvarEntrada = findViewById(R.id.btnSalvarEntrada);

        // Define o tipo fixo para "Entrada"
        etTipo.setText("Entrada");
        etTipo.setEnabled(false);

        // Define a data atual como padrão
        etData.setText(sdf.format(new Date()));

        // Configura o campo de data para abrir um DatePickerDialog ao clicar
        etData.setOnClickListener(view -> showDatePickerDialog());

        // Configura o clique do botão Salvar
        btnSalvarEntrada.setOnClickListener(view -> salvarTransacao());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String dateString = String.format(Locale.getDefault(),
                            "%02d/%02d/%04d", selectedDay, (selectedMonth + 1), selectedYear);
                    etData.setText(dateString);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void salvarTransacao() {
        // Validação dos campos
        if (etDescricao.getText().toString().trim().isEmpty()) {
            etDescricao.setError("Informe a descrição");
            etDescricao.requestFocus();
            return;
        }

        if (etValor.getText().toString().trim().isEmpty()) {
            etValor.setError("Informe o valor");
            etValor.requestFocus();
            return;
        }

        try {
            double valor = Double.parseDouble(etValor.getText().toString());
            if (valor <= 0) {
                etValor.setError("O valor deve ser positivo");
                etValor.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etValor.setError("Valor inválido");
            etValor.requestFocus();
            return;
        }

        String dataStr = etData.getText().toString();
        Date data;
        try {
            data = sdf.parse(dataStr);
            if (data == null) {
                etData.setError("Data inválida");
                etData.requestFocus();
                return;
            }
        } catch (ParseException e) {
            etData.setError("Formato de data inválido (dd/MM/yyyy)");
            etData.requestFocus();
            return;
        }

        // Cria e salva a transação
        Transacao novaTransacao = new Transacao(
                0, // ID será gerado automaticamente
                etDescricao.getText().toString().trim(),
                Double.parseDouble(etValor.getText().toString()),
                etTipo.getText().toString(),
                data
        );

        TransacaoRepository.addTransacao(novaTransacao);
        Toast.makeText(this, "Entrada registrada com sucesso!", Toast.LENGTH_SHORT).show();
        finish();
    }
}