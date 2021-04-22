package com.food.vegtar.Dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.food.vegtar.models.OrderDetail


@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(orderDetail: OrderDetail)

    @Delete
    suspend fun delete(orderDetail: OrderDetail)

    @Query("Select * from cart_table order by id ASC")
    fun getAllItems(): LiveData<List<OrderDetail>>

    @Query("DELETE FROM cart_table")
    suspend fun deleteAll()

    @Update
    suspend fun update(orderDetail: OrderDetail)
}