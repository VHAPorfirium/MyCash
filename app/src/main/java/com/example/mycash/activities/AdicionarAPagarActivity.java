package com.example.mycash.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return windowInsets;
        });

        etDescricaoAPagar = findViewById(R.id.etDescricaoAPagar);
        etValorAPagar = findViewById(R.id.etValorAPagar);
        etDataAPagar = findViewById(R.id.etDataAPagar);
        btnSalvarAPagar = findViewById(R.id.btnSalvarAPagar);

        etDataAPagar.setOnClickListener(view -> showDatePickerDialog());

        btnSalvarAPagar.setOnClickListener(view -> {
            try {
                String descricao = etDescricaoAPagar.getText().toString();
                double valor = Double.parseDouble(etValorAPagar.getText().toString());
                String dataStr = etDataAPagar.getText().toString();
                Date data = dateFormat.parse(dataStr);

                Transacao trans = new Transacao(0, descricao, valor, "A Pagar", data);
                TransacaoRepository.addTransacao(trans);

            } catch (ParseException | NumberFormatException e) {
                e.printStackTrace();
            }
            finish();
        });
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
        int year  = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day   = calendar.get(Calendar.DAY_OF_MONTH);

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
