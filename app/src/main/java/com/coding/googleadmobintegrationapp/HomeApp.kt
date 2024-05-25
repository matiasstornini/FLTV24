package com.coding.googleadmobintegrationapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.coding.googleadmobintegrationapp.R

class HomeApp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_app)

        val cardButton = findViewById<Button>(R.id.cardButton)

        // Configurar OnClickListener para el botón cardButton
        cardButton.setOnClickListener {
            // Crear un Intent para abrir la actividad CalendarPage
            val intent = Intent(this, CalendarPage::class.java)

            // Iniciar la actividad CalendarPage
            startActivity(intent)
        }
    }
}
