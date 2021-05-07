package com.food.vegtar.models

data class Message(val key:String?="",
                   val message:String?="",
                   val Name:String?="",
                   val address:String?="",
                   val timeOfMessage:String?="",
                   val status:String?="",
                   val image:String="",
                   val amount:Int=200,
                   val shopname:String="",
                   val deliveryCharges:Int=0)