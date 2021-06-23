package com.food.testing

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.food.testing.Dao.MessageDao
import com.food.testing.Dao.userDao
import com.food.testing.models.Message
import com.food.testing.models.TokenId
import com.food.testing.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class userDetail : AppCompatActivity() {
    var SHARED_PRE = "sharedpreference"
    lateinit var auth: FirebaseAuth
    lateinit var uid: String
    lateinit var currentUser: User
    lateinit var submit: Button
    lateinit var cancel: Button
    lateinit var name: EditText
    lateinit var phone: EditText
    lateinit var houseno: EditText
    lateinit var landmark: EditText
    var fromcart: String? = null
    var ShopImage: String?=null
    lateinit var DateTime: String
    var shopId: String? = null
    var Delivery: Int? = null
    var OwnerPhone: String? = null
    var Pricelist: ArrayList<String>? = ArrayList()
    var Quantitylist: ArrayList<Int>? = ArrayList()
    lateinit var progress_bar: ProgressBar
    lateinit var messageEditText: EditText
    lateinit var messageTextView: TextView

    var Foodlist: ArrayList<String>? = ArrayList()

    //    var SHARED_PRE="sharedpreference"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        setContentView(R.layout.activity_user_detail)
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser!!.uid
        messageEditText = findViewById(R.id.specialMessageEditText)
        messageTextView = findViewById(R.id.specialMessage)
        submit = findViewById(R.id.submit)
        cancel = findViewById(R.id.cancel)
        name = findViewById(R.id.NaamEditText)
        phone = findViewById(R.id.PhoneEditText)
        houseno = findViewById(R.id.HouseNoEditText)
        landmark = findViewById(R.id.landmarkEditText)
        progress_bar = findViewById(R.id.progress_bar)
        var sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
        progress_bar.visibility = View.GONE
        fromcart = intent.getStringExtra("fromcart").toString()
        Foodlist = intent.getStringArrayListExtra("Foodlist")
        Pricelist = intent.getStringArrayListExtra("Pricelist")
        Quantitylist = intent.getIntegerArrayListExtra("Quantitylist")
//        ShopImage = intent.getStringExtra("ShopImage").toString()
        ShopImage = sharedPreferences.getString("ShopImage", " ")!!
        DateTime = intent.getStringExtra("DateTime").toString()

        messageTextView.visibility = View.GONE
        messageEditText.visibility = View.GONE
        Log.e("Checking", "DateTime -> $DateTime")
        val ShopName = sharedPreferences.getString("name", "Name")

        Log.e("Checking", "${Foodlist?.get(0)}")
        val userDao = userDao()
        GlobalScope.launch(Dispatchers.IO) {
            currentUser = userDao.getUserById(uid).await().toObject(User::class.java)!!
            withContext(Dispatchers.Main) {
                name.setText(currentUser.displayName.toString())
                phone.setText(currentUser.phone.toString())
                houseno.setText(currentUser.houseNo.toString())
                landmark.setText(currentUser.landmark.toString())
            }
        }


        if (intent.getStringExtra("fromcart").toString() == "fromcart") {
            submit.text = "Confirm Order"
            messageTextView.visibility = View.VISIBLE
            messageEditText.visibility = View.VISIBLE
        } else {
            submit.text = "Submit"
        }


        submit.setOnClickListener {
            if (fromcart != "fromcart") {
                val user = User(
                    uid,
                    name.text.toString(),
                    auth.currentUser.photoUrl.toString(),
                    phone.text.toString(),
                    houseno.text.toString(),
                    landmark.text.toString()
                )
                userDao.addUser(user)
                Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                val phoneText = phone.text.toString()
                var a=1
                for(i in phoneText.indices){
                    if(phoneText[i].code < 48 || phoneText[i].code >57){
                        a=0
                    }
                }
                if (name.text.toString() == "" || phone.text.toString().length != 10 || a==0 || houseno.text.toString() == "" || landmark.text.toString() == "") {
                    if (name.text.toString() == "") {
                        name.error = "Please enter your name"
                    }
                    if (phone.text.toString().length != 10||a==0) {
                        phone.error = "Please enter a valid Mobile no."
                    }
                    if (houseno.text.toString() == "") {
                        houseno.error = "Enter your house number"
                    }
                    if (landmark.text.toString() == "") {
                        landmark.error = "Enter the landmark"
                    }
                } else {
                    progress_bar.visibility = View.VISIBLE
                    val user = User(
                        uid,
                        name.text.toString(),
                        auth.currentUser.photoUrl.toString(),
                        phone.text.toString(),
                        houseno.text.toString(),
                        landmark.text.toString()
                    )
                    userDao.addUser(user)
                    var Price = 0.0
                    var Order: String? = ""
                    for (i in 0 until Foodlist!!.size) {
                        Price += Quantitylist!![i] * (Pricelist!![i].toDouble())
                        Order += "${Foodlist!![i]}*${Quantitylist!![i]}=${
                            Integer.valueOf(Quantitylist!![i]) *
                                    Pricelist!![i].toDouble()
                        },\n"
                    }
                    Log.e("Checking", "Amount -> $Price \n$Order")
                    shopId = sharedPreferences.getString("shopId", " ")
                    Delivery = sharedPreferences.getInt("delivery", 0)
                    OwnerPhone = sharedPreferences.getString("phone","1000000000")
                    val intent = Intent(this, orderedActivity::class.java)
                    val userDetail = auth.currentUser
                    val messageDao = MessageDao()
                    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                    val message = Message(
                        "${userDetail.uid}$timeStamp",
                        Order,
                        name.text.toString(),
                        "${houseno.text}\n${landmark.text}",
                        DateTime,
                        "Ordered",
                        ShopImage!!,
                        Price,
                        ShopName.toString(),
                        Delivery!!,
                        phone.text.toString()
                    )
                    message.number = OwnerPhone!!
                    message.specialMessage = messageEditText.text.toString()
                    FirebaseMessaging.getInstance().token.addOnCompleteListener {
                        message.userTokenId = it.result!!
                        GlobalScope.launch(Dispatchers.IO) {
                            if (isConnected()) {
                                notification()
                                messageDao.addMessage(message, shopId!!)
                                messageDao.addMessageInUser(userDetail.uid, message, message.key!!)
                                Log.e("Checking", timeStamp)
                                intent.putExtra("ShopName", message.name)
                                intent.putExtra("Amount", message.amount)
                                intent.putExtra("Delivery", message.deliveryCharges)
                                intent.putExtra("Status", message.status)
                                intent.putExtra("Address", message.address)
                                intent.putExtra("Message", message.message)
                                intent.putExtra("specialMessage",message.specialMessage)


                                val db = FirebaseFirestore.getInstance()
                                val token =
                                    db.collection("TokenId").document(shopId.toString()).get()
                                        .await().toObject(TokenId::class.java)
//                            val title = "New Order from ${message.Name}"
//                            val message = "You received an order of rupees ${message.amount+message.deliveryCharges}"
                                val title = "New Order from ${message.name}"
                                val Message =
                                    "You received an order of rupees ${(message.amount) + (message.deliveryCharges)}"
                                val recipientToken = token!!.TokenId
                                if (title.isNotEmpty() && Message.isNotEmpty() && recipientToken.isNotEmpty()) {
                                    PushNotification(
                                        NotificationData(title, Message),
                                        recipientToken
                                    ).also {
                                        sendNotification(it)
                                        startActivity(intent)
                                        finish()
                                    }
                                }

                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Check Your Internet Connection",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    progress_bar.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
            }
        }
        cancel.setOnClickListener {
            finish()
        }
    }

    fun notification() {
        val intent = Intent(applicationContext, orderList::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val builder = NotificationCompat.Builder(applicationContext, "CHANNEL_ID")
            .setSmallIcon(R.drawable.snacks)
            .setContentTitle("Ordered Sucessfully")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "getString(0)"
            val descriptionText = "hbdkje"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    @Throws(InterruptedException::class, IOException::class)
    fun isConnected(): Boolean {
        val command = "ping -c 1 google.com"
        return Runtime.getRuntime().exec(command).waitFor() == 0
    }


    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.d("TAG", "Response: ${Gson().toJson(response)}")
                } else {
                    Log.e("TAG", response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e("TAG", e.toString())
            }
        }
}