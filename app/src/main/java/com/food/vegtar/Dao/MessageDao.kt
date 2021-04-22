package com.food.vegtar.Dao

import com.food.vegtar.models.Message
import com.food.vegtar.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MessageDao {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
//    private var userCollection = db.collection("Message")
    private var userCollection = db.collection("Shop")

    fun addMessage(message: Message?){
        message?.let {
            GlobalScope.launch(Dispatchers.IO) {
//                userCollection.document(message.key).set(it)
                userCollection.document("Xr5uHwRLoalMzJAR3HyK").collection("Message").add(message)
            }
        }
    }

    fun getUserById(uId: String): Task<DocumentSnapshot> {
        return userCollection.document(uId).get()
    }
}