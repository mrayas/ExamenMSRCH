package com.mario.rayas.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
//import android.app.Notification;
//import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;

//import java.io.BufferedInputStream;
//import java.io.FileInputStream;
import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.location.Address;


import android.location.Geocoder;
import java.util.Locale;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import java.math.BigDecimal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.text.NumberFormat;
import java.text.DecimalFormat;
//import java.util.logging.Logger;

import android.os.Environment;

//import com.google.android.gms.maps.model.PolylineOptions;
//import com.google.android.gms.maps.model.LatLng;

//import com.ryac.smarttaxi.conductor.R;


import android.os.Build;

import com.mario.rayas.R;

import org.apache.commons.codec.binary.Base64;

public class Funciones {
    public enum enMetodo {
        ValidaUsuario,AlmacenaDato,ObtieneDatos
    }

    static String Carpeta = "/MarioRayasTest/";

    //static String Carpeta ="/"+getString(R.string.title_activity_inbox)+"/";
    public static String fFecha(String sFormato) {
        String sFecha = "";
        try {

            SimpleDateFormat sFormatoFechaLocal = new SimpleDateFormat(sFormato);
            sFecha = sFormatoFechaLocal.format(new Date());

        } catch (Exception e) {
            fEscribeLog("En fFecha excepción: " + e.toString());
            return "";
        }

        return sFecha;
    }

    public static String fHora(String sFormato) {
        String sHora = "";
        try {

            SimpleDateFormat sFormatoHoraLocal = new SimpleDateFormat(sFormato);
            sHora = sFormatoHoraLocal.format(new Date());
        } catch (Exception e) {
            fEscribeLog("En fFechaHora excepción: " + e.toString());
            return "";
        }

        return sHora;
    }

    public static String fFechaGTM(String sFormato) {
        String sFecha = "";
        try {

            Date localTime = new Date();
            //SimpleDateFormat converter = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat converter = new SimpleDateFormat(sFormato);
            converter.setTimeZone(TimeZone.getTimeZone("UTC"));
            sFecha = localTime.toString();
            sFecha = converter.format(localTime).toString();
            //yyyy-MM-dd
            //sFecha=sFecha.substring(8,10)+"/"+sFecha.substring(5,7)+"/"+sFecha.substring(0,4);
        } catch (Exception e) {
            fEscribeLog("En fFechaGTM excepción: " + e.toString());
            return "";
        }

        return sFecha;
    }

    public static String fHoraGTM(String sFormato) {
        String sHora = "";
        try {

            Date localTime = new Date();
            //SimpleDateFormat converter = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat converter = new SimpleDateFormat(sFormato);
            converter.setTimeZone(TimeZone.getTimeZone("UTC"));
            sHora = localTime.toString();
            sHora = converter.format(localTime).toString();
        } catch (Exception e) {
            fEscribeLog("En fFechaHoraUTC excepción: " + e.toString());
            return "";
        }

        return sHora;
    }

    public static String[] fObtieneParametros(String sMetodo) {

        String[] asNombreParametro = null;
        try {
            enMetodo Metodo = enMetodo.valueOf(sMetodo);
            switch (Metodo) {
                case ValidaUsuario:
                    asNombreParametro = new String[2];
                    asNombreParametro[0] = "sIdUsuario";
                    asNombreParametro[1] = "sPassword";
                    break;
                case AlmacenaDato:
                    asNombreParametro = new String[1];
                    asNombreParametro[0] = "sJSON";
                    break;
                case ObtieneDatos:
                    asNombreParametro = new String[2];
                    asNombreParametro[0] = "sFechaInicial";
                    asNombreParametro[1] = "sFechaFinal";
                    break;
                default:
                    asNombreParametro = null;
                    break;
            }

        } catch (Exception e) {
            asNombreParametro = null;
        }

        return asNombreParametro;
    }

    public static void fMensajeGlobal(String sTitulo, String sMensaje, Context contexto) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                contexto);
        alertDialogBuilder.setTitle(sTitulo);
        alertDialogBuilder
                .setMessage(sMensaje)
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();

                    }
                })
        ;

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }


    public static String fObtieneDireccionGeoCode(double dLat, double dLon, Context context) {
        String sDireccion = "No disponible";
        try {
            Geocoder coder = new Geocoder(context, Locale.getDefault());
            List<Address> addr = coder.getFromLocation(dLat, dLon, 1);
            sDireccion = "";
            for (int i = 0; i <= addr.get(0).getMaxAddressLineIndex(); i++) {
                if (i == 0)
                    sDireccion = addr.get(0).getAddressLine(0);
                else
                    sDireccion += " " + addr.get(0).getAddressLine(i);

            }
        } catch (Exception e) {
            //Archivos.escribeLog("Al obtener direccion para Lat:"+sLat+" Lon:"+sLon+" Ocurrio la exepcion:"+e);
            sDireccion = "";
        }
        return sDireccion;
    }






    public static void fEscribeLog(String text) {
        String patron = "yyyyMMdd";
        SimpleDateFormat formato = new SimpleDateFormat(patron);
        String sFecha = formato.format(new Date());
        String sCarpetaLog = Carpeta + "log/";
        String sNombreArchivo = "Log" + sFecha + ".txt";

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + sCarpetaLog;
        File logFile = new File(Environment.getExternalStorageDirectory().toString() + sCarpetaLog + sNombreArchivo);


        boolean exists = (new File(path)).exists();
        if (!exists) {
            new File(path).mkdirs();
        }

        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            patron = "HH:mm:ss";
            formato = new SimpleDateFormat(patron);
            String sHora = formato.format(new Date());
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append("[" + sHora + "] " + text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String fCreaArchivoBase64(String sNombreArchivo, String sContenidoBase64) {
        String sRuta="";
        String sCarpetaArchivo = Carpeta + "excel/";


        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + sCarpetaArchivo;
        File ExcelFile = new File(Environment.getExternalStorageDirectory().toString() + sCarpetaArchivo + sNombreArchivo);

        boolean exists = (new File(path)).exists();
        if (!exists) {
            new File(path).mkdirs();
        }


        try {
            byte[] byteContenido = Base64.decodeBase64(sContenidoBase64.getBytes());

            //FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + sCarpetaArchivo + sNombreArchivo);
            FileOutputStream out = new FileOutputStream(path + sNombreArchivo);
            out.write(byteContenido);
            out.close();

            //InputStream inputStream = Base64EncodeDecodeJDK.class.getResourceAsStream(
            return path + sNombreArchivo;
        } catch (IOException ex) {
            fEscribeLog("En fCreaArcivoLog IOExcepción: " + ex.toString());
        } catch (Exception ex) {
            fEscribeLog("En fCreaArcivoLog Excepción: " + ex.toString());
        }
        return "";
    }


    public static String fNombreTerminal() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return fValorCadena(model);
        }
        return fValorCadena(manufacturer) + " " + model;
    }

    private static String fValorCadena(String str) {
        String sValor = "";
        try {
            char[] arr = str.toCharArray();
            boolean capitalizeNext = true;

            //        String phrase = "";
            StringBuilder phrase = new StringBuilder();
            for (char c : arr) {
                if (capitalizeNext && Character.isLetter(c)) {
                    //                phrase += Character.toUpperCase(c);
                    phrase.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                    continue;
                } else if (Character.isWhitespace(c)) {
                    capitalizeNext = true;
                }
                //            phrase += c;
                phrase.append(c);
                sValor = phrase.toString();
            }
        } catch (Exception e) {
            sValor = "";
        }
        return sValor;
    }

    public static String fFormato(double dNumero, String sFormato) {
        String sNumero = "";
        try {
            NumberFormat formatter = new DecimalFormat(sFormato);
            sNumero = formatter.format(dNumero);
        } catch (Exception e) {
            sNumero = Double.toString(dNumero);
        }
        return sNumero;
    }

    /*public static PolylineOptions fOpcionesPoliLinea(cObjetosWS.cPosicion[] aoPosicion) {
        PolylineOptions plRuta = new PolylineOptions();
        try {
            for (int i = 0; i < aoPosicion.length; i++) {
                LatLng latLng = new LatLng(aoPosicion[i].latitud.doubleValue(), aoPosicion[i].longitud.doubleValue());
                plRuta.add(latLng).color(Color.BLUE);
            }

        } catch (Exception e) {
            plRuta = null;
        }
        return plRuta;
    }*/

    public static boolean fEsNumero(String sNumero) {
        boolean bEsNumero = true;
        try {

            Double.parseDouble(sNumero);

        } catch (NumberFormatException e) {
            bEsNumero = false;
        }
        return bEsNumero;
    }

    public static boolean fValidaVigencia(String sFecha_Vigencia,String sFormato) {
        boolean bValida = false;
        try {

            //SimpleDateFormat sFormatoFechaLocal = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            SimpleDateFormat sFormatoFechaLocal = new SimpleDateFormat(sFormato);
            Date dtVigencia = sFormatoFechaLocal.parse(sFecha_Vigencia);
            Date dtHoy = new Date();
            if (dtHoy.before(dtVigencia))
                bValida = true;

        } catch (Exception e) {
            return false;
        }

        return bValida;
    }

    public static long fDiasVigencia(String sFecha_Vigencia) {
        long lDias = 0;
        try {
            if (!sFecha_Vigencia.equals("")) {
                SimpleDateFormat sFormatoFechaLocal = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date dtVigencia = sFormatoFechaLocal.parse(sFecha_Vigencia);
                Date dtHoy = new Date();
                lDias = dtVigencia.getTime() - dtHoy.getTime();
                lDias = lDias / (1000 * 60 * 60 * 24);
            }

        } catch (Exception e) {
            lDias = 0;
            ;
        }

        return lDias;
    }

    public static cObjetos.cAplicacionAndroid fDatosAplicacionAndroid(Context context) {
        cObjetos.cAplicacionAndroid oAplicacionAndroid = new cObjetos.cAplicacionAndroid();
        try {
            android.content.pm.PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            oAplicacionAndroid.sVersion = pInfo.versionName;
            oAplicacionAndroid.iCodigoVersion = pInfo.versionCode;
            cDatosPersistentes oDatosPersistentes = cDatosPersistentes.fObtieneDatos(context);
            if (oDatosPersistentes != null)
                oAplicacionAndroid.sCompilacion = oDatosPersistentes.sVersionCompilacion;
            else {
                oDatosPersistentes = new cDatosPersistentes();
                oAplicacionAndroid.sCompilacion = oDatosPersistentes.sVersionCompilacion;
            }
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            oAplicacionAndroid = null;
            fEscribeLog("En fDatosAplicacionAndroid ocurrio la expecion: " + e.toString());
        }
        return oAplicacionAndroid;
    }

    public  static void AbreGooglePlay(Context context,String sNombrePaquete){
        try {
            String url = "";
            try {
                //Check whether Google Play store is installed or not:
                context.getPackageManager().getPackageInfo("com.android.vending", 0);

                url = "market://details?id=" + sNombrePaquete;
            } catch ( final Exception e ) {
                url = "https://play.google.com/store/apps/details?id=" + sNombrePaquete;
            }


            //Open the app page in Google Play store:
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            context.startActivity(intent);
        } catch (Exception e) {
            fEscribeLog("En AbreGooglePlay ocurrio la expecion: " + e.toString());
        }
    }

    public static String[] HexADecimal(String sCadena) {
        String[] asValor = new String[2];
        try {

            String digits = "0123456789ABCDEF";
            sCadena = sCadena.replace(" ", "");
            sCadena = sCadena.replace(":", sCadena);
            sCadena = sCadena.toUpperCase();
            int val = 0;
            for (int i = 0; i < sCadena.length(); i++) {
                char c = sCadena.charAt(i);
                int d = digits.indexOf(c);
                val = 16 * val + d;
            }
            asValor[0] = sCadena;
            asValor[1] = val + "";

        } catch (Exception e) {
            asValor=null;
            fEscribeLog("En HexADecimal ocurrio la expecion: " + e.toString());
        }

        return asValor;
    }

    public static boolean fContenido(String[] aArreglo, String sValor) {
        return Arrays.asList(aArreglo).contains(sValor);
    }

    public static String fIdCatalogo(String sIdCatalogo, String sCaracterInicio, String sCaracterFin) {
        try {
            if (sCaracterInicio.equals(""))
                sIdCatalogo = sIdCatalogo.substring(0, sIdCatalogo.indexOf(sCaracterFin));
            else
                sIdCatalogo = sIdCatalogo.substring(sCaracterInicio.length(), sIdCatalogo.indexOf(sCaracterFin));
        } catch (Exception e) {
            sIdCatalogo = null;
            fEscribeLog("En fIdCatalogo ocurrio la expecion: " + e.toString());
        }
        return sIdCatalogo;
    }

    public static void fReproduceAlerta(Context context){
        try {
            MediaPlayer mp = MediaPlayer.create(context, R.raw.alerta);
            mp.start();
        } catch (Exception e) {
            fEscribeLog("En Sonido expecion = " + e);
        }
    }

    public static void Sonido(Context context){
        try {
            MediaPlayer mp = MediaPlayer.create(context, R.raw.sonido);
            mp.start();
        } catch (Exception e) {
            fEscribeLog("En Sonido expecion = " + e);
        }
    }

    public static void NotificacionBarraEstado(String sMensaje, Context context){

        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "1")
                    .setSmallIcon(R.drawable.ic_alerta)
                    .setContentTitle("SmartLocation SMS")
                    .setContentText(sMensaje)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(sMensaje))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        } catch (Exception e) {
            fEscribeLog("En NotificacionBarraEstado expecion = " + e);
        }
    }

    @SuppressLint({ "NewApi", "NewApi", "NewApi" })
    public static boolean enviaSMS(String sNumeroMovil, String sMensaje)
    {
        boolean bEnviado=false;
        //SmsManager sms = SmsManager.getDefault();
        SmsManager sms = SmsManager.getDefault();

        if (sMensaje.length()>160)
        {
            sMensaje=sMensaje.substring(0,160);
        }
        try {
            //sms.sendTextMessage(sNumeroMovil, null, sMensaje, null, null);
            sms.sendTextMessage(sNumeroMovil,null,sMensaje,null,null);
            bEnviado=true;
        } catch (Exception e) {

        }
        return bEnviado;
    }

    public static boolean existeRecurso(String sNombre, String sCaprpeta, Context context){
        try {
            int iExiste = context.getResources().getIdentifier(sNombre, sCaprpeta, context.getPackageName());

            if ( iExiste != 0 )
                return true;
            else {
                fEscribeLog("No se encuentra el recurso " + sCaprpeta+"/"+sNombre);
                return false;
            }
        } catch (Exception e) {
            fEscribeLog("En existeRecuros expecion = " + e);
            return false;
        }
    }

    public static cObjetos.cRespuesta validaUsuario(String sUsuario, String sPassword){
        cObjetos.cRespuesta oObjeto= new cObjetos.cRespuesta();
        try {
            if (sUsuario.equals("mrayas") && sPassword.equals("1234567890a")) {
                oObjeto.oCodigoRespuesta = cObjetos.cCodigoRespuesta.foCodigoRespuesta("00");
                oObjeto.oCodigoRespuesta.codigo_respuesta="Acceso permitido";
                cTablas.Datos.cDato  oDatoUsuario = new cTablas.Datos.cDato ();
                oDatoUsuario.nombre="Mario Sergio";
                oDatoUsuario.apellidos="Rayas Chávez";
                oDatoUsuario.correo_electronico="mrayas@ryac.com";
                oDatoUsuario.telefono="5555172210";
                oObjeto.sJSON=cTablas.Datos.cDato .fsJSON(oDatoUsuario);
            }else{
                oObjeto.oCodigoRespuesta = cObjetos.cCodigoRespuesta.foCodigoRespuesta("EA");
            }

        } catch (Exception e) {
            fEscribeLog("En Funciones.validaUsuario expecion = " + e);
            oObjeto.oCodigoRespuesta= cObjetos.cCodigoRespuesta.foCodigoRespuesta("EX");
            oObjeto.oCodigoRespuesta.descripcion=e.toString();
        }

        return oObjeto;
    }

}

