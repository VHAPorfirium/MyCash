package com.example.mycash.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

        // Configura o campo de data para abrir um DatePickerDialog ao clicar
        etData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        // Configura o clique do botão Salvar
        btnSalvarEntrada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String descricao = etDescricao.getText().toString();
                double valor = Double.parseDouble(etValor.getText().toString());
                String tipo = etTipo.getText().toString(); // sempre "Entrada"
                String dataStr = etData.getText().toString();
                Date data = null;
                try {
                    data = sdf.parse(dataStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // Cria o objeto Transacao e adiciona no repositório
                Transacao novaTransacao = new Transacao(0, descricao, valor, tipo, data);
                TransacaoRepository.addTransacao(novaTransacao);
                finish();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // O mês é indexado a partir de 0
                        String dateString = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        etData.setText(dateString);
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}
