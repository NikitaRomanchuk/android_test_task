package com.example.firstapplication

/**
 * @author Nikita Romanchuk
 * Test task_SignUp
 * KPFU, ITIS Higher School
 * 11-905
 */

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()

        btn_sgn_up.setOnClickListener {
            signUpUser()
        }
    }

    private fun signUpUser() {
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

        if (!password.text.toString().contains(Regex("""(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{6,}"""))) {
            password.error = "Please, enter valid password (6 or more characters, one uppercase and one uppercase letter, one digit)"
            password.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(baseContext, "Sign Up failed. Try again after some time",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}
