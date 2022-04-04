package com.mario.rayas.util;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import com.google.gson.Gson;

public class cObjetos {
    public static class cVersion implements Serializable{
        public String sVersion="";
        public String sCompilacion="";
        public String sIPServidor="";
        public String sNombreServidor="";
        public String sDesarrollador="";
        public String sEmpresa="";
        public String sCorreo="";

        public static cVersion foObjet(String sJSON){
            cVersion oObjeto=null;
            try {
                Gson gJSON = new Gson();
                oObjeto = gJSON.fromJson(sJSON, cVersion.class);
            } catch (Exception ex) {
                Funciones.fEscribeLog("En cObjetos.cVersion.foObjet exepcion:"+ex);
                oObjeto=null;
            }
            return oObjeto;
        }
    }
    public static class cAplicacionAndroid implements Serializable {
        public String sVersion="";
        public int iCodigoVersion=-1;
        public String sCompilacion="";
        public  String sIdPaquete="";

        public static String fsJSON(cAplicacionAndroid oAplicacionAndroid){
            String sJSON="";
            try {
                Gson gJSON = new Gson();
                sJSON = gJSON.toJson(oAplicacionAndroid);
            } catch (Exception ex) {
                Funciones.fEscribeLog("En cObjetosWS.cAplicacionAndroid.foObjet Exepcion:"+ex);
                sJSON=null;
            }
            return sJSON;
        }

        public static cAplicacionAndroid  foObjet(String sJSON){
            cAplicacionAndroid  oAplicacionAndroid =null;
            try {
                Gson gJSON = new Gson();
                oAplicacionAndroid  = gJSON.fromJson(sJSON, cAplicacionAndroid .class);
            } catch (Exception ex) {
                Funciones.fEscribeLog("En cObjetosWS.cAplicacionAndroid.foObjet Exepcion:"+ex);
                oAplicacionAndroid =null;
            }
            return oAplicacionAndroid;
        }

    }
    public static class cFechaHora {
        public String sFechaLocal="";
        public String sFechaGMT="";
        public String sHoraLocal="";
        public String sHoraGMT="";
        public String sFormatoFecha="";
        public String sFormatoHora="";
        public int iDiferenciaGMT=0;

        public static cFechaHora FechaHora(String sFormatoFecha, String sFormatoHora){
            cFechaHora oFechaHora=new cFechaHora();
            oFechaHora.sFechaLocal=Funciones.fFecha(sFormatoFecha);
            oFechaHora.sFechaGMT=Funciones.fFechaGTM(sFormatoFecha);
            oFechaHora.sHoraLocal=Funciones.fHora(sFormatoHora);
            oFechaHora.sHoraGMT=Funciones.fHoraGTM(sFormatoHora);
            oFechaHora.iDiferenciaGMT=0;
            try {
                java.text.DateFormat dateFormat = new SimpleDateFormat(sFormatoFecha+" "+sFormatoHora);
                java.util.Date dFechaLocal = dateFormat.parse(oFechaHora.sFechaLocal+" "+oFechaHora.sHoraLocal);
                java.util.Date dFechaGTM = dateFormat.parse(oFechaHora.sFechaGMT+" "+oFechaHora.sHoraGMT);
                long lDireferncia=dFechaGTM.getTime()-dFechaLocal.getTime();
                lDireferncia=lDireferncia/(1000*60*60);
                lDireferncia=(lDireferncia)*(-1);
                oFechaHora.iDiferenciaGMT=new BigDecimal(lDireferncia).intValueExact();

            } catch (Exception ex) {
                oFechaHora.iDiferenciaGMT=0;
            }

            oFechaHora.sFormatoFecha=sFormatoFecha;
            oFechaHora.sFormatoHora=sFormatoHora;

            return oFechaHora;
        }
    }
    public static class cCodigoRespuesta {
        public String id_codigo_respuesta = "";
        public String codigo_respuesta = "";
        public String descripcion = "";
        public String id_codigo_respuesta_remota = "";
        public String codigo_respuesta_remota = "";
        public String descripcion_remota = "";

        public static cCodigoRespuesta[] faoObjet(String sJSON){
            cCodigoRespuesta[] aoObjeto=null;
            try {
                Gson gJSON = new Gson();
                aoObjeto = gJSON.fromJson(sJSON, cCodigoRespuesta[].class);
            } catch (Exception ex) {
                Funciones.fEscribeLog("En cObjetos.cCodigoRespuesta.faoObjet exepcion:"+ex);
                aoObjeto=null;
            }
            return aoObjeto;
        }

        public static String fsJSON(cCodigoRespuesta oObjeto){
            String sJSON="";
            try {
                Gson gJSON = new Gson();
                sJSON = gJSON.toJson(oObjeto);
            } catch (Exception ex) {
                Funciones.fEscribeLog("En cObjetos.cCodigoRespuesta.foObjet exepcion:"+ex);
                sJSON=null;
            }
            return sJSON;
        }

        public static String fsJSON(cCodigoRespuesta[] aObjeto){
            String sJSON="";
            try {
                Gson gJSON = new Gson();
                ArrayList<cCodigoRespuesta> laObjeto = new ArrayList();
                for (int i = 0; i < aObjeto.length; i++) {
                    laObjeto.add(aObjeto[i]);
                }
                sJSON = gJSON.toJson(laObjeto);
            } catch (Exception ex) {
                sJSON=null;
                Funciones.fEscribeLog("En cObjetos.cCodigoRespuesta.fsJSON Exepcion:"+ex);
            }
            return sJSON;
        }

        public static cCodigoRespuesta foCodigoRespuesta(String idCodigoRespuesta){
            cCodigoRespuesta oObjeto =new cCodigoRespuesta();
            try {
                String sJSON_CodigosRespuesta="[{\n" +
                        "id_codigo_respuesta:\"00\",\n" +
                        "codigo_respuesta:\"Exito\",\n" +
                        "descripcion:\"Operación exitosa\"\n" +
                        "},\n" +
                        "{\n" +
                        "id_codigo_respuesta:\"01\",\n" +
                        "codigo_respuesta:\"Error en BD\",\n" +
                        "descripcion:\"No se pudo abrir la base de datos\"\n" +
                        "},\n" +
                        "{\n" +
                        "id_codigo_respuesta:\"EX\",\n" +
                        "codigo_respuesta:\"Exepcion\",\n" +
                        "descripcion:\"\"\n" +
                        "},\n" +
                        "{\n" +
                        "id_codigo_respuesta:\"EA\",\n" +
                        "codigo_respuesta:\"Error \",\n" +
                        "descripcion:\"Revise su usuario y/o password\"\n" +
                        "}]";
                cCodigoRespuesta[] aoObjeto = faoObjet(sJSON_CodigosRespuesta);

                if (aoObjeto!=null) {
                    for (int i=0;i<aoObjeto.length;i++){
                        if (aoObjeto[i].id_codigo_respuesta.equals(idCodigoRespuesta))
                            return aoObjeto[i];
                    }
                }else{
                    oObjeto.id_codigo_respuesta="--";
                    oObjeto.codigo_respuesta="Código no definido";
                    oObjeto.id_codigo_respuesta="El código de respuesta "+idCodigoRespuesta+" no está definido";
                }

            }catch (Exception e){
                oObjeto = null;
            }

            return oObjeto;
        }
    }

    public static class cRespuesta {
        public cCodigoRespuesta oCodigoRespuesta = null;
        public String sXML = "";
        public String sHTML = "";
        public String sJSON = "";
        public String[] asExtra = null;

        public static String fsJSON(cRespuesta oObjeto){
            String sJSON="";
            try {
                Gson gJSON = new Gson();
                sJSON = gJSON.toJson(oObjeto);
            } catch (Exception ex) {
                Funciones.fEscribeLog("En cObjetos.cRespuesta.foObjet exepcion:"+ex);
                sJSON=null;
            }
            return sJSON;
        }

        public String fsJSON(cRespuesta[] aObjeto){
            String sJSON="";
            try {
                Gson gJSON = new Gson();
                ArrayList<cRespuesta> laObjeto = new ArrayList();
                for (int i = 0; i < aObjeto.length; i++) {
                    laObjeto.add(aObjeto[i]);
                }
                sJSON = gJSON.toJson(laObjeto);
            } catch (Exception ex) {
                sJSON=null;
                Funciones.fEscribeLog("En cObjetos.cRespuesta.fsJSON Exepcion:"+ex);
            }
            return sJSON;
        }

        public static cRespuesta foObjet(String sJSON){
            cRespuesta oObjeto=null;
            try {
                Gson gJSON = new Gson();
                oObjeto = gJSON.fromJson(sJSON, cRespuesta.class);
            } catch (Exception ex) {
                Funciones.fEscribeLog("En cObjetos.cRespuesta.foObjet exepcion:"+ex);
                oObjeto=null;
            }
            return oObjeto;
        }
        public static cRespuesta[] faoObjet(String sJSON){
            cRespuesta[] aoObjeto=null;
            try {
                Gson gJSON = new Gson();
                aoObjeto = gJSON.fromJson(sJSON, cRespuesta[].class);
            } catch (Exception ex) {
                Funciones.fEscribeLog("En cObjetos.cRespuesta.faoObjet exepcion:"+ex);
                aoObjeto=null;
            }
            return aoObjeto;
        }
    }


}
