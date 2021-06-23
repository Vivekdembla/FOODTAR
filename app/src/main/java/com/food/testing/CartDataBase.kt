package com.food.testing

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.food.testing.Dao.OrderDao
import com.food.testing.models.OrderDetail

@Database(entities = [OrderDetail::class],version = 1,exportSchema = false)
abstract class CartDataBase:RoomDatabase() {
    abstract fun getCartDao():OrderDao

    companion object{
        @Volatile
        private var INSTANCE:CartDataBase?=null

        fun getDataBase(context: Context):CartDataBase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CartDataBase::class.java,
                    "cart_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}