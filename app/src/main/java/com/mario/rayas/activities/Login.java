package com.mario.rayas.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


import android.content.Context;

import java.util.ArrayList;

import android.telephony.TelephonyManager;
import android.widget.Toast;



import android.Manifest;

import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mario.rayas.R;
import com.mario.rayas.util.Funciones;
import com.mario.rayas.util.PermissionUtils;
import com.mario.rayas.util.PermissionResultCallback;
import com.mario.rayas.util.cDatosPersistentes;
import com.mario.rayas.util.cObjetos;
import com.mario.rayas.util.cTablas;

public class Login extends AppCompatActivity implements OnRequestPermissionsResultCallback,PermissionResultCallback  {
    EditText txtUsuario,txtPassword;
    Button btnLogin;
    /*String[] asPERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECEIVE_SMS,Manifest.permission.SEND_SMS,Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE};*/

    String[] asPERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_PHONE_STATE};
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    ArrayList<String> permissions=new ArrayList<>();
    PermissionUtils permissionUtils;

    private ProgressDialog dialogoProcesando = null;

    private String mMETHOD_NAME="";
    private String[] mNombreParametro=null;
    private String[] mValorParametro=null;

    private TelephonyManager mTelephonyManager=null;
    private String mIdTerminal="";
    private String mModeloTerminal="";
    cDatosPersistentes moDatosPersistentes=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Inicia();

            } else {
                permissionUtils=new PermissionUtils(Login.this);
                for (int i=0;i<asPERMISSIONS.length;i++){
                    permissions.add(asPERMISSIONS[i]);
                }
                permissionUtils.check_permission(permissions,"Es IMPORTANTE dar TODOS lo permisos a la apliación para acceder",1);


            }
        } catch (Exception e) {
            Funciones.fEscribeLog("Exepcion:"+e);
            Toast.makeText(Login.this,
                    e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void Inicia(){

        try {
            txtUsuario = (EditText) findViewById(R.id.txtUsuario);
            txtPassword = (EditText) findViewById(R.id.txtPassword);
            btnLogin = (Button) findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String sIdTerminal=fObitieneIdTerminal();
                    if(!txtUsuario.getText().toString().equals("") && !txtPassword.getText().toString().equals("") && sIdTerminal!=null ){
                        wsValidaUsuario(txtUsuario.getText().toString(),txtPassword.getText().toString());
                    }
                }
            });
            moDatosPersistentes=cDatosPersistentes.fObtieneDatos(Login.this);

            /*if(moDatosPersistentes!=null && !moDatosPersistentes.sJSON_Usuario_Cliente.equals("") &&
                    !moDatosPersistentes.sIdTerminal.equals("") && !moDatosPersistentes.sIdModeloTerminal.equals("")){
                mIdTerminal=moDatosPersistentes.sIdTerminal;
                mModeloTerminal=moDatosPersistentes.sIdModeloTerminal;

                cObjetosWS.cUsuarioCliente oUsuarioCliente =cObjetosWS.cUsuarioCliente.foObjet(moDatosPersistentes.sJSON_Usuario_Cliente);
                txtUsuario.setText(oUsuarioCliente.id_usuario_cliente);
                txtPassword.setText(oUsuarioCliente.password);
                wsValidaUsuario(oUsuarioCliente.id_usuario_cliente,oUsuarioCliente.password);
            }*/

        } catch (Exception e) {
            Funciones.fEscribeLog("Exepcion:"+e);
            Toast.makeText(Login.this,
                    e.toString(),
                    Toast.LENGTH_LONG).show();
        }

    }

    public String fObitieneIdTerminal() {

        String device_id ="";
        try {
            boolean bPermiso=false;

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_PHONE_STATE)
                        == PackageManager.PERMISSION_GRANTED) {
                    //Location Permission already granted
                    mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    bPermiso=true;
                }else
                    return null;
            }
            else {
                mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                bPermiso=true;
            }

            if (bPermiso==true){
                mIdTerminal = mTelephonyManager.getDeviceId();
                mModeloTerminal = Build.MODEL;
                if (mIdTerminal == null) {
                    mIdTerminal = "";
                    @SuppressWarnings("deprecation")
                    String androidID = Settings.System.getString(this.getContentResolver(), Settings.System.ANDROID_ID);
                    if (androidID != null)
                        mIdTerminal = androidID;
                    //String androidID = System.getString(this.getContentResolver(),System.ANDROID_ID);
                }
                if (mIdTerminal != null) {
                    btnLogin.setVisibility(View.VISIBLE);
                }
            }else{
                Funciones.fMensajeGlobal("Lectura de Id terminal",
                        "Es indispeonsable el permiso. Concedalo ya que no puede contunuar",
                        Login.this);
                finish();
            }

        } catch (Exception e) {
            device_id=null;
            //Archivos.escribeLog("En fObitieneIMEI Exepcion:"+e);
        }
        return device_id;
    }

    //----Inicia Funciones
    public void wsValidaUsuario(String sIdUsuario,String sPassword) {
        try {
            //limpiaMapa();
            dialogoProcesando = ProgressDialog.show(Login.this, "Obteniendo datos para el usuario con Id"+sIdUsuario, "Espere unos segundos...", true, false);
            //cObjetosWS.cPersonal oPersonal = cDatosPersistentes.foPersonal(Main.this);;

            mMETHOD_NAME="ValidaUsuario";
            mNombreParametro=Funciones.fObtieneParametros(mMETHOD_NAME);
            mValorParametro=new String[mNombreParametro.length];;
            mValorParametro[0] = sIdUsuario;
            mValorParametro[1] = sPassword;
            new Tarea().execute("");

        } catch (Exception e) {
            Funciones.fEscribeLog("En wsUsuarioCliente excepción:" + e.toString());
            dialogoProcesando.dismiss();
            Toast.makeText(Login.this,
                    e.toString(),
                    Toast.LENGTH_LONG).show();

        }
    }


    //----Fin funciones

    //---Inicia Tarea
    private class Tarea extends AsyncTask<String,Void,String> {

        private cObjetos.cRespuesta oRespuesta=new cObjetos.cRespuesta();


        protected String doInBackground(String... params) {
            try {


                oRespuesta= Funciones.validaUsuario(mValorParametro[0],mValorParametro[1]);

            } catch (Exception e) {
                oRespuesta.oCodigoRespuesta= cObjetos.cCodigoRespuesta.foCodigoRespuesta("EX");
                oRespuesta.oCodigoRespuesta.descripcion=e.toString();
                Funciones.fEscribeLog("En MainActivity.Tarea.doInBackground Exepcion:"+e);
            }
            return cObjetos.cRespuesta.fsJSON(oRespuesta);

        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);

        }

        protected void onPostExecute(String result) {
            try{
                dialogoProcesando.dismiss();

                Funciones.enMetodo Metodo =Funciones.enMetodo.valueOf(mMETHOD_NAME);
                if (oRespuesta.oCodigoRespuesta.id_codigo_respuesta.equals("00")){
                    if (moDatosPersistentes==null)
                        moDatosPersistentes=new cDatosPersistentes();
                    switch (Metodo) {
                        case ValidaUsuario:
                            cTablas.Datos.cDato  oDatoUsuario =cTablas.Datos.cDato .foObjet(oRespuesta.sJSON);
                            moDatosPersistentes.sJSON_Usuario=oRespuesta.sJSON;
                            cDatosPersistentes.fAlmacenaDatos(moDatosPersistentes,Login.this);

                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            break;
                    }
                }else{
                    switch (Metodo) {
                        case ValidaUsuario:
                            Funciones.fMensajeGlobal("Validación de usuario",
                                    "["+oRespuesta.oCodigoRespuesta.id_codigo_respuesta+"] "+
                                            oRespuesta.oCodigoRespuesta.descripcion,Login.this);
                            //finish();
                        default:
                            Funciones.fMensajeGlobal("Error",
                                    "["+oRespuesta.oCodigoRespuesta.id_codigo_respuesta+"] "+
                                            oRespuesta.oCodigoRespuesta.descripcion,Login.this);
                            break;
                    }
                }
            } catch (Exception e) {
                dialogoProcesando.dismiss();
                Funciones.fEscribeLog("En MainActivity.Tarea.onPostExecute Exepcion:"+e);
            }
        }
        @Override
        protected void onCancelled() {
            dialogoProcesando.dismiss();
        }
    }
    //---Fin Tarea

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        // redirects to utils

        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }

    //@Override
    public void PermissionGranted(int request_code) {

        Inicia();
    }

    //@Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Toast.makeText(Login.this,
                "Son nesarios TOFOS los permisos. No puedo continuar.",
                Toast.LENGTH_LONG).show();
    }

    //@Override
    public void PermissionDenied(int request_code) {
        Toast.makeText(Login.this,
                "No se dió ningún permiso. No puede continuar",
                Toast.LENGTH_LONG).show();
    }

    //@Override
    public void NeverAskAgain(int request_code) {
        Toast.makeText(Login.this,
                "No se dió ningún permiso. No puede continuar. Vaya a apliaciones y de deforma manual todos los permisos",
                Toast.LENGTH_LONG).show();
    }


}
