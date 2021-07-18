package com.food.vegtar.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.food.vegtar.models.OrderDetail


@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orderDetail: OrderDetail)

    @Delete
    suspend fun delete(orderDetail: OrderDetail)

    @Query("Select * from cart_table order by EachFoodName ASC")
    fun getAllItems(): LiveData<List<OrderDetail>>

    @Query("DELETE FROM cart_table")
    suspend fun deleteAll()

    @Update
    suspend fun update(orderDetail: OrderDetail)

    @Query("SELECT EXISTS (SELECT 1 FROM cart_table WHERE EachFoodName = :name)")
    fun exists(name: String): Boolean
}