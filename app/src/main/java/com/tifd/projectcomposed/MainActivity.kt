package com.tifd.projectcomposed

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.tifd.projectcomposed.ui.theme.ProjectComposeDTheme

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContent {
            ProjectComposeDTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyScreen(auth)
                }
            }
        }
    }
}

@Composable
fun MyScreen(auth: FirebaseAuth) {
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    val isFormFilled = emailText.isNotBlank() && passwordText.isNotBlank()
    val buttonColor = if (isFormFilled) ButtonDefaults.buttonColors() else ButtonDefaults.buttonColors(
        containerColor = Color.Gray
    )
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Kartu dengan teks Login diposisikan di tengah
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally) // Memposisikan teks di tengah
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        InputForm(
            icon = Icons.Filled.AccountBox,
            label = "Masukkan Email",
            value = emailText,
            onValueChange = { emailText = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        InputForm(
            icon = Icons.Filled.Lock,
            label = "Masukkan Password",
            value = passwordText,
            onValueChange = { passwordText = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol login dengan warna lebih menarik dan sudut membulat
        Button(
            onClick = {
                if (isFormFilled) {
                    auth.signInWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Login berhasil, pindah ke ListActivity
                                val intent = Intent(context, ListActivity::class.java)
                                context.startActivity(intent)
                            } else {
                                // Gagal login
                                Toast.makeText(context, "Login gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Harap isi email dan password", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = isFormFilled,
            colors = buttonColor
        ) {
            Text("Login", style = MaterialTheme.typography.bodyLarge, color = Color.White)
        }
    }
}

@Composable
fun InputForm(
    icon: ImageVector,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation
        )
    }
}
