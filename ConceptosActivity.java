package com.example.appingresosegresos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import java.util.List;

public class ConceptosActivity extends AppCompatActivity {

    EditText editId, editDescripcion;
    Spinner spinnerTipo;
    Button btnGuardar, btnModificar, btnListar, btnVolverMenu;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conceptos);

        editId = findViewById(R.id.editIdConcepto);
        editDescripcion = findViewById(R.id.editDescripcion);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        btnGuardar = findViewById(R.id.btnGuardarConcepto);
        btnModificar = findViewById(R.id.btnModificarConcepto);
        btnListar = findViewById(R.id.btnListarConceptos);
        btnVolverMenu = findViewById(R.id.btnVolverMenu);

        dbHelper = new DBHelper(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipos_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);

        btnGuardar.setOnClickListener(v -> {
            String descripcion = editDescripcion.getText().toString();
            String tipo = spinnerTipo.getSelectedItem().toString();
            if (dbHelper.insertConcepto(descripcion, tipo)) {
                Toast.makeText(this, "Concepto guardado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        });

        btnModificar.setOnClickListener(v -> {
            try {
                int id = Integer.parseInt(editId.getText().toString());
                String descripcion = editDescripcion.getText().toString();
                String tipo = spinnerTipo.getSelectedItem().toString();
                if (dbHelper.modificarConcepto(id, descripcion, tipo)) {
                    Toast.makeText(this, "Concepto modificado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Error al modificar", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "ID inválido", Toast.LENGTH_SHORT).show();
            }
        });

        btnListar.setOnClickListener(v -> {
            List<String> conceptos = dbHelper.listarConceptos();
            StringBuilder lista = new StringBuilder();
            for (String c : conceptos) {
                lista.append(c).append("\n");
            }
            new AlertDialog.Builder(this)
                    .setTitle("Conceptos")
                    .setMessage(lista.toString())
                    .setPositiveButton("OK", null)
                    .show();
        });

        btnVolverMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
}
