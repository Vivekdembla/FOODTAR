package com.food.testing.models

data class Shop(var NameOfShop:String? = "",
                var OpeningTime:Double?=0.0,
                var ClosingTime:Double?=0.0,
                var Price:ArrayList<String>? = ArrayList(),
                var ListOfFood:ArrayList<String>? =ArrayList(),
                var FoodImageUrl:ArrayList<String>? =ArrayList<String>(),
                var Description:ArrayList<String>? =ArrayList<String>(),
                var shopImage:String? = "https://www.gstatic.com/mobilesdk/160503_mobilesdk/logo/2x/firebase_28dp.png",
                var Address:String? = "Taraori",
                var ShopDescription:String? = "Fast Food",
                var Minimum:Int?=0,
                var Delivery:Int?=0,
                var phone:String="1000000000")