package com.coding.googleadmobintegrationapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.coding.googleadmobintegrationapp.R

class NoPlayer : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.no_player)
        val cardButton = findViewById<Button>(R.id.cardButton)

        cardButton.setOnClickListener {
            // URL de la página web que deseas abrir
            val url = "https://play.google.com/store/apps/details?id=com.card.red.demo"

            // Crear un Intent con la acción ACTION_VIEW y la URL como datos
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            // Verificar si hay navegadores web disponibles para manejar el Intent
            if (intent.resolveActivity(packageManager) != null) {
                // Iniciar la actividad del navegador web externo
                startActivity(intent)
            } else {
                // Manejar el caso en el que no hay navegadores web disponibles
                // Aquí podrías mostrar un mensaje de error o proporcionar una alternativa
            }
        }
    }
}