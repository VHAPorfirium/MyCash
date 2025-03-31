package com.example.mycash.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mycash.model.Transacao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransacaoDAO {

    private DatabaseHelper dbHelper;

    public TransacaoDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long addTransacao(Transacao transacao) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DESCRICAO, transacao.getDescricao());
        values.put(DatabaseHelper.COLUMN_VALOR, transacao.getValor());
        values.put(DatabaseHelper.COLUMN_TIPO, transacao.getTipo());
        values.put(DatabaseHelper.COLUMN_DATA, transacao.getData().getTime());
        values.put(DatabaseHelper.COLUMN_CATEGORIA, transacao.getCategoria());
        values.put(DatabaseHelper.COLUMN_FORMA_PAGAMENTO, transacao.getFormaPagamento());
        long id = db.insert(DatabaseHelper.TABLE_TRANSACOES, null, values);
        db.close();
        return id;
    }

    public int updateTransacao(Transacao transacao) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DESCRICAO, transacao.getDescricao());
        values.put(DatabaseHelper.COLUMN_VALOR, transacao.getValor());
        values.put(DatabaseHelper.COLUMN_TIPO, transacao.getTipo());
        values.put(DatabaseHelper.COLUMN_DATA, transacao.getData().getTime());
        values.put(DatabaseHelper.COLUMN_CATEGORIA, transacao.getCategoria());
        values.put(DatabaseHelper.COLUMN_FORMA_PAGAMENTO, transacao.getFormaPagamento());
        int rowsAffected = db.update(DatabaseHelper.TABLE_TRANSACOES, values,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(transacao.getId())});
        db.close();
        return rowsAffected;
    }

    public int deleteTransacao(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(DatabaseHelper.TABLE_TRANSACOES,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected;
    }

    public Transacao getTransacaoById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_TRANSACOES,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);
        Transacao transacao = null;
        if (cursor != null && cursor.moveToFirst()) {
            transacao = cursorToTransacao(cursor);
            cursor.close();
        }
        db.close();
        return transacao;
    }

    public List<Transacao> getTransacoes() {
        List<Transacao> transacoes = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_TRANSACOES,
                null, null, null, null, null,
                DatabaseHelper.COLUMN_DATA + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Transacao t = cursorToTransacao(cursor);
                transacoes.add(t);
            }
            cursor.close();
        }
        db.close();
        return transacoes;
    }

    private Transacao cursorToTransacao(Cursor cursor) {
        Transacao t = new Transacao();
        t.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)));
        t.setDescricao(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRICAO)));
        t.setValor(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_VALOR)));
        t.setTipo(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TIPO)));
        long dataLong = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATA));
        t.setData(new Date(dataLong));
        t.setCategoria(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CATEGORIA)));
        t.setFormaPagamento(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FORMA_PAGAMENTO)));
        return t;
    }

    public double getTotalEntradas() {
        double total = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + DatabaseHelper.COLUMN_VALOR + ") as total FROM "
                        + DatabaseHelper.TABLE_TRANSACOES
                        + " WHERE " + DatabaseHelper.COLUMN_VALOR + " > 0", null);
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
            cursor.close();
        }
        db.close();
        return total;
    }

    public double getTotalSaidas() {
        double total = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(ABS(" + DatabaseHelper.COLUMN_VALOR + ")) as total FROM "
                        + DatabaseHelper.TABLE_TRANSACOES
                        + " WHERE " + DatabaseHelper.COLUMN_VALOR + " < 0", null);
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
            cursor.close();
        }
        db.close();
        return total;
    }

    public double getSaldo() {
        return getTotalEntradas() - getTotalSaidas();
    }
}
