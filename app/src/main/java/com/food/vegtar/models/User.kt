package com.food.vegtar.models

data class User(val uid :String = "",
                val displayName:String?="",
                val imageUrl:String?= " ",
                val phone:String? = "**********",
                val houseNo:String? = "",
                val landmark:String?="")