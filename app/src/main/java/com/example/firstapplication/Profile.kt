package com.example.firstapplication

/**
 * @author Nikita Romanchuk
 * Test task_Profile
 * KPFU, ITIS Higher School
 * 11-905
 */

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream


class Profile : AppCompatActivity() {
    private lateinit var nameUser: EditText
    private val TAKE_IMAGE_CODE = 100001
    private val TAG = "ProfileActivity"
//    private lateinit var photo: ContactsContract.DisplayPhoto


    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val email = intent.extras!!.getString("email").toString()
        emailUser.text = email

        var profileImageView = findViewById<ListView>(R.id.imageView2)

        btn_log_out.setOnClickListener {
            logOut()
        }
        btn_set_name.setOnClickListener {
            readObserveDataName(email)
        }
        btn_set_surname.setOnClickListener {
            readObserveDataSurname()
        }
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        startActivity(
            Intent(this, MainActivity::class.java)
        )
        finish()
    }

    private fun saveDataName(email: String) {
        val setName = nameUser.text.toString()

        val map = mutableMapOf<String, String>()
        map["email"] = email.toString()
        map["name"] = setName

        FirebaseDatabase.getInstance().reference
            .child("users")
            .child("1")
            .setValue(map)
    }

    private fun saveDataSurname() {
        val setSurname = surnameUser.text.toString()

        val map = mutableMapOf<String, String>()
        map["surname"] = setSurname

        FirebaseDatabase.getInstance().reference
            .child("users")
            .child("1")
            .setValue(map)
    }


    private fun readObserveDataName(email: String) {
        FirebaseDatabase.getInstance().reference
            .child("users")
            .child("1")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(
                        baseContext, "Write your name",
                        Toast.LENGTH_SHORT
                    ).show()
                    nameUser.text = " " as Editable?
                    saveDataName(email)

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val map = p0.value as Map<String, Any>
                    nameUser.text = map["name"] as Editable?
                }

            })
    }

    private fun readObserveDataSurname() {
        FirebaseDatabase.getInstance().reference
            .child("users")
            .child("1")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(
                        baseContext, "Write your name",
                        Toast.LENGTH_SHORT
                    ).show()
                    nameUser.text = " " as Editable?
                    saveDataSurname()

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val map = p0.value as Map<String, Any>
                    nameUser.text = map["surname"] as Editable?
                }

            })

    }

    fun handleImageClick(view: View) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, TAKE_IMAGE_CODE)
        }
    }

    @SuppressLint("WrongViewCast")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TAKE_IMAGE_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    val profileImageView = findViewById<ListView>(R.id.imageView2)
//                    profileImageView.setImageBitmap(bitmap)
                    handleUpload(bitmap)
                }
            }
        }
    }

    private fun handleUpload(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val reference = FirebaseStorage.getInstance().reference
            .child("profileImages")
            .child("$uid.jpeg")
        reference.putBytes(baos.toByteArray())
            .addOnSuccessListener { getDownloadUrl(reference) }
            .addOnFailureListener { e -> Log.e(TAG, "onFailure: ", e.cause) }
    }

    private fun getDownloadUrl(reference: StorageReference) {
        reference.downloadUrl
            .addOnSuccessListener { uri ->
                Log.d(TAG, "onSuccess: $uri")
                setUserProfileUrl(uri)
            }
    }

    private fun setUserProfileUrl(uri: Uri) {
        val user = FirebaseAuth.getInstance().currentUser
        val request = UserProfileChangeRequest.Builder()
            .setPhotoUri(uri)
            .build()
        user?.updateProfile(request)
            ?.addOnSuccessListener {
                Toast.makeText(
                    this@Profile,
                    "Updated succesfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            ?.addOnFailureListener {
                Toast.makeText(
                    this@Profile,
                    "Profile image failed...",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}

// TODO: вылетает активити при добавлении данных пользователя в базу данных. Доделать автарку пользователя

