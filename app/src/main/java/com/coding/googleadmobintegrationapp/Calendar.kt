package com.coding.googleadmobintegrationapp


import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

data class MatchData(val date: String, val competencia: String,val partido: String, val switch: String, val btn1: String, val btn2: String, val btn3: String,val version: String)

class CalendarPage : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
//        requestPerm()
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE )!=
            PackageManager.PERMISSION_GRANTED){ ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0) }

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        val client = OkHttpClient()
        val uid = "1uD_UtAaYl8lh7w_8VWRCnVi-Ugat-O_2V-puezenbdw"
        val sheet = "AdminOLD"
        val request = Request.Builder()
            .url("https://docs.google.com/spreadsheets/d/$uid/gviz/tq?tqx=out:json&sheet=$sheet")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val jsonString = response.body?.string()
                if (jsonString != null) {
                    try {
                        // Eliminar las partes no deseadas de la respuesta JSON
                        val startIndex = jsonString.indexOf('{')
                        val endIndex = jsonString.lastIndexOf('}') + 1
                        val jsonResponse = jsonString.substring(startIndex, endIndex)

                        val jsonObject = JSONObject(jsonResponse)
                        val rowsArray = jsonObject.getJSONObject("table").getJSONArray("rows")

                        val dataList = mutableListOf<MatchData>() // Lista para almacenar objetos MatchData

// Itera sobre el array de partidos y competencias
                        for (i in 0 until rowsArray.length()) {
                            val row = rowsArray.getJSONObject(i)
                            val cells = row.getJSONArray("c")

                            val date = cells.getJSONObject(0).optLong("v", 0) // Obtener el timestamp como un Long
                            val horaMinutos = convertirTimestampAHoraMinutos(date)

                            val partido = cells.getJSONObject(1).optString("v", "") // Obtener el partido
                            val competencia = cells.getJSONObject(2).optString("v", "")
                            val switch = cells.getJSONObject(4).optString("v", "")
                            val btn1 = cells.getJSONObject(5).optString("v", "")
                            val btn2 = cells.getJSONObject(6).optString("v", "")
                            val btn3 = cells.getJSONObject(7).optString("v", "")
                            val version = if (rowsArray.length() > 0) {
                                rowsArray.getJSONObject(0).getJSONArray("c").optJSONObject(12)?.optString("v", "") ?: ""
                            } else {
                                ""
                            }

                            Log.d("switch", switch)

                            dataList.add(MatchData(horaMinutos,competencia, partido, switch,btn1,btn2,btn3,version)) // Agregar los datos a la lista
                        }


                        // Actualizar las vistas en el hilo principal
                        runOnUiThread {
                            // Llama a la función para configurar los datos en la CardView
                            setupCardView(dataList)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            fun convertirTimestampAHoraMinutos(timestamp: Long): String {
                // Crear una instancia de Date usando el timestamp
                val date = Date(timestamp * 1000) // El timestamp está en segundos, por lo que lo multiplicamos por 1000 para convertirlo a milisegundos

                // Crear un objeto SimpleDateFormat para formatear la fecha
                val format = SimpleDateFormat("HH:mm") // HH:mm para formato de hora:minutos

                // Formatear la fecha y devolverla como una cadena
                return format.format(date)
            }
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    val alertDialogBuilder = AlertDialog.Builder(this@CalendarPage)
                    alertDialogBuilder.setTitle("Error")
                    alertDialogBuilder.setMessage("No se puede conectar a Internet. Por favor, verifica tu conexión.")
                    alertDialogBuilder.setPositiveButton("Aceptar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()
                }
            }
        })
    }

    // Función para configurar los datos en la CardView
    private fun setupCardView(dataList: MutableList<MatchData>) {
        // Obtén el layout que contiene las CardViews
        val linearLayout = findViewById<LinearLayout>(R.id.cardView)

        // Itera sobre los datos obtenidos y crea una CardView para cada uno
        for ((partido, competencia, horaMinutos, switch,btn1,btn2,btn3,version) in dataList) {
            if (version == "v1") {

                // Crea una nueva instancia de CardView
                val cardView = layoutInflater.inflate(R.layout.card_view_item, null) as CardView
                //Log.d("btns", "$btn1 $btn2")
                Log.d("version", version)

                // Obtén los elementos de la CardView
                val dateTextView = cardView.findViewById<TextView>(R.id.date)
                val matchTextView = cardView.findViewById<TextView>(R.id.match)
                val competitionTextView = cardView.findViewById<TextView>(R.id.competition)
                val button = cardView.findViewById<Button>(R.id.button)
                val button2 = cardView.findViewById<Button>(R.id.button2)
                val button3 = cardView.findViewById<Button>(R.id.button3)

                // Configura los datos en los elementos de la CardView
                competitionTextView.text =
                    competencia // Configura la competencia en el TextView correspondiente
                dateTextView.text =
                    horaMinutos // Configura la hora y minutos en el TextView correspondiente
                matchTextView.text = partido // Configura el partido en el TextView correspondiente

                // Si el switch es igual a 1, muestra el botón
                if (switch == "1.0") {
                    if (btn1 != "null") {
                        button.visibility = View.VISIBLE // Muestra el botón
                        button.setOnClickListener {
                            try {
                                val intent = Intent("com.player.matt.ACTION_PLAY_VIDEO")
                                intent.putExtra("m3u8_url", btn1)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                                applicationContext.startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                // Si la actividad no puede ser encontrada o la aplicación no está instalada, abre la actividad NoPlayer
                                val intent = Intent(this, NoPlayer::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                    if (btn2 != "null") {
                        button2.visibility = View.VISIBLE // Muestra el botón
                        button2.setOnClickListener {
                            try {
                                val intent = Intent("com.player.matt.ACTION_PLAY_VIDEO")
                                intent.putExtra("m3u8_url", btn2)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                                applicationContext.startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                // Si la actividad no puede ser encontrada o la aplicación no está instalada, muestra un mensaje

                            }
                        }
                    }
                    if (btn3 != "null") {
                        button3.visibility = View.VISIBLE // Muestra el botón
                        button3.setOnClickListener {
                            try {
                                val intent = Intent("com.player.matt.ACTION_PLAY_VIDEO")
                                intent.putExtra("m3u8_url", btn3)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                                applicationContext.startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                // Si la actividad no puede ser encontrada o la aplicación no está instalada, muestra un mensaje

                            }
                        }
                    }
                } else {
                    button.visibility = View.GONE // Oculta el botón si no se cumple la condición
                }

                // Agrega la CardView al layout principal
                linearLayout.addView(cardView)
            }else{
                val alertDialogBuilder = AlertDialog.Builder(this@CalendarPage)
                alertDialogBuilder.setTitle("Error")
                alertDialogBuilder.setMessage("Actualiazr app.")
                alertDialogBuilder.setPositiveButton("Aceptar") { dialog, _ ->
                    dialog.dismiss()
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }
        }
    }

}
