package com.mario.rayas.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Build;
import android.os.Looper;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;


import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import com.mario.rayas.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import com.mario.rayas.util.*;


public class Mapa extends FragmentActivity implements OnMapReadyCallback {

    android.support.v7.widget.CardView cvInformacion;
    TextView txtInformacion;
    com.getbase.floatingactionbutton.FloatingActionButton fabCierra;
    com.getbase.floatingactionbutton.FloatingActionsMenu fabMenuMapa;
    GoogleMap mGoogleMap;
    MarkerOptions mmoMaker=null;

    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;


    private ProgressDialog dialogoProcesando = null;
    private String mMETHOD_NAME="";
    private String[] mNombreParametro=null;
    private String[] mValorParametro=null;
    boolean mInicioRastreo=false;
    boolean mbTermina=false;

    private int mYear;
    private int mMonth;
    private int mDay;
    private String mFecha = "";
    private int iTipoReporte=0;

    float ZOOMEQUIPO=15;

    cTablas.Datos.cDato[] maoDato=null;
    cTablas.Datos.cDato moDato=null;


    Toast toMensaje;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_map);


            mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);

            cvInformacion = findViewById(R.id.cvInformacion);
            txtInformacion = findViewById(R.id.txtInformacion);

            fabMenuMapa = findViewById(R.id.fabMenuMapa);

            fabCierra = findViewById(R.id.fabCierra);
            fabCierra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        mbTermina=true;
                        finish();
                    } catch (Exception ex) {
                        Funciones.fEscribeLog("En Mapa.fabCierra.onClick Exepcion:"+ex);
                    }
                }
            });

            

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            Intent intent = getIntent();



            //Equipos
            String sJSONDatos = intent.getStringExtra("sJSONDatos");
            if (sJSONDatos!=null && !sJSONDatos.equals("")) {
                maoDato=cTablas.Datos.cDato.faoObjet(sJSONDatos);
            }

            String sJSONDato = intent.getStringExtra("sJSONDato");
            if (sJSONDato!=null && !sJSONDato.equals("")) {
                moDato=cTablas.Datos.cDato.foObjet(sJSONDato);
            }


        } catch (Exception e) {
            Toast.makeText(Mapa.this,
                    "En onCreate exepción:\n"+e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }




    public void onResume() {
        try {
            super.onResume();


        } catch (Exception e) {
            Funciones.fEscribeLog("En Mapa.onResume excepción: " + e.toString());
            Toast.makeText(Mapa.this,
                    "En onResume excepción: " + e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void finish() {

        try {
            //overridePendingTransition(0, 0);
            if (mFusedLocationClient != null) {

                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }

        } catch (Exception e) {
            //showResponse("En setupAutoCompleteFragment: "+ e.toString());
            Toast.makeText(Mapa.this,
                    "En finish.finish: "+ e.toString(),
                    Toast.LENGTH_LONG).show();
        }
        super.finish();
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            boolean bPermiso=false;
            mGoogleMap = googleMap;


            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    bPermiso=true;
                } else {
                    //Request Location Permission
                    checkLocationPermission();
                }
            }
            else {

                bPermiso=true;
            }
            if (bPermiso==true){

                mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }
                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        try {
                            MarkerOptions markerOptions = new MarkerOptions();
                            LatLng latLng=marker.getPosition();
                            markerOptions.position(latLng);
                            String sLat = new DecimalFormat("##.####").format(latLng.latitude);
                            String sLon = new DecimalFormat("##.####").format(latLng.longitude);

                            markerOptions.title("Posición ("+sLat+","+sLon+")");
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            marker.remove();
                            mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);//Muestra Globo
                            mCurrLocationMarker.setDraggable(true);
                            Toast.makeText(Mapa.this,
                                    "Punto:"+sLat+","+sLon,
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(Mapa.this,
                                    "En onMapReady exepción:\n"+e.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

                    mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            //El mapa ya esrá listo
                            if (maoDato!=null) {
                                muestraPosicionDatosMapa();

                            }

                            if (moDato!=null) {
                                muestraPosicionDatoMapa();

                            }
                        }
                    });

            }
            mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                @Override
                public void onCameraIdle() {
                    //ZOOMEQUIPO = mGoogleMap.getCameraPosition().zoom;
                    //use zoomLevel value..
                }
            });

            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Integer clickCount = (Integer) marker.getTag();
                    String sTitulo= marker.getTitle();


                    return false;
                }
            });


        } catch (Exception e) {
            Toast.makeText(Mapa.this,
                    "En onMapReady exepción:\n"+e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }







    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            try {
                List<Location> locationList = locationResult.getLocations();
                if (locationList.size() > 0) {
                    //The last location in the list is the newest
                    Location location = locationList.get(locationList.size() - 1);

                    //Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                    mLastLocation = location;
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }

                    //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));



                }
            } catch (Exception e) {
                Toast.makeText(Mapa.this,
                        "En mLocationCallback.onLocationResult exepción:\n"+e.toString(),
                        Toast.LENGTH_LONG).show();
            }
        }
    };



    public static final int PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Permiso solicitado")
                        .setMessage("La aplicación requiere permiso de localización")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Mapa.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
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
            Funciones.fEscribeLog("En Mapa.solicitaConfirmacion excepción:"+e.toString());
        }
    }

    public void limpiaMapa() {
        try {
            mGoogleMap.clear();

        } catch (Exception e) {
            Toast.makeText(Mapa.this,
                    e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void muestraPosicionDatoMapa(){
        try {
            LatLng latLng = new LatLng(Double.parseDouble(moDato.oCoordenada.latitud),Double.parseDouble(moDato.oCoordenada.longitud));

            mmoMaker = new MarkerOptions();
            mmoMaker.position(latLng);
            String sTitulo=moDato.nombre+" "+moDato.apellidos;
            mmoMaker.title(sTitulo);
            mmoMaker.icon(BitmapDescriptorFactory.fromResource(R.drawable.flecha_roja));

            mmoMaker.rotation(0);

            mCurrLocationMarker = mGoogleMap.addMarker(mmoMaker);//Muestra Globo

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOMEQUIPO));
            txtInformacion.setText(sTitulo);
        } catch (Exception e) {
            Funciones.fEscribeLog("En Mapa.muestraEquipoMapa excepción:"+e.toString());
            Toast.makeText(Mapa.this,
                    "En Mapa.muestraEquiposMapa exepción:\n"+e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public void muestraPosicionDatosMapa(){
        try {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i=0;i<maoDato.length;i++){
                LatLng latLng = new LatLng(Double.parseDouble(maoDato[i].oCoordenada.latitud),Double.parseDouble(maoDato[i].oCoordenada.longitud));
                mmoMaker = new MarkerOptions();
                mmoMaker.position(latLng);
                mmoMaker.title(maoDato[i].nombre+" "+maoDato[i].apellidos);

                mmoMaker.icon(BitmapDescriptorFactory.fromResource(R.drawable.flecha_roja));

                mmoMaker.rotation(0);

                //mmoMaker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mCurrLocationMarker = mGoogleMap.addMarker(mmoMaker);//Muestra Globo
                builder.include(latLng);
            }
            LatLngBounds bounds = builder.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);

            mGoogleMap.animateCamera(cu);


        } catch (Exception e) {
            Funciones.fEscribeLog("En Mapa.muestraEquiposMapa excepción:"+e.toString());
            Toast.makeText(Mapa.this,
                    "En Mapa.muestraEquiposMapa exepción:\n"+e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }


    //---Termian funciones
    //---Task





    //---Fin task
    //--LayOut

    //--Fin layout
}



