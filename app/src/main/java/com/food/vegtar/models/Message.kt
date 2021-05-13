package com.food.vegtar.models

data class Message(var key:String?="",
                   var message:String?="",
                   var name:String?="",
                   var address:String?="",
                   var timeOfMessage:String?="",
                   var status:String?="",
                   var image:String="",
                   var amount:Double=200.0,
                   var shopname:String="",
                   var deliveryCharges:Int=0,
                   var phone:String="",
                   var userTokenId:String="")