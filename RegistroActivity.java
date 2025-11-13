package com.example.appingresosegresos;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RegistroActivity extends AppCompatActivity {

    CheckBox checkGravado;
    EditText editMonto; 
    Spinner spinnerConceptos;
    TextView txtFechaSeleccionada;
    Button btnCargar, btnSeleccionarFecha, btnVolver;
    DBHelper dbHelper;
    String fechaSeleccionada = "";
    ArrayAdapter<String> adapterConceptos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        checkGravado = findViewById(R.id.checkGravado);
        editMonto = findViewById(R.id.editMonto);
        spinnerConceptos = findViewById(R.id.spinnerConceptos);
        txtFechaSeleccionada = findViewById(R.id.txtFechaSeleccionada);
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha);
        btnCargar = findViewById(R.id.btnCargar);
        btnVolver = findViewById(R.id.btnVolverRegistro);

        dbHelper = new DBHelper(this);

        // Cargar conceptos
        adapterConceptos = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dbHelper.listarConceptos());
        adapterConceptos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerConceptos.setAdapter(adapterConceptos);

        // Selector de fecha
        btnSeleccionarFecha.setOnClickListener(v -> {
            Calendar calendario = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        fechaSeleccionada = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        txtFechaSeleccionada.setText("Fecha: " + fechaSeleccionada);
                    },
                    calendario.get(Calendar.YEAR),
                    calendario.get(Calendar.MONTH),
                    calendario.get(Calendar.DAY_OF_MONTH)
            );
            datePicker.show();
        });

        // Botón cargar
        btnCargar.setOnClickListener(v -> {
            String montoStr = editMonto.getText().toString();
            if (montoStr.isEmpty() || spinnerConceptos.getSelectedItem() == null || fechaSeleccionada.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double monto = Double.parseDouble(montoStr);
            String conceptoSeleccionado = spinnerConceptos.getSelectedItem().toString();
            String[] partes = conceptoSeleccionado.split("\\. | - ");

            if (partes.length >= 3) {
                String concepto = partes[1].trim();
                String tipo = partes[2].equals("I") ? "Ingreso" : "Egreso";
                boolean gravado = checkGravado.isChecked();

                boolean insertado = dbHelper.insertMovimiento(concepto, monto, fechaSeleccionada, tipo, gravado);
                if (insertado) {
                    Toast.makeText(this, "Movimiento registrado", Toast.LENGTH_SHORT).show();
                    editMonto.setText("");
                    fechaSeleccionada = "";
                    txtFechaSeleccionada.setText("Fecha:");
                    checkGravado.setChecked(false);
                } else {
                    Toast.makeText(this, "Error al registrar movimiento", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Concepto inválido", Toast.LENGTH_SHORT).show();
            }
        });

        btnVolver.setOnClickListener(v -> finish());
    }
}
