package com.food.vegtar

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.food.vegtar.Dao.userDao
import com.food.vegtar.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class userDetail : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    lateinit var uid: String
    lateinit var currentUser: User
    lateinit var submit:Button
    lateinit var cancel:Button
    lateinit var name:EditText
    lateinit var phone:EditText
    lateinit var houseno :EditText
    lateinit var landmark :EditText
//    var SHARED_PRE="sharedpreference"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        auth= FirebaseAuth.getInstance()
        uid  = auth.currentUser.uid
        submit = findViewById(R.id.submit)
        cancel = findViewById(R.id.cancel)
        name = findViewById(R.id.NaamEditText)
        phone = findViewById(R.id.PhoneEditText)
        houseno = findViewById(R.id.HouseNoEditText)
        landmark = findViewById(R.id.landmarkEditText)
        val userDao = userDao()
        GlobalScope.launch(Dispatchers.IO) {
            currentUser = userDao.getUserById(uid).await().toObject(User::class.java)!!
            withContext(Dispatchers.Main){
                name.setText(currentUser.displayName.toString())
                phone.setText(currentUser.phone.toString())
                houseno.setText(currentUser.houseNo.toString())
                landmark.setText(currentUser.landmark.toString())
            }
        }


        submit.setOnClickListener {
            val user = User(uid,name.text.toString(),auth.currentUser.photoUrl.toString(),phone.text.toString(),
                    houseno.text.toString(),landmark.text.toString())
            userDao.addUser(user)
//            savedata("name",name.text.toString())
//            savedata("phone",phone.text.toString())
//            savedata("houseno",houseno.text.toString())
//            savedata("landmark",landmark.text.toString())
            finish()
        }
        cancel.setOnClickListener {
            finish()
        }
    }
//    fun savedata(key:String, x:String){
//        val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
//        val editor: SharedPreferences.Editor = sharedPreferences.edit()
//        editor.putString(key, x)
//        editor.apply()
//    }
}