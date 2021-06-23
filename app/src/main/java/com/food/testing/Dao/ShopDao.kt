package com.food.testing.Dao

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ShopDao {
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var shopCollection = db.collection("Shop")

    fun getFoodById(uId: String): Task<DocumentSnapshot> {
        return shopCollection.document(uId).get()
    }
}