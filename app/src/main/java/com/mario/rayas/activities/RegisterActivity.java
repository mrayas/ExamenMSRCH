package com.mario.rayas.activities;





import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mario.rayas.R;
import com.mario.rayas.util.Funciones;
import com.mario.rayas.util.cDatosPersistentes;
import com.mario.rayas.util.cObjetos;
import com.mario.rayas.util.cTablas;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Looper;


public class RegisterActivity extends AppCompatActivity {
    EditText txtNombres,txtApellidos,txtTelefono,txtCorreo;
    Button btnRegistra,btnlimpia,btnCamara;
    ImageView imvFoto;
    TextView tvLatLon;
    private ProgressDialog dialogoProcesando = null;
    private String mMETHOD_NAME="";
    private String[] mNombreParametro=null;
    private String[] mValorParametro=null;

    private static final int SALIR=100;
    private static final int ALMACENAR=200;

    public static final String FILE_NAME = "temp.jpg";
    public static final int CAMERA_IMAGE_REQUEST = 3;
    static final int REQUEST_IMAGE_CAPTURE = 4;

    cTablas.Datos.cDato goDato;
    String sJSONRespuesta="";

    Location mLocation=null;


    LocationRequest mLocationRequest;
    FusedLocationProviderClient mFusedLocationClient;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);



            txtNombres = findViewById(R.id.txtNombres);
            txtApellidos = findViewById(R.id.txtApellidos);
            txtTelefono = findViewById(R.id.txtTelefono);
            txtCorreo = findViewById(R.id.txtCorreo);
            tvLatLon = findViewById(R.id.tvLatLon);
            imvFoto = findViewById(R.id.imvFoto);
            btnCamara = (Button) findViewById(R.id.btnCamara);
            btnCamara.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    btnCamara_click(v);
                }
            });

            btnRegistra = (Button) findViewById(R.id.btnRegistra);
            btnRegistra.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    btnRegistra_click(v);
                }
            });

            btnlimpia = (Button) findViewById(R.id.btnlimpia);
            btnlimpia.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    btnlimpia_click(v);
                }
            });

            /*txtApellidos.setText("Apellidos");
            txtCorreo.setText("mario@ryac.com");
            txtNombres.setText("Nombres");
            txtTelefono.setText("5512956642");*/

            iniciaGPS();

        } catch (Exception e) {
            Funciones.fEscribeLog("En RegisterActivity.onCreate exepcion:"+e);
            Toast.makeText(RegisterActivity.this,
                    "En RegisterActivity.onCreate exepcion:"+e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public boolean iniciaGPS(){
        boolean bPermiso=false;
        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    bPermiso=true;
                } else {
                    //Request Location Permission
                    bPermiso=false;
                }
            }
            else {
                bPermiso=true;
            }
            if (bPermiso==true){

                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    mLocation=location;
                                    tvLatLon.setText(mLocation.getLatitude()+","+mLocation.getLongitude());
                                }
                            }
                        });
            }
        } catch (Exception e) {
            bPermiso=false;
            Funciones.fEscribeLog("En onReceive Exepcion:"+e);

        }
        return bPermiso;
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == CAMERA_IMAGE_REQUEST || requestCode == REQUEST_IMAGE_CAPTURE) {
                    uploadImage(Uri.fromFile(getCameraFile()));
                }
            }

            return;
        } catch (Exception e) {
            Funciones.fEscribeLog("En Main.onActivityResult ocurrió la exepción:"+e.toString());
            Toast.makeText(getApplicationContext(), "En Main.onActivityResult ocurrió la exepción:"+e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void finish() {
        // Reset the animation to avoid flickering.
        Intent returnIntent = new Intent();
        try {

            if (sJSONRespuesta.equals("")) {

                cObjetos.cRespuesta oRespuesta = new cObjetos.cRespuesta();
                oRespuesta.oCodigoRespuesta=cObjetos.cCodigoRespuesta.foCodigoRespuesta("01");
                oRespuesta.oCodigoRespuesta.descripcion="Ningun dato agregado";
                sJSONRespuesta=cObjetos.cRespuesta.fsJSON(oRespuesta);

            }
            returnIntent.putExtra("sJSONRespuesta", sJSONRespuesta);
            setResult(RegisterActivity.RESULT_OK, returnIntent);

        } catch (Exception e) {
            //showResponse("En setupAutoCompleteFragment: "+ e.toString());
            setResult(RegisterActivity.RESULT_CANCELED, returnIntent);
            Funciones.fEscribeLog("En ListInfoActivity.finish: "+ e.toString());
        }
        super.finish();
    }

    //---Inicia Clicks
    public void uploadImage(Uri uri) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);

                //callCloudVision(bitmap);
                imvFoto.setImageBitmap(bitmap);

            } catch (IOException e) {
                Funciones.fEscribeLog("En uploadImage excepción: " + e.getMessage());
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        } else {
            Funciones.fEscribeLog("En uploadImage excepción: ");
            Toast.makeText(this, "No se obtuvo la imagen", Toast.LENGTH_LONG).show();
        }
    }
    public File getCameraFile() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }
    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }
    private void btnCamara_click(View v) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
                startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
            }else{
                //Para Android 9
                if (intent.resolveActivity(getPackageManager()) != null) {
                    String sNombre=getCameraFile().getName();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getCameraFile()));
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            }

        } catch (Exception e) {
            Funciones.fEscribeLog("En RegisterActivity.btnCamara_click exepcion:"+e);
            Toast.makeText(RegisterActivity.this,
                    "En RegisterActivity.btnCamara_click exepcion:"+e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }
    private void btnRegistra_click(View v) {
        cObjetos.cRespuesta oRespuesta=new cObjetos.cRespuesta();
        cTablas.Datos.cDato oObjeto=new cTablas.Datos.cDato();
        try {

            oObjeto=obtieneDato();

            if(oObjeto==null)
                return;


            oRespuesta=registraDato(oObjeto);

            oObjeto=cTablas.Datos.cDato.foObjet(oRespuesta.sJSON);

        } catch (Exception e) {
            Funciones.fEscribeLog("En RegisterActivity.btnEnvia_click exepcion:"+e);
            oRespuesta.oCodigoRespuesta=cObjetos.cCodigoRespuesta.foCodigoRespuesta("EX");
            oRespuesta.oCodigoRespuesta.descripcion=e.toString();
        }
        sJSONRespuesta=cObjetos.cRespuesta.fsJSON(oRespuesta);
        if (oRespuesta.oCodigoRespuesta.id_codigo_respuesta.equals("00")) {
            limpiaCampos();
            finish();
        }else {
            Funciones.fMensajeGlobal("Registra Dato",
                    "["+oRespuesta.oCodigoRespuesta.id_codigo_respuesta+"] "+
                            oRespuesta.oCodigoRespuesta.descripcion+".\n ID:"+oObjeto.id
                    ,RegisterActivity.this);

        }

    }

    private void btnlimpia_click(View v) {
        try {

            limpiaCampos();
        } catch (Exception e) {
            Funciones.fEscribeLog("En RegisterActivity.btnlimpia_click exepcion:"+e);
            Toast.makeText(RegisterActivity.this,
                    "En RegisterActivity.btnlimpia_click exepcion:"+e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }
    //---Termina Clics

    //--Funciones

    private void solicitaConfirmacion(String sTitulo, final String sMensaje, final int iFuncion, final String sJSON) {
        try {


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set title
            alertDialogBuilder.setTitle(sTitulo);

            // set dialog message
            alertDialogBuilder
                    .setMessage(sMensaje)
                    .setCancelable(false)
                    .setPositiveButton("Si",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {

                            switch (iFuncion) {
                                case ALMACENAR:
                                    try {
                                        cTablas.Datos.cDato  oDato = cTablas.Datos.cDato .foObjet(sJSON);
                                    } catch (Exception e) {
                                        Funciones.fEscribeLog("En RegisterActivity.solicitaConfirmacion.ENVIA_SMS:"+e);
                                        Toast.makeText(RegisterActivity.this,
                                                "En RegisterActivity.solicitaConfirmacion.ENVIA_SMS exepcion:"+e.toString(),
                                                Toast.LENGTH_LONG).show();
                                    }
                                    break;

                            }

                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            switch (iFuncion) {

                                default:
                                    dialog.cancel();
                                    break;

                            }

                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();


        } catch (Exception e) {
            Funciones.fEscribeLog("En MainActivity.solicitaConfirmacion excepción:"+e.toString());
        }
    }


    public void limpiaCampos(){
        txtApellidos.setText("");
        txtCorreo.setText("");
        txtNombres.setText("");
        txtTelefono.setText("");
    }

    public cObjetos.cRespuesta registraDato(cTablas.Datos.cDato oObjeto){
        cObjetos.cRespuesta oRespuesta = new cObjetos.cRespuesta();
        try {
            cTablas.Datos tDatos = new cTablas.Datos(RegisterActivity.this);
            if(tDatos.abrir()) {
                oObjeto = tDatos.insertar(oObjeto);
                if (oObjeto.id>0)
                    oRespuesta.oCodigoRespuesta=cObjetos.cCodigoRespuesta.foCodigoRespuesta("00");
                else
                    oRespuesta.oCodigoRespuesta=cObjetos.cCodigoRespuesta.foCodigoRespuesta("01");
                oRespuesta.sJSON=cTablas.Datos.cDato.fsJSON(oObjeto);
            }else{
                oRespuesta.oCodigoRespuesta=cObjetos.cCodigoRespuesta.foCodigoRespuesta("01");
            }


        } catch (Exception e) {
            Funciones.fEscribeLog("En RegisterActivity.regstraDato exepcion:"+e);
            Toast.makeText(RegisterActivity.this,
                    "En RegisterActivity.regstraDato exepcion:"+e.toString(),
                    Toast.LENGTH_LONG).show();
            oRespuesta.oCodigoRespuesta=cObjetos.cCodigoRespuesta.foCodigoRespuesta("EX");
            oRespuesta.oCodigoRespuesta.descripcion=e.toString();
        }
        return oRespuesta;
    }

    private cTablas.Datos.cDato obtieneDato (){
        cTablas.Datos.cDato oObjeto=new cTablas.Datos.cDato();
        try {

            oObjeto.oCoordenada =new cTablas.Datos.cCoordenada();
            oObjeto.nombre= txtNombres.getText().toString();
            oObjeto.apellidos= txtApellidos.getText().toString();
            oObjeto.telefono= txtTelefono.getText().toString();
            oObjeto.correo_electronico= txtCorreo.getText().toString();
            oObjeto.fotografia= "Fotografia";
            oObjeto.oCoordenada.latitud="19.427149603620745";
            oObjeto.oCoordenada.longitud="-99.16754570600145";
            oObjeto.oCoordenada.altitud="0.0";



            Pattern p = Pattern.compile("^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]{1,}[A-Za-z\\s]{0,}$");
            Matcher m = p.matcher(oObjeto.nombre);


            if(oObjeto.nombre.trim().equals(""))
            {
                Toast.makeText(RegisterActivity.this,
                        "El nombre es requerdo",
                        Toast.LENGTH_SHORT).show();
                return null;
            }else if (!m.matches()) {
                Toast.makeText(RegisterActivity.this,
                        "El nombre no es válido",
                        Toast.LENGTH_SHORT).show();
                return null;
            }

            m = p.matcher(oObjeto.apellidos);

            if(oObjeto.apellidos.trim().equals(""))
            {
                Toast.makeText(RegisterActivity.this,
                        "Los apellidos son requeridos",
                        Toast.LENGTH_SHORT).show();
                return null;
            }else if (!m.matches()) {
                Toast.makeText(RegisterActivity.this,
                        "Los apellidos no son válidos",
                        Toast.LENGTH_SHORT).show();
                return null;
            }


            p = Pattern.compile("^(?:\\+|-)?\\d+$");
            m = p.matcher(oObjeto.telefono);
            if(oObjeto.telefono.trim().equals(""))
            {
                Toast.makeText(RegisterActivity.this,
                        "El teléfono es requerido",
                        Toast.LENGTH_SHORT).show();
                return null;
            }else if (!m.find()) {
                Toast.makeText(RegisterActivity.this,
                        "El teléfono debe contener solo números",
                        Toast.LENGTH_SHORT).show();
                return null;
            }else if(oObjeto.telefono.trim().length()!=10)
            {
                Toast.makeText(RegisterActivity.this,
                        "El teléfono debe tener una longitud de 10 dígitos",
                        Toast.LENGTH_SHORT).show();
                return null;
            }



            p = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
            p = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
            m = p.matcher(oObjeto.correo_electronico);

            if(oObjeto.correo_electronico.trim().equals(""))
            {
                Toast.makeText(RegisterActivity.this,
                        "El correo es requerido",
                        Toast.LENGTH_SHORT).show();
                return null;
            }else if (!m.matches()) {
                Toast.makeText(RegisterActivity.this,
                        "El correo no es válido",
                        Toast.LENGTH_SHORT).show();
                return null;
            }


            if (null == imvFoto.getDrawable()){
                Toast.makeText(RegisterActivity.this,
                        "Debe tomar una fotografía con la cámara del teléfono ó baje la calidad de la imagen",
                        Toast.LENGTH_SHORT).show();
                return null;
            }

            Bitmap bitmap = ((BitmapDrawable) imvFoto.getDrawable()).getBitmap();

            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytImagen=stream.toByteArray();
            oObjeto.fotografia= Base64.encodeToString(bytImagen, 0);

            if(oObjeto.fotografia.trim().equals(""))
            {
                Toast.makeText(RegisterActivity.this,
                        "No se convirtio la imagen a Base64 correctamente",
                        Toast.LENGTH_SHORT).show();
                return null;
            }

            if (mLocation==null){
                Toast.makeText(RegisterActivity.this,
                        "No se ha obtenido datos del GPS. Verifique que su GPS se encuentre encendido y tenga señal",
                        Toast.LENGTH_SHORT).show();
                return null;
            }

            oObjeto.oCoordenada.latitud=mLocation.getLatitude()+"";
            oObjeto.oCoordenada.longitud=mLocation.getLongitude()+"";
            oObjeto.oCoordenada.altitud=mLocation.getAltitude()+"";





        } catch (Exception e) {
            Funciones.fEscribeLog("En RegisterActivity.btnEnvia_click exepcion:"+e);
            oObjeto=null;
        }

        return oObjeto;

    }

    //--Termina funciones

    //--Web Services
    public void wsAlmacenaDato(cTablas.Datos.cDato  oObjeto) {
        try {
            //limpiaMapa();
            dialogoProcesando = ProgressDialog.show(RegisterActivity.this, "Registrando la inforación", "Espere unos segundos...", true, false);

            //String sTerminal=cDatosPersistentes.fsTerminal(MainActivity.this);

            mMETHOD_NAME="AlmacenaDato";
            mNombreParametro=Funciones.fObtieneParametros(mMETHOD_NAME);
            mValorParametro=new String[mNombreParametro.length];
            mValorParametro[0] = cTablas.Datos.cDato .fsJSON(oObjeto);

            new RegisterActivity.Tarea().execute("");
        } catch (Exception e) {
            Funciones.fEscribeLog("En wsListaUltimoRegistroEquipoCliente excepción:" + e.toString());
            dialogoProcesando.dismiss();
            Toast.makeText(RegisterActivity.this,
                    e.toString(),
                    Toast.LENGTH_LONG).show();

        }
    }
    //Termina Web services
    //---Task


    private class Tarea extends AsyncTask<String,Void,String> {

        private cObjetos.cRespuesta oRespuesta=new cObjetos.cRespuesta();


        protected String doInBackground(String... params) {
            try {



            } catch (Exception e) {
                oRespuesta.oCodigoRespuesta= cObjetos.cCodigoRespuesta.foCodigoRespuesta("EX");
                oRespuesta.oCodigoRespuesta.descripcion=e.toString();
                Funciones.fEscribeLog("En SendCommandActivity.Tarea.doInBackground Exepcion:"+e);
            }
            return cObjetos.cRespuesta.fsJSON(oRespuesta);

        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);

        }

        protected void onPostExecute(String result) {
            try{

                if (dialogoProcesando!=null)
                    dialogoProcesando.dismiss();

                Funciones.enMetodo Metodo =Funciones.enMetodo.valueOf(mMETHOD_NAME);
                if (oRespuesta.oCodigoRespuesta.id_codigo_respuesta.equals("00")){
                    switch (Metodo) {
                        case AlmacenaDato:
                            //wsListaUltimoRegistroEquipoCliente();
                            Funciones.fMensajeGlobal("Alamacena registro",
                                    "["+oRespuesta.oCodigoRespuesta.id_codigo_respuesta+"] "+
                                            oRespuesta.oCodigoRespuesta.descripcion,RegisterActivity.this);
                            break;
                    }
                }else{

                    switch (Metodo) {
                        case AlmacenaDato:
                            Funciones.fMensajeGlobal("Almacena registro",
                                    "["+oRespuesta.oCodigoRespuesta.id_codigo_respuesta+"] "+
                                            oRespuesta.oCodigoRespuesta.descripcion,RegisterActivity.this);
                            break;
                        default:
                            Funciones.fMensajeGlobal("Error",
                                    "["+oRespuesta.oCodigoRespuesta.id_codigo_respuesta+"] "+
                                            oRespuesta.oCodigoRespuesta.descripcion,RegisterActivity.this);
                            break;
                    }
                }
            } catch (Exception e) {
                if (dialogoProcesando!=null)
                    dialogoProcesando.dismiss();
                Funciones.fEscribeLog("En MainActivity.Tarea.onPostExecute Exepcion:"+e);
            }
        }
        @Override
        protected void onCancelled() {
            if (dialogoProcesando!=null)
                dialogoProcesando.dismiss();
        }
    }


    //---Fin task
}

