package com.mario.rayas.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;



/**
 * Created by mrayas on 14/08/2016.
 */
public class cDatosPersistentes {
    private static final String  sNombreDatosPersistentes="json_datos_persistentes";
    public String sVersionCompilacion="20200212";
    public  String sJSON_Usuario="";
    public  String sIdTerminal="";
    public  String sIdModeloTerminal="";


    public  double dLatitud=0.00;
    public  double dLongitud=0.00;
    public  double dAltitud=0.00;
    public  String sUbicacion="";
    public  String sJSONVersionWS="";



    public static String fsJSON(cDatosPersistentes oDatosPersistentes){
        String sJSON="";
        try {
            Gson gJSON = new Gson();
            sJSON = gJSON.toJson(oDatosPersistentes);
        } catch (Exception ex) {
            Funciones.fEscribeLog("En cDatosPersistentes.foObjet Exepcion:"+ex);
            sJSON=null;
        }
        return sJSON;
    }
    public static cDatosPersistentes foObjet(String sJSON){
        cDatosPersistentes oDatosPersistentes=null;
        try {
            Gson gJSON = new Gson();
            oDatosPersistentes = gJSON.fromJson(sJSON, cDatosPersistentes.class);
        } catch (Exception ex) {
            Funciones.fEscribeLog("En cDatosPersistentes.foObjet Exepcion:"+ex);
            oDatosPersistentes=null;
        }
        return oDatosPersistentes;
    }

    public static boolean fBorra(Context context){
        boolean bOk=false;
        try {
            SharedPreferences.Editor eDatosPersistentes = PreferenceManager.getDefaultSharedPreferences(context).edit();
            eDatosPersistentes.putString(sNombreDatosPersistentes, null);
            eDatosPersistentes.apply();
            eDatosPersistentes.commit();
            bOk= true;
        }catch (Exception e){
            bOk= false;
        }
        return bOk;
    }


    public static boolean fBorraDatosBase64(cDatosPersistentes oDatosPersistentes,Context context){
        boolean bOk=false;
        try {

            String sJSON = cDatosPersistentes.fsJSON(oDatosPersistentes);
            SharedPreferences.Editor eDatosPersistentes = PreferenceManager.getDefaultSharedPreferences(context).edit();
            eDatosPersistentes.putString(sNombreDatosPersistentes,sJSON);
            eDatosPersistentes.apply();
            eDatosPersistentes.commit();
            bOk= true;
        }catch (Exception e){
            bOk= false;
        }
        return bOk;
    }

    public static boolean fAlmacenaDatos(cDatosPersistentes oDatosPersistentes,Context context){
        boolean bOk=false;
        try {
            String sJSON = cDatosPersistentes.fsJSON(oDatosPersistentes);
            SharedPreferences.Editor eDatosPersistentes = PreferenceManager.getDefaultSharedPreferences(context).edit();
            eDatosPersistentes.putString(sNombreDatosPersistentes,sJSON);
            eDatosPersistentes.apply();
            eDatosPersistentes.commit();
            bOk= true;
        }catch (Exception e){
            bOk= false;
        }
        return bOk;
    }

    public static cDatosPersistentes fObtieneDatos(Context context){
        cDatosPersistentes oDatosPersistentes=null;
        try {
            SharedPreferences apDatosPersistentes = PreferenceManager.getDefaultSharedPreferences(context);
            String sJSON = apDatosPersistentes.getString(sNombreDatosPersistentes,null);
            if (sJSON!=null)
                oDatosPersistentes=cDatosPersistentes.foObjet(sJSON);

        }catch (Exception e){
            oDatosPersistentes=null;
        }
        return oDatosPersistentes;
    }

    public static cTablas.Datos.cDato foUsuario(Context context){
        cTablas.Datos.cDato  oDatoUsuario=null;
        try {
            SharedPreferences apDatosPersistentes = PreferenceManager.getDefaultSharedPreferences(context);
            String sJSON = apDatosPersistentes.getString(sNombreDatosPersistentes,null);
            if (sJSON!=null) {
                cDatosPersistentes oDatosPersistentes = cDatosPersistentes.foObjet(sJSON);
                oDatoUsuario = cTablas.Datos.cDato .foObjet(oDatosPersistentes.sJSON_Usuario);
            }

        }catch (Exception e){
            oDatoUsuario=null;
        }
        return oDatoUsuario ;
    }



    public static cObjetos.cVersion foVersion(Context context){
        cObjetos.cVersion oObjeto=null;
        try {
            SharedPreferences apDatosPersistentes = PreferenceManager.getDefaultSharedPreferences(context);
            String sJSON = apDatosPersistentes.getString(sNombreDatosPersistentes,null);
            if (sJSON!=null) {
                cDatosPersistentes oDatosPersistentes = cDatosPersistentes.foObjet(sJSON);
                oObjeto = cObjetos.cVersion.foObjet(oDatosPersistentes.sJSONVersionWS);
            }

        }catch (Exception e){
            oObjeto=null;
        }
        return oObjeto ;
    }

    public static String fsTerminal(Context context){
        String sTerminal=null;
        try {
            SharedPreferences apDatosPersistentes = PreferenceManager.getDefaultSharedPreferences(context);
            String sJSON = apDatosPersistentes.getString(sNombreDatosPersistentes,null);
            if (sJSON!=null) {
                cDatosPersistentes oDatosPersistentes = cDatosPersistentes.foObjet(sJSON);
                sTerminal = oDatosPersistentes.sIdTerminal;
            }

        }catch (Exception e){
            sTerminal=null;
        }
        return sTerminal;
    }

    public static String fsModeloTerminal(Context context){
        String sModeloTerminal=null;
        try {
            SharedPreferences apDatosPersistentes = PreferenceManager.getDefaultSharedPreferences(context);
            String sJSON = apDatosPersistentes.getString(sNombreDatosPersistentes,null);
            if (sJSON!=null) {
                cDatosPersistentes oDatosPersistentes = cDatosPersistentes.foObjet(sJSON);
                sModeloTerminal = oDatosPersistentes.sIdModeloTerminal;
            }

        }catch (Exception e){
            sModeloTerminal=null;
        }
        return sModeloTerminal;
    }


}

