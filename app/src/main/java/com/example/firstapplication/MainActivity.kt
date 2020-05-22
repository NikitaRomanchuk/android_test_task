package com.example.firstapplication

/**
 * @author Nikita Romanchuk
 * Test task
 * KPFU, ITIS Higher School
 * 11-905
 */

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        btn_sgn_up.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        btnLogin.setOnClickListener {
            doLogin();
        }
    }

    private fun doLogin() {
        if (email.text.toString().isEmpty()) {
            email.error = "Please, enter email"
            email.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            email.error = "Please, enter valid email"
            email.requestFocus()
            return
        }

        if (password.text.toString().isEmpty()) {
            password.error = "Please, enter email"
            password.requestFocus()
            return
        }

        if (!password.text.toString()
                .contains(Regex("""(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{6,}"""))
        ) {
            password.error =
                "Please, enter valid password (6 or more characters, one " +
                        "uppercase and one uppercase letter, one digit)"
            password.requestFocus()
            return
        }

        auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    updateUI(user)
                } else {
                        updateUI(null)
                }
            }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val text = email.text.toString()
            Toast.makeText(baseContext, "Welcome!",
                Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Profile::class.java)
                .putExtra("email", text))
            finish()

        } else {
            Toast.makeText(baseContext, "Login failed, try again",
                Toast.LENGTH_SHORT).show()
        }
    }
}


