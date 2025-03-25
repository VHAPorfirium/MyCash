package com.example.mycash.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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

public class AdicionarSaidaActivity extends AppCompatActivity {

    private EditText etDescricao, etValor, etData;
    private Spinner spTipoSaida;
    private Button btnSalvarSaida;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adicionar_saida);

        // Ajusta os insets para acomodar as barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Vincula as views
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

        // Configura o campo de data para abrir um DatePickerDialog ao ser clicado
        etData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        // Ao clicar no botão Salvar, coleta os dados, cria a saída e persiste na repository
        btnSalvarSaida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String descricao = etDescricao.getText().toString();
                    String valorText = etValor.getText().toString();
                    // Força o valor a ser negativo, mesmo que o usuário não insira o sinal
                    double valor = -Math.abs(Double.parseDouble(valorText));
                    String tipo = spTipoSaida.getSelectedItem().toString();
                    String dataStr = etData.getText().toString();
                    Date data = dateFormat.parse(dataStr);

                    // Cria o objeto Transacao (id 0 para indicar novo registro; repository atribuirá o id)
                    Transacao novaSaida = new Transacao(0, descricao, valor, tipo, data);
                    // Salva na repository
                    TransacaoRepository.addTransacao(novaSaida);
                } catch (ParseException | NumberFormatException e) {
                    e.printStackTrace();
                    // Aqui você pode exibir um erro para o usuário se a data ou o valor forem inválidos
                }
                finish(); // Fecha a Activity após salvar
            }
        });
    }

    // Exibe um DatePickerDialog para seleção da data
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year  = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day   = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Lembre-se: o mês é indexado em 0 (janeiro = 0)
                        String dateString = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        etData.setText(dateString);
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}
