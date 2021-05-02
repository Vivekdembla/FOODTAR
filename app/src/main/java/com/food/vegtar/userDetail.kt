package com.food.vegtar

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.food.vegtar.Dao.MessageDao
import com.food.vegtar.Dao.userDao
import com.food.vegtar.models.Message
import com.food.vegtar.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
    lateinit var fromcart:String
    lateinit var ShopImage:String
    var Pricelist :ArrayList<String>?= ArrayList()
    var Quantitylist :ArrayList<String>?= ArrayList()

    var Foodlist:ArrayList<String>?= ArrayList()
//    var SHARED_PRE="sharedpreference"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        setContentView(R.layout.activity_user_detail)
        auth= FirebaseAuth.getInstance()
        uid  = auth.currentUser!!.uid
        submit = findViewById(R.id.submit)
        cancel = findViewById(R.id.cancel)
        name = findViewById(R.id.NaamEditText)
        phone = findViewById(R.id.PhoneEditText)
        houseno = findViewById(R.id.HouseNoEditText)
        landmark = findViewById(R.id.landmarkEditText)

        fromcart = intent.getStringExtra("fromcart").toString()
        Foodlist = intent.getStringArrayListExtra("Foodlist")
        Pricelist = intent.getStringArrayListExtra("Pricelist")
        Quantitylist = intent.getStringArrayListExtra("Quantitylist")
        ShopImage = intent.getStringExtra("ShopImage").toString()

        Log.e("Checking","${Foodlist?.get(0)}")
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

    if(fromcart!=null){
        submit.setText("Confirm Order")
    }
    else{
        submit.setText("Submit")
    }


        submit.setOnClickListener {
            if(fromcart==null) {
                val user = User(
                    uid,
                    name.text.toString(),
                    auth.currentUser.photoUrl.toString(),
                    phone.text.toString(),
                    houseno.text.toString(),
                    landmark.text.toString()
                )
                userDao.addUser(user)
//            savedata("name",name.text.toString())
//            savedata("phone",phone.text.toString())
//            savedata("houseno",houseno.text.toString())
//            savedata("landmark",landmark.text.toString())
                finish()
            }
            else{
                if(name.text.toString()==""||phone.text.toString().length!=10||houseno.text.toString()==""||landmark.text.toString()=="") {
                    if(name.text.toString()==""){
                        name.setError("Please enter your name")
                    }
                    if (phone.text.toString().length != 10) {
                        phone.setError("Please enter a valid Mobile no.")
                    }
                    if (houseno.text.toString() == "") {
                        houseno.setError("Enter your house number")
                    }
                    if (landmark.text.toString() == "") {
                        landmark.setError("Enter the landmark")
                    }
                }
                else {
                    val intent = Intent(this, orderedActivity::class.java)
                    val userDetail=auth.currentUser
                    val messageDao = MessageDao()
                    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                    val message = Message("${userDetail.uid}$timeStamp","Garlic Bread",
                        "Amandeep","Dairy Mohalla","$timeStamp",
                        "ordered","shopid.toString()",ShopImage)
                    messageDao.addMessage(message)
                    messageDao.addMessageInUser(userDetail.uid,message)
                    Log.e("Checking",timeStamp)
                    startActivity(intent)
                }
            }
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