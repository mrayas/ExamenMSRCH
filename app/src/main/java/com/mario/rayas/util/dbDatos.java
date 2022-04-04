package com.mario.rayas.util;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mario.rayas.util.*;
import com.mario.rayas.util.Funciones;
public class dbDatos extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dbDatos.db";

    public dbDatos(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE " + cTablas.Datos.TABLE_NAME + " (" +
                    cTablas.Datos.ID + " STRING PRIMARY KEY," +
                    cTablas.Datos.NOMBRE+" STRING,"+
                    cTablas.Datos.APELLIDOS+" STRING,"+
                    cTablas.Datos.TELEFONO+" STRING,"+
                    cTablas.Datos.CORREO_ELECTRONICO+" STRING,"+
                    cTablas.Datos.FOTOGRAFIA+" STRING,"+
                    cTablas.Datos.LATITUD+" STRING,"+
                    cTablas.Datos.LONGITUD+" STRING,"+
                    cTablas.Datos.ALTITUD+" STRING,"+
                    cTablas.Datos.CREADO+ " TEXT,"+
                    cTablas.Datos.ACTUALIZADO + " TEXT)");
        } catch (Exception ex) {
            Funciones.fEscribeLog("En dbSmartQoDi.onCreate exepcion:"+ex);

        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + cTablas.Datos.TABLE_NAME);
            onCreate(db);
        } catch (Exception ex) {
            Funciones.fEscribeLog("En dbSmartQoDi.onUpgrade exepcion:"+ex);

        }
    }
}
