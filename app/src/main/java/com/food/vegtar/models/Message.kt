package com.food.vegtar.models

data class Message(val key:String,
                   val message:String,
                   val Name:String,
                   val Address:String,
                   val TimeOfMessage:String,
                   val status:String,
                   val ownerId:String,
                   val amount:Int=200,
                   val image:String="",
                   val shopname:String="")