package com.example.platensehoy

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.platensehoy.ui.theme.MarronPlatense
import com.google.firebase.auth.FirebaseAuth

@Composable
fun PantallaLoginScreen(onBack: () -> Unit, onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var loginExitoso by remember { mutableStateOf(false) }
    val user = auth.currentUser

    var usuario by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Encabezado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MarronPlatense)
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("PlatenseHOY", fontSize = 32.sp, color = Color.White)
        }

        // Menú superior
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MarronPlatense)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val secciones = listOf("Últimas noticias", "Fútbol", "Básquet", "Login")
            secciones.forEach { seccion ->
                Text(
                    text = seccion,
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        when (seccion) {
                            "Fútbol" -> onNavigate("futbol")
                            "Básquet" -> onNavigate("basquet")
                            "Últimas noticias" -> onNavigate("home")
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (loginExitoso || user != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Login exitoso",
                    fontSize = 22.sp,
                    color = MarronPlatense
                )
                Text(
                    text = "Bienvenido, ${user?.email ?: usuario}",
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { onNavigate("home") }) {
                    Text("Ir al inicio")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    auth.signOut()
                    Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                    loginExitoso = false
                    usuario = ""
                    contraseña = ""
                }) {
                    Text("Cerrar sesión")
                }
            }
        } else {
            // FORMULARIO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Iniciar Sesión", fontSize = 24.sp, color = MarronPlatense)

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = usuario,
                    onValueChange = { usuario = it },
                    label = { Text("Correo electrónico") },
                    isError = error.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = contraseña,
                    onValueChange = { contraseña = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    isError = error.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        error = ""
                        auth.signInWithEmailAndPassword(usuario.trim(), contraseña.trim())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    loginExitoso = true
                                } else {
                                    error = task.exception?.message ?: "Error desconocido"
                                }
                            }
                    },
                    enabled = usuario.isNotBlank() && contraseña.isNotBlank()
                ) {
                    Text("Ingresar")
                }

                if (error.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                TextButton(onClick = onBack) {
                    Text("Volver al inicio")
                }
            }
        }
    }
}
