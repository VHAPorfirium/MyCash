package com.example.mycash.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.mycash.R;
import com.example.mycash.database.TransacaoRepository;
import com.example.mycash.model.Transacao;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditarEntradaActivity extends AppCompatActivity {

    private EditText etDescricao, etValor, etTipo, etData;
    private Button btnSalvarEdicao;
    private Transacao transacao;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_entrada);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etDescricao = findViewById(R.id.etDescricao);
        etValor = findViewById(R.id.etValor);
        etTipo = findViewById(R.id.etTipo);
        etData = findViewById(R.id.etData);
        btnSalvarEdicao = findViewById(R.id.btnSalvarEdicao);

        int transacaoId = getIntent().getIntExtra("transacao_id", -1);
        transacao = TransacaoRepository.getTransacaoById(transacaoId);

        if (transacao != null) {
            etDescricao.setText(transacao.getDescricao());
            etValor.setText(String.valueOf(transacao.getValor()));
            etTipo.setText(transacao.getTipo());
            etData.setText(sdf.format(transacao.getData()));
        }

        etData.setOnClickListener(v -> showDatePickerDialog());

        btnSalvarEdicao.setOnClickListener(v -> atualizarTransacao());
    }

    private void atualizarTransacao() {
        String novaDescricao = etDescricao.getText().toString().trim();
        String valorText = etValor.getText().toString().trim();
        String novaDataStr = etData.getText().toString().trim();

        if (novaDescricao.isEmpty()) {
            etDescricao.setError("Informe a descrição");
            etDescricao.requestFocus();
            return;
        }
        if (valorText.isEmpty()) {
            etValor.setError("Informe o valor");
            etValor.requestFocus();
            return;
        }
        if (novaDataStr.isEmpty()) {
            etData.setError("Selecione a data");
            etData.requestFocus();
            return;
        }

        try {
            double novoValor = Double.parseDouble(valorText);
            transacao.setDescricao(novaDescricao);
            transacao.setValor(novoValor);
            transacao.setTipo(etTipo.getText().toString());
            transacao.setData(sdf.parse(novaDataStr));
            Toast.makeText(this, "Entrada atualizada!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            etValor.setError("Valor inválido");
            etValor.requestFocus();
        } catch (ParseException e) {
            etData.setError("Formato de data inválido (dd/MM/yyyy)");
            etData.requestFocus();
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        String currentDateStr = etData.getText().toString();
        if (!currentDateStr.isEmpty()) {
            try {
                calendar.setTime(sdf.parse(currentDateStr));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String dateString = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    etData.setText(dateString);
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}
