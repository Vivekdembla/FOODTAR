package com.food.vegtar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.food.vegtar.Dao.MessageDao
import com.food.vegtar.Dao.userDao
import com.food.vegtar.models.Message
import com.food.vegtar.models.Shop
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase

class orderList : AppCompatActivity(), IOrderAdapter {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter : orderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)
        recyclerView = findViewById(R.id.orderList)
        setRecyclerView()
    }
    fun setRecyclerView(){
        val auth = Firebase.auth
        val uid = auth.currentUser.uid
        Log.e("Checking","Uid is $uid")
        val messageDao = MessageDao()
        val Collection=messageDao.userCollection2
//        var messageCollection = Collection.document(uid).collection("Message")
        val messageCollection = Collection.document(uid).collection("Message")
        val query = messageCollection.orderBy("key", Query.Direction.ASCENDING)
        val recyclerViewOption = FirestoreRecyclerOptions.Builder<Message>().setQuery(
                query,
                Message::class.java
        ).build()
        adapter = orderAdapter(recyclerViewOption,this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

}