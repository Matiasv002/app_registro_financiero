package com.example.appingresosegresos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "Finanzas.db", null, 2); // Subir versión a 2 para ejecutar onUpgrade
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE conceptos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "descripcion TEXT, " +
                "tipo TEXT)");

        db.execSQL("CREATE TABLE movimientos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "concepto TEXT, " +
                "monto REAL, " +
                "fecha TEXT, " +
                "tipo TEXT, " +
                "gravado INTEGER, " + // 1 = true, 0 = false
                "iva REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS conceptos");
        db.execSQL("DROP TABLE IF EXISTS movimientos");
        onCreate(db);
    }

    public boolean insertConcepto(String descripcion, String tipo) {
        if (descripcion.isEmpty() || tipo.isEmpty()) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("descripcion", descripcion);
        values.put("tipo", tipo);
        long result = db.insert("conceptos", null, values);
        return result != -1;
    }

    public boolean modificarConcepto(int id, String descripcion, String tipo) {
        if (descripcion.isEmpty() || tipo.isEmpty()) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("descripcion", descripcion);
        values.put("tipo", tipo);
        int result = db.update("conceptos", values, "id=?", new String[]{String.valueOf(id)});
        return result > 0;
    }

    public List<String> listarConceptos() {
        List<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM conceptos", null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String desc = cursor.getString(1);
                String tipo = cursor.getString(2);
                String tipoCorto = tipo.equalsIgnoreCase("Ingreso") ? "I" : "E";
                lista.add(id + ". " + desc + " - " + tipoCorto);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public boolean insertMovimiento(String concepto, double monto, String fecha, String tipo, boolean gravado) {
        if (concepto.isEmpty() || fecha.isEmpty() || tipo.isEmpty() || monto == 0) return false;

        double iva = gravado ? monto * 0.1 : 0;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("concepto", concepto);
        values.put("monto", monto);
        values.put("fecha", fecha);
        values.put("tipo", tipo);
        values.put("gravado", gravado ? 1 : 0);
        values.put("iva", iva);
        long result = db.insert("movimientos", null, values);
        return result != -1;
    }

    public Cursor obtenerMovimientos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM movimientos", null);
    }

    public boolean eliminarMovimiento(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("movimientos", "id=?", new String[]{String.valueOf(id)}) > 0;
    }

    public double obtenerTotalPorTipo(String tipo) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(monto) FROM movimientos WHERE tipo=?", new String[]{tipo});
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public double obtenerTotalIVA() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(iva) FROM movimientos", null);
        double totalIVA = 0;
        if (cursor.moveToFirst()) {
            totalIVA = cursor.getDouble(0);
        }
        cursor.close();
        return totalIVA;
    }

    public int contarMovimientosGravados() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM movimientos WHERE gravado = 1", null);
        int cantidad = 0;
        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0);
        }
        cursor.close();
        return cantidad;
    }

    public List<String> obtenerDetallesBalance() {
        List<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT concepto, monto, fecha, tipo, gravado, iva FROM movimientos", null);
        if (cursor.moveToFirst()) {
            do {
                String concepto = cursor.getString(0);
                double monto = cursor.getDouble(1);
                String fecha = cursor.getString(2);
                String tipo = cursor.getString(3);
                int gravado = cursor.getInt(4);
                double iva = cursor.getDouble(5);
                String gStr = gravado == 1 ? "Gravado (IVA: $" + iva + ")" : "No Gravado";
                lista.add(fecha + " - " + concepto + " - $" + monto + " (" + tipo + ") - " + gStr);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }
}
