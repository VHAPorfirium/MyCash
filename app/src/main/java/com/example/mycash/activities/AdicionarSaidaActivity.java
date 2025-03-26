package com.example.mycash.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

public class AdicionarSaidaActivity extends AppCompatActivity {

    private EditText etDescricao, etValor, etData;
    private Spinner spTipoSaida;
    private Button btnSalvarSaida;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adicionar_saida);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etDescricao = findViewById(R.id.etDescricao);
        etValor = findViewById(R.id.etValor);
        etData = findViewById(R.id.etData);
        spTipoSaida = findViewById(R.id.spTipoSaida);
        btnSalvarSaida = findViewById(R.id.btnSalvarSaida);

        // Configura o Spinner com as opções "Crédito" e "Débito"
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(
                this,
                R.array.array_tipo_saida,
                android.R.layout.simple_spinner_item
        );
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipoSaida.setAdapter(adapterSpinner);

        // Define data atual se o campo estiver vazio
        if (etData.getText().toString().trim().isEmpty()) {
            etData.setText(dateFormat.format(new Date()));
        }

        etData.setOnClickListener(view -> showDatePickerDialog());

        btnSalvarSaida.setOnClickListener(view -> salvarSaida());
    }

    private void salvarSaida() {
        String descricao = etDescricao.getText().toString().trim();
        String valorText = etValor.getText().toString().trim();
        String dataStr = etData.getText().toString().trim();
        String tipo = spTipoSaida.getSelectedItem().toString();

        if (descricao.isEmpty()) {
            etDescricao.setError("Informe a descrição");
            etDescricao.requestFocus();
            return;
        }
        if (valorText.isEmpty()) {
            etValor.setError("Informe o valor");
            etValor.requestFocus();
            return;
        }
        if (dataStr.isEmpty()) {
            etData.setError("Selecione a data");
            etData.requestFocus();
            return;
        }

        try {
            // Força o valor a ser negativo
            double valor = -Math.abs(Double.parseDouble(valorText));
            Date data = dateFormat.parse(dataStr);
            if (data == null) {
                etData.setError("Data inválida");
                etData.requestFocus();
                return;
            }
            Transacao novaSaida = new Transacao(0, descricao, valor, tipo, data);
            TransacaoRepository.addTransacao(novaSaida);
            Toast.makeText(this, "Saída registrada com sucesso!", Toast.LENGTH_SHORT).show();
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
                    etData.setText(dateString);
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}
