package com.food.testing.Dao

import com.food.testing.models.Message
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
    var userCollection2 = db.collection("users")

    fun addMessage(message: Message?,ownerId:String){
        message?.let {
            GlobalScope.launch(Dispatchers.IO) {
//                userCollection.document(message.key).set(it)
//                userCollection.document("Xr5uHwRLoalMzJAR3HyK").collection("Message").document(id).set(message)
                userCollection.document(ownerId).collection("Message").add(message)
            }
        }
    }

    fun getUserById(uId: String,ownerId: String): Task<DocumentSnapshot> {
        return userCollection2.document(ownerId).collection("Message").document(uId).get()
    }

    fun addMessageInUser(uId: String,message: Message?,id:String){
        message?.let {
            GlobalScope.launch(Dispatchers.IO) {
                userCollection2.document(uId).collection("Message").document(id).set(message)
            }
        }
    }
}