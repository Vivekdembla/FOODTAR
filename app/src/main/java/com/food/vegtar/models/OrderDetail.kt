package com.food.vegtar.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_table")
data class OrderDetail(
        @PrimaryKey var EachFoodName:String,
        @ColumnInfo var EachFoodPrice:String?,
        @ColumnInfo var EachFoodUrl:String?,
        @ColumnInfo var EachFoodDes:String?,
        @ColumnInfo var EachShopName:String?,
        @ColumnInfo var quantity:Int?=1
)
//{
//    @PrimaryKey(autoGenerate = true) var id = 0
//}