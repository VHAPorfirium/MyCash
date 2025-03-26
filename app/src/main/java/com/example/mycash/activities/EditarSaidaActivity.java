package com.example.mycash.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class EditarSaidaActivity extends AppCompatActivity {

    private EditText etDescricaoSaida, etValorSaida, etDataSaida;
    private RadioGroup rgTipoSaida;
    private RadioButton rbCreditoSaida, rbDebitoSaida;
    private Button btnSalvarEdicaoSaida;
    private Transacao transacao;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_saida);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etDescricaoSaida = findViewById(R.id.etDescricaoSaida);
        etValorSaida = findViewById(R.id.etValorSaida);
        etDataSaida = findViewById(R.id.etDataSaida);
        rgTipoSaida = findViewById(R.id.rgTipoSaida);
        rbCreditoSaida = findViewById(R.id.rbCreditoSaida);
        rbDebitoSaida = findViewById(R.id.rbDebitoSaida);
        btnSalvarEdicaoSaida = findViewById(R.id.btnSalvarEdicaoSaida);

        int transacaoId = getIntent().getIntExtra("transacao_id", -1);
        transacao = TransacaoRepository.getTransacaoById(transacaoId);

        if (transacao != null) {
            etDescricaoSaida.setText(transacao.getDescricao());
            // Exibe o valor como absoluto para edição
            etValorSaida.setText(String.valueOf(Math.abs(transacao.getValor())));
            if ("Crédito".equalsIgnoreCase(transacao.getTipo())) {
                rbCreditoSaida.setChecked(true);
            } else if ("Débito".equalsIgnoreCase(transacao.getTipo())) {
                rbDebitoSaida.setChecked(true);
            }
            etDataSaida.setText(dateFormat.format(transacao.getData()));
        }

        etDataSaida.setOnClickListener(v -> showDatePickerDialog());

        btnSalvarEdicaoSaida.setOnClickListener(v -> atualizarTransacao());
    }

    private void atualizarTransacao() {
        String novaDescricao = etDescricaoSaida.getText().toString().trim();
        String valorText = etValorSaida.getText().toString().trim();
        String novaDataStr = etDataSaida.getText().toString().trim();

        if (novaDescricao.isEmpty()) {
            etDescricaoSaida.setError("Informe a descrição");
            etDescricaoSaida.requestFocus();
            return;
        }
        if (valorText.isEmpty()) {
            etValorSaida.setError("Informe o valor");
            etValorSaida.requestFocus();
            return;
        }
        if (novaDataStr.isEmpty()) {
            etDataSaida.setError("Selecione a data");
            etDataSaida.requestFocus();
            return;
        }

        try {
            // Garante que o valor seja negativo
            double novoValor = -Math.abs(Double.parseDouble(valorText));
            int selectedId = rgTipoSaida.getCheckedRadioButtonId();
            String novoTipo = "";
            if (selectedId == rbCreditoSaida.getId()) {
                novoTipo = "Crédito";
            } else if (selectedId == rbDebitoSaida.getId()) {
                novoTipo = "Débito";
            }
            Date novaData = dateFormat.parse(novaDataStr);
            if (novaData == null) {
                etDataSaida.setError("Data inválida");
                etDataSaida.requestFocus();
                return;
            }
            transacao.setDescricao(novaDescricao);
            transacao.setValor(novoValor);
            transacao.setTipo(novoTipo);
            transacao.setData(novaData);
            Toast.makeText(this, "Saída atualizada!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            etValorSaida.setError("Valor inválido");
            etValorSaida.requestFocus();
        } catch (ParseException e) {
            etDataSaida.setError("Formato de data inválido (dd/MM/yyyy)");
            etDataSaida.requestFocus();
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        String currentDateStr = etDataSaida.getText().toString();
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
                    etDataSaida.setText(dateString);
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}
