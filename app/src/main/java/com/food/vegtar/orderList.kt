package com.food.vegtar

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.food.vegtar.Dao.MessageDao
import com.food.vegtar.models.Message
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class orderList : AppCompatActivity(), IOrderAdapter {
    lateinit var progressBar: ProgressBar
    lateinit var recyclerView: RecyclerView
    lateinit var adapter : orderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        setContentView(R.layout.activity_order_list)
        recyclerView = findViewById(R.id.orderList)
        progressBar = findViewById(R.id.progressBar4)
        progressBar.visibility = View.VISIBLE
        setRecyclerView()
    }
    fun setRecyclerView(){
        val auth = Firebase.auth
        val uid = auth.currentUser.uid
        Log.e("Checking","Uid is $uid")
        val messageDao = MessageDao()
        val Collection=messageDao.userCollection2
        val messageCollection = Collection.document(uid).collection("Message")
        val query = messageCollection.orderBy("key", Query.Direction.DESCENDING)
        val recyclerViewOption = FirestoreRecyclerOptions.Builder<Message>().setQuery(
                query,
                Message::class.java
        ).build()
        adapter = orderAdapter(recyclerViewOption,this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        progressBar.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onOrderClicked(orderid: String) {
        val intent = Intent(this,orderedActivity::class.java)
        val messageDao = MessageDao()
        var uid = Firebase.auth.currentUser.uid
        GlobalScope.launch(Dispatchers.IO) {
            val message = messageDao.getUserById(orderid,uid).await().toObject(Message::class.java)
            intent.putExtra("ShopName",message?.name)
            intent.putExtra("Amount",message?.amount)
            intent.putExtra("Delivery",message?.deliveryCharges)
            intent.putExtra("Status",message?.status)
            intent.putExtra("Address",message?.address)
            intent.putExtra("Message",message?.message)
            startActivity(intent)
        }
    }
}