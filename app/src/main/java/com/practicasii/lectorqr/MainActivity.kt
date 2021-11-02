package com.practicasii.lectorqr

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import org.json.JSONException
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    var cardView1: CardView? = null
    var cardView2:CardView? = null
    var btnScan:Button? = null
    var btnEnterCode:Button? = null
    var btnEnter:Button? = null
    var edtCode:EditText? = null
    var tvTexto:TextView? = null
    var hide: Animation? = null
    var reveal:Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        cardView1 = findViewById(R.id.cardView1)
        cardView2 = findViewById(R.id.cardView2)
        btnScan = findViewById(R.id.btnScan)
        btnEnterCode = findViewById(R.id.btnEnterCode)
        btnEnter = findViewById(R.id.btnEnter)
        edtCode = findViewById(R.id.edtCode)
        tvTexto = findViewById(R.id.tvTexto)


        hide = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
        reveal = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)


        tvTexto!!.startAnimation(reveal)
        cardView2!!.startAnimation(reveal)
        tvTexto!!.setText("Scan QR Code Here")
        cardView2!!.visibility = View.VISIBLE


        btnScan!!.setOnClickListener{
            tvTexto!!.startAnimation(reveal)
            cardView1!!.startAnimation(hide)
            cardView2!!.startAnimation(reveal)

            cardView2!!.visibility = View.VISIBLE
            cardView2!!.visibility = View.GONE
            tvTexto!!.setText("Scan QR Code Here")


        }

        cardView2!!.setOnClickListener {
            cameraTask()
        }

        btnEnter!!.setOnClickListener{
            if (edtCode!!.text.toString().isNullOrEmpty()){
                Toast.makeText(this, "Please enter code", Toast.LENGTH_SHORT).show()
            }else{

            var value = edtCode!!.text.toString()

            Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
            }
        }

        btnEnterCode!!.setOnClickListener{
            tvTexto!!.startAnimation(reveal)
            cardView1!!.startAnimation(reveal)
            cardView2!!.startAnimation(hide)

            cardView2!!.visibility = View.GONE
            cardView2!!.visibility = View.VISIBLE
            tvTexto!!.setText("Enter QR Code Here")
        }

    }


    private fun hasCameraAccess():Boolean
    {
       return EasyPermissions.hasPermissions(this,android.Manifest.permission.CAMERA)
    }


    private fun cameraTask(){
        if (hasCameraAccess()){
            var qrScanner = IntentIntegrator(this)
            qrScanner.setPrompt("Escanea un codigo qr")
            qrScanner.setCameraId(0)
            qrScanner.setOrientationLocked(true)
            qrScanner.setBeepEnabled(true)
            qrScanner.captureActivity = CaptureActivity::class.java
            qrScanner.initiateScan()

        }else{
            EasyPermissions.requestPermissions(this,
                "La app requiere acceso a la camara",
                123,android.Manifest.permission.CAMERA)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        var result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        if (result != null){
            if (result.contents == null){
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show()
                edtCode!!.setText("")
            }else{
                try {

                    cardView1!!.startAnimation(reveal)
                    cardView2!!.startAnimation(hide)
                    cardView1!!.visibility = View.VISIBLE
                    cardView2!!.visibility = View.GONE
                    edtCode!!.setText(result.contents.toString())
                }catch (exception:JSONException){
                    Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
                    edtCode!!.setText("")
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE){
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }



    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onRationaleAccepted(requestCode: Int) {

    }

    override fun onRationaleDenied(requestCode: Int) {

    }
}