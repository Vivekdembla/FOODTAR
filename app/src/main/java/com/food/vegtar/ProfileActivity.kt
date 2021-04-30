package com.food.vegtar

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.food.vegtar.Dao.userDao
import com.food.vegtar.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var userName:TextView
    private lateinit var userNumber:TextView
    private lateinit var editProfile:ImageView
    private lateinit var orderBack:ImageView
    lateinit var userDao: userDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        setContentView(R.layout.activity_profile)
        val userImage = findViewById<ImageView>(R.id.userImage)
        orderBack = findViewById(R.id.orderBack)
        userName = findViewById(R.id.userName)
        userNumber = findViewById(R.id.userNumber)
        editProfile = findViewById(R.id.nameBack)
        auth = Firebase.auth
        currentUser = auth.currentUser!!
        userDao = userDao()
        Glide.with(userImage.context ).load(currentUser.photoUrl).apply(RequestOptions.circleCropTransform()).into(userImage)
        editProfile.setOnClickListener {
            val intent = Intent(this,userDetail::class.java)
            startActivity(intent)
        }
        orderBack.setOnClickListener {
            val intent = Intent(this,orderList::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        GlobalScope.launch (Dispatchers.Main){
            val user = userDao.getUserById(currentUser.uid).await().toObject(User::class.java)
            userNumber.setText("+91 ${user?.phone.toString()}")
            userName.setText(user?.displayName.toString())
        }
    }
}