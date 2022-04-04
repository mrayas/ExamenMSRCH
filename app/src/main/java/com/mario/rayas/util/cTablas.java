package com.mario.rayas.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.google.gson.Gson;


import java.io.Serializable;
import java.util.ArrayList;

public final  class cTablas {
    private cTablas() {}



    public static class Datos implements BaseColumns {
        public static final String TABLE_NAME = "t_datos";

        public static final String ID = "id";
        public static final String NOMBRE = "nombre";
        public static final String APELLIDOS = "apellidos";
        public static final String TELEFONO = "telefono";
        public static final String CORREO_ELECTRONICO = "correo_electronico";
        public static final String FOTOGRAFIA = "fotografia";
        public static final String LATITUD= "latitud";
        public static final String LONGITUD="longitud";
        public static final String ALTITUD="altitud";
        public static final String CREADO = "creado";
        public static final String ACTUALIZADO = "actualizado";





        public static class cCoordenada implements Serializable{

            public String latitud = "";
            public String longitud = "";
            public String altitud = "";

            //public cNFCTag[] aoNFCTag;

            public static String fsJSON(cCoordenada oObjeto){
                String sJSON="";
                try {
                    Gson gJSON = new Gson();
                    sJSON = gJSON.toJson(oObjeto);
                } catch (Exception ex) {
                    Funciones.fEscribeLog("En cObjetos.cCoordenada.fsJSON exepcion:"+ex);
                    sJSON=null;
                }
                return sJSON;
            }



            public static cCoordenada foObjet(String sJSON){
                cCoordenada oObjeto=null;
                try {
                    Gson gJSON = new Gson();
                    oObjeto = gJSON.fromJson(sJSON, cCoordenada.class);
                } catch (Exception ex) {
                    Funciones.fEscribeLog("En cObjetos.cCoordenada.foObjet Exepcion:"+ex);
                    oObjeto=null;
                }
                return oObjeto;
            }
        }

        public static class cDato implements Serializable{
            public int id=-1;
            public String nombre = "";
            public String apellidos = "";
            public String telefono = "";
            public String correo_electronico = "";
            public String fotografia = "";
            public cCoordenada oCoordenada;
            public String creado="";
            public String actualizado="";


            public static String fsJSON(cDato oObjeto){
                String sJSON="";
                try {
                    Gson gJSON = new Gson();
                    sJSON = gJSON.toJson(oObjeto);
                } catch (Exception ex) {
                    Funciones.fEscribeLog("En cObjetos.cDato.foObjet exepcion:"+ex);
                    sJSON=null;
                }
                return sJSON;
            }

            public static String fsJSON(cDato[] aoObjeto){
                String sJSON="";
                try {
                    Gson gJSON = new Gson();
                    ArrayList<cDato> laObjeto = new ArrayList();
                    for (int i = 0; i < aoObjeto.length; i++) {
                        laObjeto.add(aoObjeto[i]);
                    }
                    sJSON = gJSON.toJson(laObjeto);
                } catch (Exception ex) {
                    sJSON=null;
                    Funciones.fEscribeLog("En cObjetos.cDato.fsJSON Exepcion:"+ex);
                }
                return sJSON;
            }



            public static cDato foObjet(String sJSON){
                cDato oObjeto=null;
                try {
                    Gson gJSON = new Gson();
                    oObjeto = gJSON.fromJson(sJSON, cDato.class);
                } catch (Exception ex) {
                    Funciones.fEscribeLog("En cObjetos.cDato.foObjet Exepcion:"+ex);
                    oObjeto=null;
                }
                return oObjeto;
            }

            public static cDato[] faoObjet(String sJSON){
                cDato[] aoObjeto =null;
                try {
                    Gson gJSON = new Gson();
                    aoObjeto  = gJSON.fromJson(sJSON, cDato[].class);
                } catch (Exception ex) {
                    Funciones.fEscribeLog("En cObjetos.cDato.faoObjet Exepcion:"+ex);
                    aoObjeto =null;
                }
                return aoObjeto ;
            }
        }
        com.mario.rayas.util.dbDatos oDBDatos;
        //dbDatos oDBDatos;
        SQLiteDatabase db;
        /** Constructor de clase */
        public Datos(Context context)
        {
            try {
                oDBDatos = new dbDatos( context );
            } catch (Exception e) {
                Funciones.fEscribeLog("En cTablas.Datos exepcion:"+e);
            }
        }

        public boolean abrir()
        {
            try {
                db = oDBDatos.getWritableDatabase();
                return true;
            } catch (Exception e) {
                Funciones.fEscribeLog("En cTablas.tMensajesQR.abrir exepcion:"+e);
                return false;
            }
        }

        public boolean cerrar()
        {
            try {
                oDBDatos.close();
                return true;
            } catch (Exception e) {
                Funciones.fEscribeLog("En cTablas.tMensajesQR.cerrar exepcion:"+e);
                return false;
            }
        }

        public cDato datoCursor(Cursor curDato)
        {
            cDato oObjeto=null;

            try {
                if (curDato!=null){
                    oObjeto=new cDato();
                    oObjeto.oCoordenada=new cCoordenada();
                    //String iD=curTransaccion.getString(curTransaccion.getColumnIndex(Transacciones.ID_CODIGO_RESPUESTA));

                    //oObjeto.id=curDato.getInt(0);
                    oObjeto.id=curDato.getInt(curDato.getColumnIndex(Datos.ID));
                    oObjeto.nombre=curDato.getString(curDato.getColumnIndex(Datos.NOMBRE));
                    oObjeto.apellidos=curDato.getString(curDato.getColumnIndex(Datos.APELLIDOS));
                    oObjeto.telefono=curDato.getString(curDato.getColumnIndex(Datos.TELEFONO));
                    oObjeto.correo_electronico=curDato.getString(curDato.getColumnIndex(Datos.CORREO_ELECTRONICO));
                    oObjeto.fotografia=curDato.getString(curDato.getColumnIndex(Datos.FOTOGRAFIA));
                    oObjeto.oCoordenada.latitud=curDato.getString(curDato.getColumnIndex(Datos.LATITUD));
                    oObjeto.oCoordenada.longitud=curDato.getString(curDato.getColumnIndex(Datos.LONGITUD));
                    oObjeto.oCoordenada.altitud=curDato.getString(curDato.getColumnIndex(Datos.ALTITUD));
                    oObjeto.creado=curDato.getString(curDato.getColumnIndex(Datos.CREADO));
                    oObjeto.actualizado=curDato.getString(curDato.getColumnIndex(Datos.ACTUALIZADO));


                }

            } catch (Exception e) {
                oObjeto=null;
                Funciones.fEscribeLog("En cTablas.Transaccion.transaccionCursor exepcion:"+e);
            }
            return oObjeto;

        }

        public cDato MaxID()
        {
            //Log.i("SQLite", "query -> Consulta solo registros sexo='Hombre' " );
            cDato oObjeto=null;
            try {

                String sSQL="SELECT "+Datos.ID+" FROM "+Datos.TABLE_NAME+
                        " Where rowid=(SELECT MAX(rowid) FROM "+Datos.TABLE_NAME+");";
                Cursor curObjeto= db.rawQuery(sSQL,null);

                if (curObjeto!=null &&  curObjeto.moveToFirst() )//Ojo
                    oObjeto=DatoId(curObjeto.getString(0));

            } catch (Exception e) {
                oObjeto=null;
                Funciones.fEscribeLog("En cTablas.Transaccion.MaxID exepcion:"+e);
            }
            return oObjeto;

        }

        public cDato NuevoDato(Context context)
        {

            cDato oObjeto=new cDato();
            try {

                cDato oUltimoDato=MaxID();
                if(oUltimoDato!=null)
                    oObjeto.id=oObjeto.id+1;
                else
                    oObjeto.id=1;


            } catch (Exception e) {
                oObjeto=null;
                Funciones.fEscribeLog("En cTablas.tMensajesQR.MensajeId exepcion:"+e);
            }
            return oObjeto;

        }

        public cDato DatoId(String sId)
        {
            cDato oObjeto=null;
            try {

                String sSQL="SELECT rowid,"+
                        Datos.ID+","+
                        Datos.NOMBRE+","+
                        Datos.APELLIDOS+","+
                        Datos.TELEFONO+","+
                        Datos.CORREO_ELECTRONICO+","+
                        Datos.FOTOGRAFIA+","+
                        Datos.LATITUD+","+
                        Datos.LONGITUD+","+
                        Datos.ALTITUD+","+
                        Datos.CREADO+","+
                        Datos.ACTUALIZADO+
                        " From "+Datos.TABLE_NAME+
                        " Where "+Datos.ID+"='"+sId+"'";

                Cursor curObjeto= db.rawQuery(sSQL,null);


                if (curObjeto!=null &&  curObjeto.moveToFirst() )
                    oObjeto=datoCursor(curObjeto);

            } catch (Exception e) {
                oObjeto=null;
                Funciones.fEscribeLog("En cTablas.cTransaccion.transaccionId exepcion:"+e);
            }
            return oObjeto;

        }





        public cDato[] DatosFechas(String FechaInicial,String FechaFinal)
        {
            //Log.i("SQLite", "query -> Consulta solo registros sexo='Hombre' " );
            cDato[] aoObjeto=null;
            try {

                String sCondicion="";
                if (!FechaInicial.equals(""))
                    sCondicion="DATE("+Datos.CREADO+")='"+FechaInicial+"'";

                String sSQL="SELECT "+
                        Datos.ID+
                        " From "+Datos.TABLE_NAME;

                if(!sCondicion.equals(""))
                    sSQL+=" Where "+sCondicion;


                Cursor curObjeto= db.rawQuery(sSQL,null);
                if (curObjeto!=null &&  curObjeto.moveToFirst() ){
                    aoObjeto=new cDato[curObjeto.getCount()];
                    int i=0;
                    do{
                        aoObjeto[i]=DatoId(curObjeto.getString(0));
                        i++;
                    }while( curObjeto.moveToNext() );
                }

            } catch (Exception e) {
                aoObjeto=null;
                Funciones.fEscribeLog("En cTablas.t_transacciones.TransaccionesFechas exepcion:"+e);
            }
            return aoObjeto;

        }

        public cDato insertar(cDato oObjeto)
        {

            long lRepuesta=0;
            try {

                cDato oUltimo = MaxID();
                oObjeto.id=1;
                if (oUltimo!=null) {
                    oObjeto.id=oUltimo.id+1;
                }

                if (oObjeto.creado==null || oObjeto.creado.equals("")) {
                    cObjetos.cFechaHora oFechaHora = new cObjetos.cFechaHora();
                    oFechaHora = oFechaHora.FechaHora("yyyy-MM-dd", "HH:mm:ss");
                    oObjeto.creado = oFechaHora.sFechaLocal + " " + oFechaHora.sHoraLocal;
                }


                //Archivos.escribeLog("INSERT: RFC" + pRFC + " Dato " + pDatoJSON);
                ContentValues contentValues = new ContentValues();
                contentValues.put( Datos.ID, oObjeto.id);
                contentValues.put( Datos.NOMBRE, oObjeto.nombre);
                contentValues.put( Datos.APELLIDOS, oObjeto.apellidos);
                contentValues.put( Datos.TELEFONO, oObjeto.telefono);
                contentValues.put( Datos.CORREO_ELECTRONICO, oObjeto.correo_electronico);
                contentValues.put( Datos.FOTOGRAFIA, oObjeto.fotografia);
                contentValues.put( Datos.LATITUD, oObjeto.oCoordenada.latitud);
                contentValues.put( Datos.LONGITUD, oObjeto.oCoordenada.longitud);
                contentValues.put( Datos.ALTITUD, oObjeto.oCoordenada.altitud);
                contentValues.put( Datos.CREADO, oObjeto.creado);
                contentValues.put( Datos.ACTUALIZADO, oObjeto.actualizado);

                //table, nullColumnHack, values
                lRepuesta = db.insert( Datos.TABLE_NAME,null,contentValues);
            } catch (Exception e) {
                Funciones.fEscribeLog("En cTablas.tMensajesQR.insertar exepcion:"+e);
                oObjeto.id= -1;
            }
            return oObjeto;
        }

        public long actualizar(cDato oObjeto)
        {


            long lRepuesta=0;
            try {

                if (oObjeto.actualizado==null || oObjeto.actualizado.equals("")) {
                    cObjetos.cFechaHora oFechaHora = new cObjetos.cFechaHora();
                    oFechaHora = oFechaHora.FechaHora("yyyy-MM-dd", "HH:mm:ss");
                    oObjeto.actualizado = oFechaHora.sFechaLocal + " " + oFechaHora.sHoraLocal;
                }


                ContentValues contentValues = new ContentValues();

                contentValues.put( Datos.NOMBRE, oObjeto.nombre);
                contentValues.put( Datos.APELLIDOS, oObjeto.apellidos);
                contentValues.put( Datos.TELEFONO, oObjeto.telefono);
                contentValues.put( Datos.CORREO_ELECTRONICO, oObjeto.correo_electronico);
                contentValues.put( Datos.FOTOGRAFIA, oObjeto.fotografia);
                contentValues.put( Datos.LATITUD, oObjeto.oCoordenada.latitud);
                contentValues.put( Datos.LONGITUD, oObjeto.oCoordenada.longitud);
                contentValues.put( Datos.ALTITUD, oObjeto.oCoordenada.altitud);
                contentValues.put( Datos.CREADO, oObjeto.creado);
                contentValues.put( Datos.ACTUALIZADO, oObjeto.actualizado);

                //table, nullColumnHack, values
                lRepuesta = db.update( Datos.TABLE_NAME,contentValues,Datos.ID + " = '" + oObjeto.id +"'",null);
            } catch (Exception e) {
                Funciones.fEscribeLog("En cTablas.Transacciones.actualizar.insertar exepcion:"+e);
                lRepuesta= -1;
            }
            return lRepuesta;
        }

        public int eliminar( String sId )
        {

            try {

                return db.delete( Datos.TABLE_NAME, Datos.ID + " = '" + sId +"'" ,  null);
            } catch (Exception e) {
                Funciones.fEscribeLog("En cTablas.Transacciones.eliminar exepcion:"+e);
                return -1;
            }
        }
    }
}

