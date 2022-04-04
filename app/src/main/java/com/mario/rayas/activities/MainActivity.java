package com.mario.rayas.activities;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
//import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import android.os.AsyncTask;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mario.rayas.R;
import com.mario.rayas.util.*;

import com.mario.rayas.util.Funciones;
import com.mario.rayas.util.cDatosPersistentes;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    private ProgressDialog dialogoProcesando = null;
    ListView lvRegistros;

    com.getbase.floatingactionbutton.FloatingActionButton fabAgrega,fabMapa;
    com.getbase.floatingactionbutton.FloatingActionsMenu fabMenuMain;
    int INDEX=-1;
    //String IMEI="";

    cDatosPersistentes moDatosPersistentes=null;
    String mNombreCliente="";
    public static final String MIME_TEXT_PLAIN = "text/plain";

    private String mMETHOD_NAME="";
    private String[] mNombreParametro=null;
    private String[] mValorParametro=null;
    private cObjetos.cAplicacionAndroid oAplicacionAndroid=null;

    cTablas.Datos.cDato[] maoObjeto=null;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String mFecha = "";
    private int iTipoReporte=0;
    private static final int LOGOUT=100;
    public static final int AGREGA_DATOS=110;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE=3;
    private static final int REQUEST_CODE_READ_PHONE_STATE = 2;

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear + 1;
                    mDay = dayOfMonth;
                    mFecha = ((mDay < 10) ? "0" + mDay : mDay) + "/" + ((mMonth < 10) ? "0" + mMonth : mMonth) + "/" + mYear;
                    if (iTipoReporte == 1){

                        Snackbar.make(view, "Obteniendo registros para el equipo:"+
                                mFecha, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        //wsRegistrosEquipoIMEIClienteFechas(moRegistroEquipo,mFecha,"");
                    }else
                        Funciones.fMensajeGlobal("Reporte",
                                "Datos por día:"+mFecha,MainActivity.this);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);
            toolbar = findViewById(R.id.toolbar);
            getSupportActionBar().hide();
            //setSupportActionBar(toolbar);//355628082746214

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            navigationView.setNavigationItemSelectedListener(this);

            fabMenuMain = findViewById(R.id.fabMenuMain);
            fabMenuMain.expand();
            fabAgrega = findViewById(R.id.fabAgrega);
            //fabEquipos.setVisibility(View.GONE);
            fabAgrega.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {



                    String sMensaje = "Agregar registro ";

                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    //startActivity(intent);
                    startActivityForResult(intent,AGREGA_DATOS);

                    Snackbar.make(view, sMensaje
                            , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                }
            });

            fabMapa = findViewById(R.id.fabMapa);
            fabMapa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    if (maoObjeto!=null){
                        cTablas.Datos.cDato[] aoDato = maoObjeto;
                        for (int i=0;i<aoDato.length; i++){
                            aoDato[i].fotografia="";
                        }
                        String sMensaje = "Mostrar datos en mapa";
                        Snackbar.make(view, sMensaje
                                , Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        Intent intent = new Intent(MainActivity.this, Mapa.class);
                        String sJSONDatos= cTablas.Datos.cDato.fsJSON(aoDato);
                        intent.putExtra("sJSONDatos", sJSONDatos);
                        //startActivity(intent);
                        startActivityForResult(intent,AGREGA_DATOS);
                    }




                }
            });


            lvRegistros = (ListView) findViewById(R.id.lvRegistros);
            lvRegistros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View v, int posicion, long id) {
                    Object i = lvRegistros.getItemAtPosition(posicion);
                    INDEX=posicion;
                    if (i!=null) {
                        try {
                            /*if (maoUltimoRegistroEquipo!=null) {

                                String sMensaje = i.toString();
                                Toast.makeText(getApplicationContext(),
                                        "Equipo seleccionado:" +
                                                maoUltimoRegistroEquipo[INDEX].oEquipoAsignado.oEquipo.imei_equipo + " (" +
                                                maoUltimoRegistroEquipo[INDEX].oEquipoAsignado.alias_equipo + ")", Toast.LENGTH_LONG).show();


                            }else {
                                //IMEI="";
                                moRegistroEquipo=null;
                                Toast.makeText(getApplicationContext(),
                                        "Menú disponible!!", Toast.LENGTH_LONG).show();
                            }*/

                        } catch (Exception e) {
                            Funciones.fEscribeLog("Al seleccionar el CFDI ocurrio la Exepcion:"+e);

                        }

                    }
                }

            });
            registerForContextMenu(this.lvRegistros);


            oAplicacionAndroid=Funciones.fDatosAplicacionAndroid(MainActivity.this);
            fechaHoy(0);


            moDatosPersistentes = cDatosPersistentes.fObtieneDatos(MainActivity.this);

            cTablas.Datos.cDato oUsuario = cTablas.Datos.cDato.foObjet(moDatosPersistentes.sJSON_Usuario);
            wsObtieneDatos("","");
            toolbar.setTitle(oUsuario.nombre);


        } catch (Exception ex) {
            Funciones.fEscribeLog("En MainActivity.onCreate Exepcion:"+ex);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == Activity.RESULT_OK){
                if (requestCode==AGREGA_DATOS){
                    String sJSONRespuesta =data.getStringExtra("sJSONRespuesta");
                    cObjetos.cRespuesta oRespuesta = cObjetos.cRespuesta.foObjet(sJSONRespuesta);
                    if (oRespuesta.oCodigoRespuesta.id_codigo_respuesta.equals("00"))
                        wsObtieneDatos("","");
                }
            }

        } catch (Exception ex) {
            Funciones.fEscribeLog("En onContextItemSelected Exepcion:"+ex);

        }
    }

    private void check_READ_PHONE_STATE() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Permiso solicitado")
                        .setMessage("La aplicación requiere permiso de lectura de Id de la terminal")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.READ_PHONE_STATE},
                                        REQUEST_CODE_READ_PHONE_STATE );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                //mbAccesoIdTerminal=true;
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUEST_CODE_READ_PHONE_STATE );
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE_READ_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_PHONE_STATE)
                            == PackageManager.PERMISSION_GRANTED) {
                        //mbAccesoIdTerminal=true;
                        Intent intent = new Intent(MainActivity.this, Login.class);
                        startActivity(intent);

                    }

                } else {
                    //mbAccesoIdTerminal=false;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    //Toast.makeText(this, "Permiso denegado para lectuta Id terminal", Toast.LENGTH_LONG).show();
                    Funciones.fMensajeGlobal("Acceso a Id Terminal",
                            "Es necesario conceda permiso para acceder al Id de la terminal, para continuar.",
                            MainActivity.this);
                    finish();
                }
                return;
            }
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permiso denegado para escritura de log", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        cDatosPersistentes oDatosPersistentes =cDatosPersistentes.fObtieneDatos(MainActivity.this);
        int id = item.getItemId();

        if (id == R.id.nav_salir) {
            if (oDatosPersistentes != null) {
                solicitaConfirmacion("Logout", "¿Desea hacer logout y cerrar la aplicación?", LOGOUT);
            } else
                finish();
        } else if(id==R.id.nav_agrega){
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_acerca) {
            if (oDatosPersistentes!=null) {
                cTablas.Datos.cDato  oDatoUsuario = cDatosPersistentes.foUsuario(MainActivity.this);
                cObjetos.cVersion oVersion = cDatosPersistentes.foVersion(MainActivity.this);

                String sMensaje = "Usuario:" +
                        oDatoUsuario.nombre + " " + oDatoUsuario.apellidos +
                        " (" + oDatoUsuario.correo_electronico + ")";
                sMensaje +="\nIdTerminal:" + oDatosPersistentes.sIdTerminal + " (" +
                        oDatosPersistentes.sIdModeloTerminal + ")";

                Funciones.fMensajeGlobal("Acerca", sMensaje, MainActivity.this);
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add("Ver en mapa lugar de registros");
        menu.add("Ver datos");



    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String sMenu=item.toString();
        if (INDEX>=0) {

            if (sMenu.equals("Ver datos")){

                try {
                    if (maoObjeto!=null){
                        muestraDato(maoObjeto[INDEX]);
                    }


                } catch (Exception ex) {
                    Funciones.fEscribeLog("En onContextItemSelected Exepcion:"+ex);

                }

            }else if (sMenu.equals("Ver en mapa lugar de registros")){

                try {

                    if (maoObjeto!=null){
                        cTablas.Datos.cDato oDato =maoObjeto[INDEX];
                        oDato.fotografia="";
                        Intent intent = new Intent(MainActivity.this, Mapa.class);

                        intent.putExtra("sJSONDato",cTablas.Datos.cDato.fsJSON(oDato));
                        startActivity(intent);
                    }




                } catch (Exception ex) {
                    Funciones.fEscribeLog("En onContextItemSelected Exepcion:"+ex);

                }

            }else if (sMenu.equals("Ver datos equipo")){

                try {
                    /*if (maoUltimoRegistroEquipo!=null) {
                        moRegistroEquipo=new cObjetosWS.cRegistroEquipo();
                        moRegistroEquipo.oEquipoAsignado=new cObjetosWS.cEquipoAsignado();
                        moRegistroEquipo.oEquipoAsignado = maoUltimoRegistroEquipo[INDEX].oEquipoAsignado;
                        if (!moRegistroEquipo.oEquipoAsignado.json_informacion.equals("")) {
                            cObjetosWS.cInformacionEquipoAsignado oInformacionEquipoAsignado=cObjetosWS.cInformacionEquipoAsignado.foObjet(moRegistroEquipo.oEquipoAsignado.json_informacion);
                            String sInformacion="Uso:"+moRegistroEquipo.oEquipoAsignado.oTipoUso.tipo_uso+
                                    "\nSIM:"+moRegistroEquipo.oEquipoAsignado.oEquipo.numero_sim+
                                    "\nMarca:"+oInformacionEquipoAsignado.oMarca.Marca+
                                    "\nModelo:"+oInformacionEquipoAsignado.oMarca.Modelo;
                            if (moRegistroEquipo.oEquipoAsignado.oTipoUso.id_tipo_uso.equals("01")){
                                sInformacion+="\nSubdarca:"+oInformacionEquipoAsignado.oAutoMotriz.Submarca+
                                        "\nMatricula:"+oInformacionEquipoAsignado.oAutoMotriz.Matricula;
                            }else if (moRegistroEquipo.oEquipoAsignado.oTipoUso.id_tipo_uso.equals("02")){
                                sInformacion+="\nTerminal:"+oInformacionEquipoAsignado.oTerminal.IdTerminal;
                            }
                            sInformacion+="\nUsuario:"+oInformacionEquipoAsignado.oPersona.Nombre+
                                    "\nMovil:"+oInformacionEquipoAsignado.oPersona.Movil+
                                    "\nCorreo:"+oInformacionEquipoAsignado.oPersona.Correo;

                            Funciones.fMensajeGlobal(moRegistroEquipo.oEquipoAsignado.oEquipo.oModeloEquipo.modelo_equipo+
                                    "("+moRegistroEquipo.oEquipoAsignado.oEquipo.oModeloEquipo.oTipoEquipo.tipo_equipo+")",
                                    sInformacion,
                                    MainActivity.this);
                        }
                    }*/


                } catch (Exception ex) {
                    Funciones.fEscribeLog("En onContextItemSelected Exepcion:"+ex);

                }

            }else if (sMenu.equals("Elimina registro")){

                try {
                    /*if (maoUltimoRegistroEquipo!=null) {
                        moRegistroEquipo=new cObjetosWS.cRegistroEquipo();
                        moRegistroEquipo.oEquipoAsignado=new cObjetosWS.cEquipoAsignado();
                        moRegistroEquipo.oEquipoAsignado = maoUltimoRegistroEquipo[INDEX].oEquipoAsignado;
                        solicitaConfirmacion("Borrar ultimo registro",
                                "¿Desea eliminar el ultimo registro del equipo "+moRegistroEquipo.oEquipoAsignado.alias_equipo,
                                BORRA_ULTIMO_REGISTRO);
                    }*/


                } catch (Exception ex) {
                    Funciones.fEscribeLog("En onContextItemSelected Exepcion:"+ex);

                }

            }


        }
        return true;
    }

    //---Funciones
    private void solicitaConfirmacion(String sTitulo,String sMensaje,final int iFuncion) {
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
                            cDatosPersistentes oDatosPersistentes = cDatosPersistentes.fObtieneDatos(MainActivity.this);
                            switch (iFuncion) {
                                case LOGOUT:
                                    finish();
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
    public void verDatePicker() {
        DatePickerDialog d = new DatePickerDialog(this, mDateSetListener, mYear, mMonth - 1, mDay);
        d.show();
    }

    public boolean fechaHoy(int iYear) {
        try {
            java.util.Calendar cFecha = java.util.Calendar.getInstance();
            if (iYear < 0)
                cFecha.add(java.util.Calendar.YEAR, iYear);

            mYear = cFecha.get(java.util.Calendar.YEAR);
            mDay = cFecha.get(java.util.Calendar.DAY_OF_MONTH);
            mMonth = cFecha.get(java.util.Calendar.MONTH);
            mMonth++;
            mFecha = ((mDay < 10) ? "0" + mDay : mDay) + "/" + ((mMonth < 10) ? "0" + mMonth : mMonth) + "/" + mYear;
            return true;
        } catch (Exception e) {
            Funciones.fEscribeLog("En onCreate fechaHoy excepción: " + e.toString());
            return false;
        }
    }

    public boolean asignaFecha(String sFecha) {
        try {

            mYear = Integer.parseInt(sFecha.substring(6,10));
            mDay = Integer.parseInt(sFecha.substring(0,2));
            mMonth = Integer.parseInt(sFecha.substring(3,5));
            mFecha = ((mDay < 10) ? "0" + mDay : mDay) + "/" + ((mMonth < 10) ? "0" + mMonth : mMonth) + "/" + mYear;
            return true;
        } catch (Exception e) {
            Funciones.fEscribeLog("En onCreate fechaHoy excepción: " + e.toString());
            return false;
        }
    }

    public cObjetos.cRespuesta ObtieneDatos(String FechaInicial,String FechaFinal){
        cObjetos.cRespuesta oRespuesta = new cObjetos.cRespuesta();
        try {
            cTablas.Datos tDatos = new cTablas.Datos(MainActivity.this);
            if(tDatos.abrir()) {
                cTablas.Datos.cDato[]  aoObjeto = tDatos.DatosFechas(FechaInicial,FechaFinal);
                if (aoObjeto!=null) {
                    oRespuesta.oCodigoRespuesta = cObjetos.cCodigoRespuesta.foCodigoRespuesta("00");
                    oRespuesta.sJSON=cTablas.Datos.cDato.fsJSON(aoObjeto);
                }
                else
                    oRespuesta.oCodigoRespuesta=cObjetos.cCodigoRespuesta.foCodigoRespuesta("01");

            }else{
                oRespuesta.oCodigoRespuesta=cObjetos.cCodigoRespuesta.foCodigoRespuesta("01");
            }


        } catch (Exception e) {
            Funciones.fEscribeLog("En RegisterActivity.regstraDato exepcion:"+e);
            Toast.makeText(MainActivity.this,
                    "En RegisterActivity.regstraDato exepcion:"+e.toString(),
                    Toast.LENGTH_LONG).show();
            oRespuesta.oCodigoRespuesta=cObjetos.cCodigoRespuesta.foCodigoRespuesta("EX");
            oRespuesta.oCodigoRespuesta.descripcion=e.toString();
        }
        return oRespuesta;
    }

    public void wsObtieneDatos(String sFechaInicial,String sFechaFinal) {
        try {
            //limpiaMapa();
            dialogoProcesando = ProgressDialog.show(MainActivity.this, "Obteniendo datos", "Espere unos segundos...", true, false);

            //String sTerminal=cDatosPersistentes.fsTerminal(MainActivity.this);

            mMETHOD_NAME="ObtieneDatos";
            mNombreParametro=Funciones.fObtieneParametros(mMETHOD_NAME);
            mValorParametro=new String[mNombreParametro.length];
            mValorParametro[0] = sFechaInicial;
            mValorParametro[1] = sFechaFinal;

            new Tarea().execute("");
        } catch (Exception e) {
            Funciones.fEscribeLog("En wsListaUltimoRegistroEquipoCliente excepción:" + e.toString());
            dialogoProcesando.dismiss();
            Toast.makeText(MainActivity.this,
                    e.toString(),
                    Toast.LENGTH_LONG).show();

        }
    }
    private void check_WRITE_EXTERNAL_STORAGE() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                new AlertDialog.Builder(this)
                        .setTitle("Permiso solicitado")
                        .setMessage("La aplicación requiere permiso de escritura")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        REQUEST_CODE_WRITE_EXTERNAL_STORAGE );
                            }
                        })
                        .create()
                        .show();

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_WRITE_EXTERNAL_STORAGE );
            }
        }
    }





    private void poblaDatos(cTablas.Datos.cDato[] aoObjeto){
        try {
            Toast.makeText(MainActivity.this,
                    "Seleccione el registro y mantengalo pulsado para sacar el menú",
                    Toast.LENGTH_LONG).show();
            maoObjeto=aoObjeto;
            lvRegistros = (ListView) findViewById(R.id.lvRegistros);
            ArrayAdapter<String> adRegistros;
            ArrayList alRegistros;


            alRegistros = new ArrayList();
            adRegistros = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, alRegistros);
            lvRegistros.setAdapter(adRegistros);

            String sDatosRegistro="";

            for(int i=0;i<aoObjeto.length;i++){
                sDatosRegistro="Nombre(s): "+aoObjeto[i].nombre+"\n"+
                        "Apellidos: "+aoObjeto[i].apellidos+"\n"+
                        "Teléfono: "+aoObjeto[i].telefono+"\n"+
                        "Correo: "+aoObjeto[i].correo_electronico+"\n"+
                        "Latitud: "+aoObjeto[i].oCoordenada.latitud+"\n"+
                        "Longitud: "+aoObjeto[i].oCoordenada.longitud+"\n"+
                        "Creado: "+aoObjeto[i].creado;
                alRegistros.add(sDatosRegistro);
            }

        } catch (Exception e) {
            Funciones.fEscribeLog("poblaDatos Exepcion:"+e);
            Toast.makeText(MainActivity.this,
                    e.toString(),
                    Toast.LENGTH_LONG).show();
        }

    }

    //---Termina funciones
    //---Task


    private class Tarea extends AsyncTask<String,Void,String> {

        private cObjetos.cRespuesta oRespuesta=new cObjetos.cRespuesta();
        protected String doInBackground(String... params) {
            try {

                oRespuesta= ObtieneDatos(mValorParametro[0],mValorParametro[1]);

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
                    switch (Metodo) {
                        case ObtieneDatos:
                            cTablas.Datos.cDato[] aoObjeto = cTablas.Datos.cDato.faoObjet(oRespuesta.sJSON);
                            poblaDatos(aoObjeto);
                            break;
                    }
                }else{
                    switch (Metodo) {
                        case ObtieneDatos:
                            Toast.makeText(MainActivity.this,
                                    "["+oRespuesta.oCodigoRespuesta.id_codigo_respuesta+"] "+
                                            oRespuesta.oCodigoRespuesta.descripcion,
                                    Toast.LENGTH_LONG).show();
                            break;
                        default:
                            toolbar.setTitle("");
                            Funciones.fMensajeGlobal("Error",
                                    "["+oRespuesta.oCodigoRespuesta.id_codigo_respuesta+"] "+
                                            oRespuesta.oCodigoRespuesta.descripcion,MainActivity.this);
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

    public void muestraDato(final cTablas.Datos.cDato oObjeto){
        try {

            final cTablas.Datos.cDato oDato= oObjeto;
            oDato.fotografia="";
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.content_data, null);
            ImageView iFoto = (ImageView) promptsView.findViewById(R.id.iFoto);
            TextView tvNombre = (TextView) promptsView.findViewById(R.id.tvNombre);
            TextView tvApellidos =  promptsView.findViewById(R.id.tvApellidos);
            TextView tvTelefono = (TextView) promptsView.findViewById(R.id.tvTelefono);
            TextView tvCorreo =  promptsView.findViewById(R.id.tvCorreo);
            TextView tvLink =  promptsView.findViewById(R.id.tvLink);
            tvNombre.setText(oObjeto.nombre);
            tvApellidos.setText(oObjeto.apellidos);
            tvTelefono.setText(oObjeto.telefono);
            tvCorreo.setText(oObjeto.correo_electronico);

            String sURL ="http://maps.google.com/?q=";
            sURL += oObjeto.oCoordenada.latitud + "," +
                    oObjeto.oCoordenada.longitud;

            final String url = "<html><a href='"+sURL+"'>"+sURL+"</a></html>";
            tvLink.setText(Html.fromHtml(url));
            //tvLink.setMovementMethod(LinkMovementMethod.getInstance());




            if (!oObjeto.fotografia.equals("")) {
                byte[] imageBytes = Base64.decode(oObjeto.fotografia, Base64.DEFAULT);

                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);


                if (decodedImage != null)
                    iFoto.setImageBitmap(decodedImage);
            }



            AlertDialog.Builder adbConfirmacion = new AlertDialog.Builder(
                    this);

            adbConfirmacion.setView(promptsView);
            adbConfirmacion
                    .setTitle("Información")
                    .setCancelable(false)
                    .setNeutralButton("Ver en mapa",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    //startActivity(intent);
                                    Intent intent = new Intent(MainActivity.this, Mapa.class);
                                    intent.putExtra("sJSONDato",cTablas.Datos.cDato.fsJSON(oDato));
                                    startActivity(intent);
                                }
                            })
                    .setNegativeButton("Cerrar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog adConfirmacion = adbConfirmacion.create();

            // show it
            adConfirmacion.show();

        }catch (Exception ex) {
            Toast.makeText(MainActivity.this,
                    "En confirmaEnviaEvento ocurrió :"+ex.toString(),
                    Toast.LENGTH_LONG).show();
            Funciones.fEscribeLog("En confirmaSolicitudServicio Exepcion:"+ex);
        }

    }





    //---Fin task
}

