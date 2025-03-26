package com.example.mycash.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
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
import com.example.mycash.database.TransacaoRepository;
import com.example.mycash.model.Transacao;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdicionarAPagarActivity extends AppCompatActivity {

    private EditText etDescricaoAPagar, etValorAPagar, etDataAPagar;
    private Button btnSalvarAPagar;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adicionar_apagar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etDescricaoAPagar = findViewById(R.id.etDescricaoAPagar);
        etValorAPagar = findViewById(R.id.etValorAPagar);
        etDataAPagar = findViewById(R.id.etDataAPagar);
        btnSalvarAPagar = findViewById(R.id.btnSalvarAPagar);

        // Se o campo de data estiver vazio, define a data atual
        if (etDataAPagar.getText().toString().trim().isEmpty()) {
            etDataAPagar.setText(dateFormat.format(new Date()));
        }

        etDataAPagar.setOnClickListener(view -> showDatePickerDialog());

        btnSalvarAPagar.setOnClickListener(view -> salvarTransacao());
    }

    private void salvarTransacao() {
        String descricao = etDescricaoAPagar.getText().toString().trim();
        String valorText = etValorAPagar.getText().toString().trim();
        String dataStr = etDataAPagar.getText().toString().trim();

        if (descricao.isEmpty()) {
            etDescricaoAPagar.setError("Informe a descrição");
            etDescricaoAPagar.requestFocus();
            return;
        }
        if (valorText.isEmpty()) {
            etValorAPagar.setError("Informe o valor");
            etValorAPagar.requestFocus();
            return;
        }
        if (dataStr.isEmpty()) {
            etDataAPagar.setError("Selecione a data");
            etDataAPagar.requestFocus();
            return;
        }

        try {
            double valor = Double.parseDouble(valorText);
            if (valor <= 0) {
                etValorAPagar.setError("O valor deve ser positivo");
                etValorAPagar.requestFocus();
                return;
            }
            Date data = dateFormat.parse(dataStr);
            if (data == null) {
                etDataAPagar.setError("Data inválida");
                etDataAPagar.requestFocus();
                return;
            }
            Transacao transacao = new Transacao(0, descricao, valor, "A Pagar", data);
            TransacaoRepository.addTransacao(transacao);
            Toast.makeText(this, "Transação 'A Pagar' registrada!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            etValorAPagar.setError("Valor inválido");
            etValorAPagar.requestFocus();
        } catch (ParseException e) {
            etDataAPagar.setError("Formato de data inválido (dd/MM/yyyy)");
            etDataAPagar.requestFocus();
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        String currentDateStr = etDataAPagar.getText().toString();
        if (!currentDateStr.isEmpty()) {
            try {
                Date currentDate = dateFormat.parse(currentDateStr);
                calendar.setTime(currentDate);
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
                    etDataAPagar.setText(dateString);
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}
