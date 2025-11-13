package com.example.appingresosegresos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DetallesActivity extends AppCompatActivity {

    ListView listViewDetalles;
    Button btnEliminarDetalle, btnVolverDetalles;
    DBHelper dbHelper;
    List<String> detalles;
    ArrayAdapter<String> adapter;
    int selectedPosition = -1; // Para saber qué ítem se seleccionó

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles); // Asegúrate de que el archivo XML se llame así

        listViewDetalles = findViewById(R.id.listViewDetalles);
        btnEliminarDetalle = findViewById(R.id.btnEliminarDetalle);
        btnVolverDetalles = findViewById(R.id.btnVolverDetalles);
        dbHelper = new DBHelper(this);

        cargarDetalles();

        listViewDetalles.setOnItemClickListener((parent, view, position, id) -> {
            selectedPosition = position;
            listViewDetalles.setItemChecked(position, true);
            Toast.makeText(this, "Seleccionado: " + detalles.get(position), Toast.LENGTH_SHORT).show();
        });

        btnEliminarDetalle.setOnClickListener(v -> {
            if (selectedPosition == -1) {
                Toast.makeText(this, "Selecciona un movimiento primero", Toast.LENGTH_SHORT).show();
                return;
            }

            String seleccionado = detalles.get(selectedPosition);
            String idTexto = seleccionado.split("\\.")[0];
            try {
                int id = Integer.parseInt(idTexto);
                new AlertDialog.Builder(this)
                        .setTitle("Confirmar eliminación")
                        .setMessage("¿Eliminar el movimiento seleccionado?")
                        .setPositiveButton("Sí", (dialog, which) -> {
                            if (dbHelper.eliminarMovimiento(id)) {
                                Toast.makeText(this, "Movimiento eliminado", Toast.LENGTH_SHORT).show();
                                cargarDetalles();
                                selectedPosition = -1;
                            } else {
                                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "ID inválido", Toast.LENGTH_SHORT).show();
            }
        });

        btnVolverDetalles.setOnClickListener(v -> finish());
    }

    private void cargarDetalles() {
        detalles = dbHelper.obtenerDetallesBalance();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, detalles);
        listViewDetalles.setAdapter(adapter);
        listViewDetalles.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }
}
