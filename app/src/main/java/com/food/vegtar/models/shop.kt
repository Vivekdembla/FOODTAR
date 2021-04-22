package com.food.vegtar.models

data class Shop(val NameOfShop:String? = "",
                val OpeningTime:Int?=0,
                val ClosingTime:Int?=0,
                val Price:ArrayList<String>? = ArrayList(),
                val ListOfFood:ArrayList<String>? =ArrayList(),
                val FoodImageUrl:ArrayList<String>? =ArrayList<String>(),
                val Description:ArrayList<String>? =ArrayList<String>(),
                val shopImage:String? = "https://www.gstatic.com/mobilesdk/160503_mobilesdk/logo/2x/firebase_28dp.png")