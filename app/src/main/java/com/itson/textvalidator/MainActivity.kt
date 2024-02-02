package com.itson.textvalidator

import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AlertDialog
import com.google.android.material.slider.Slider
import com.itson.textvalidator.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupSlider()
        setupFocusListeners()

        binding.submitButton.setOnClickListener { submitForm() }
    }

    private fun setupSlider() {
        val slider = findViewById<Slider>(R.id.slider)
        val sliderLabel = findViewById<TextView>(R.id.sliderLabel)

        slider.stepSize = 1f
        slider.valueFrom = 1f
        slider.valueTo = 100f
        slider.value = 1f  // Establece un valor inicial dentro del rango (por ejemplo, 50)

        slider.addOnChangeListener { _, value, _ ->
            val intValue = value.toInt()
            sliderLabel.text = intValue.toString()
        }
    }

    private fun setupFocusListeners() {
        emailFocusListener()
        passwordFocusListener()
        phoneFocusListener()
    }

    private fun submitForm() {
        binding.emailContainer.helperText = validarEmail()
        binding.passwordContainer.helperText = validarPassword()
        binding.phoneContainer.helperText = validarTelefono()
        binding.sliderLabel.text = binding.sliderLabel.text.toString()

        val emailValido = binding.emailContainer.helperText == null
        val passwordValido = binding.passwordContainer.helperText == null
        val telefonoValido = validarTelefono() == null
        val edadValida = validarEdad()

        if (emailValido && passwordValido && telefonoValido && edadValida)
            resetFormulario()
        else
            formularioInvalido(edadValida)
    }

    private fun formularioInvalido(edadValida: Boolean) {
        var mensaje = ""
        if (binding.emailContainer.helperText != null)
            mensaje += "\n\nEmail: " + binding.emailContainer.helperText
        if (binding.passwordContainer.helperText != null)
            mensaje += "\n\nContraseña: " + binding.passwordContainer.helperText
        if (binding.phoneContainer.helperText != null)
            mensaje += "\n\nTeléfono: " + binding.phoneContainer.helperText
        if (!edadValida)
            mensaje += "\n\nEdad: La edad debe ser igual o mayor a 18"

        AlertDialog.Builder(this)
            .setTitle("Formulario Inválido")
            .setMessage(mensaje)
            .setPositiveButton("Entendido") { _, _ ->
                // no hacer nada
            }
            .show()
    }

    private fun resetFormulario() {
        var mensaje = "Email: " + binding.emailEditText.text
        mensaje += "\nContraseña: " + binding.passwordEditText.text
        mensaje += "\nTeléfono: " + binding.phoneEditText.text
        mensaje += "\nEdad: " + binding.sliderLabel.text.toString()
        AlertDialog.Builder(this)
            .setTitle("Formulario Enviado")
            .setMessage(mensaje)
            .setPositiveButton("Entendido") { _, _ ->
                binding.emailEditText.text = null
                binding.passwordEditText.text = null
                binding.phoneEditText.text = null

                binding.emailContainer.helperText = getString(R.string.required)
                binding.passwordContainer.helperText = getString(R.string.required)
                binding.phoneContainer.helperText = getString(R.string.required)
            }
            .show()
    }

    private fun emailFocusListener() {
        binding.emailEditText.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.emailContainer.helperText = validarEmail()
            }
        }
    }

    private fun validarEmail(): String? {
        val textoEmail = binding.emailEditText.text.toString()
        if (!Patterns.EMAIL_ADDRESS.matcher(textoEmail).matches()) {
            return "Dirección de Email Inválida"
        }
        return null
    }

    private fun passwordFocusListener() {
        binding.passwordEditText.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.passwordContainer.helperText = validarPassword()
            }
        }
    }

    private fun validarPassword(): String? {
        val textoContraseña = binding.passwordEditText.text.toString()
        if (textoContraseña.length < 8) {
            return "Contraseña con un Mínimo de 8 Caracteres"
        }
        if (!textoContraseña.matches(".*[A-Z].*".toRegex())) {
            return "Debe Contener 1 Carácter en Mayúscula"
        }
        if (!textoContraseña.matches(".*[a-z].*".toRegex())) {
            return "Debe Contener 1 Carácter en Minúscula"
        }
        if (!textoContraseña.matches(".*[@#\$%^&+=].*".toRegex())) {
            return "Debe Contener 1 Carácter Especial (@#\$%^&+=)"
        }

        return null
    }

    private fun phoneFocusListener() {
        binding.phoneEditText.setOnFocusChangeListener { _, focused ->
            if (!focused) {
                binding.phoneContainer.helperText = validarTelefono()
            }
        }
    }

    private fun validarTelefono(): String? {
        val textoTelefono = binding.phoneEditText.text.toString()
        if (!textoTelefono.matches(".*[0-9].*".toRegex())) {
            return "Debe Contener Solo Dígitos"
        }
        if (textoTelefono.length != 10) {
            return "Debe Contener 10 Dígitos"
        }

        return null
    }

    private fun validarEdad(): Boolean {
        val valorEdad = binding.sliderLabel.text.toString().toIntOrNull()
        return valorEdad != null && valorEdad >= 18
    }
}
