package com.example.mycash.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private Transacao transacao; // Objeto que será editado
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

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

        // Vincula as views
        etDescricaoSaida = findViewById(R.id.etDescricaoSaida);
        etValorSaida = findViewById(R.id.etValorSaida);
        etDataSaida = findViewById(R.id.etDataSaida);
        rgTipoSaida = findViewById(R.id.rgTipoSaida);
        rbCreditoSaida = findViewById(R.id.rbCreditoSaida);
        rbDebitoSaida = findViewById(R.id.rbDebitoSaida);
        btnSalvarEdicaoSaida = findViewById(R.id.btnSalvarEdicaoSaida);

        // Recupera o ID da transação a partir do Intent e carrega os dados da repository
        int transacaoId = getIntent().getIntExtra("transacao_id", -1);
        transacao = TransacaoRepository.getTransacaoById(transacaoId);

        if (transacao != null) {
            etDescricaoSaida.setText(transacao.getDescricao());
            // Garante que o valor seja negativo; exibe valor absoluto para edição
            String valorStr = String.valueOf(Math.abs(transacao.getValor()));
            etValorSaida.setText(valorStr);
            // Seleciona o RadioButton de acordo com o tipo salvo ("Crédito" ou "Débito")
            if ("Crédito".equalsIgnoreCase(transacao.getTipo())) {
                rbCreditoSaida.setChecked(true);
            } else if ("Débito".equalsIgnoreCase(transacao.getTipo())) {
                rbDebitoSaida.setChecked(true);
            }
            // Preenche o campo de data
            String dateString = dateFormat.format(transacao.getData());
            etDataSaida.setText(dateString);
        }

        // Configura o campo de data para abrir um DatePickerDialog
        etDataSaida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        // Configura o clique do botão Salvar
        btnSalvarEdicaoSaida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String novaDescricao = etDescricaoSaida.getText().toString();
                String valorTexto = etValorSaida.getText().toString();
                // Garante que o valor possua sinal negativo
                double novoValor = -Math.abs(Double.parseDouble(valorTexto));
                // Obtém o tipo selecionado no RadioGroup
                String novoTipo = "";
                int selectedId = rgTipoSaida.getCheckedRadioButtonId();
                if (selectedId == rbCreditoSaida.getId()) {
                    novoTipo = "Crédito";
                } else if (selectedId == rbDebitoSaida.getId()) {
                    novoTipo = "Débito";
                }
                String novaDataStr = etDataSaida.getText().toString();
                Date novaData = null;
                try {
                    novaData = dateFormat.parse(novaDataStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // Atualiza o objeto transacao e, se necessário, salve as alterações na repository
                transacao.setDescricao(novaDescricao);
                transacao.setValor(novoValor);
                transacao.setTipo(novoTipo);
                if (novaData != null) {
                    transacao.setData(novaData);
                }
                // Em uma implementação real, você pode chamar um método update na repository

                finish();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year  = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day   = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String dateString = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    etDataSaida.setText(dateString);
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}
