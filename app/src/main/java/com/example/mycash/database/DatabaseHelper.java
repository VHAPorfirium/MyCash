package com.example.mycash.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "mycash.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_TRANSACOES = "transacoes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DESCRICAO = "descricao";
    public static final String COLUMN_VALOR = "valor";
    public static final String COLUMN_TIPO = "tipo";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_CATEGORIA = "categoria";
    public static final String COLUMN_FORMA_PAGAMENTO = "forma_pagamento";

    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_TRANSACOES + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DESCRICAO + " TEXT, "
            + COLUMN_VALOR + " REAL, "
            + COLUMN_TIPO + " TEXT, "
            + COLUMN_DATA + " INTEGER, "
            + COLUMN_CATEGORIA + " TEXT, "
            + COLUMN_FORMA_PAGAMENTO + " TEXT"
            + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Para simplificar, descartamos a tabela antiga e criamos uma nova.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACOES);
        onCreate(db);
    }
}
