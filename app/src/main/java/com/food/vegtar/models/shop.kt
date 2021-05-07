package com.food.vegtar.models

data class Shop(val NameOfShop:String? = "",
                val OpeningTime:Double?=0.0,
                val ClosingTime:Double?=0.0,
                val Price:ArrayList<String>? = ArrayList(),
                val ListOfFood:ArrayList<String>? =ArrayList(),
                val FoodImageUrl:ArrayList<String>? =ArrayList<String>(),
                val Description:ArrayList<String>? =ArrayList<String>(),
                val shopImage:String? = "https://www.gstatic.com/mobilesdk/160503_mobilesdk/logo/2x/firebase_28dp.png",
                val Address:String? = "Taraori",
                val ShopDescription:String? = "Fast Food",
                val Minimum:Int?=0,
                val Delivery:Int?=0)