package com.food.testing

import androidx.lifecycle.LiveData
import com.food.testing.Dao.OrderDao
import com.food.testing.models.OrderDetail

class CartRepository(private val orderDoa:OrderDao) {

    val allcart:LiveData<List<OrderDetail>> = orderDoa.getAllItems()
    suspend fun insert(orderDetail:OrderDetail){
        orderDoa.insert(orderDetail)
    }
    suspend fun delete(orderDetail: OrderDetail){
        orderDoa.delete(orderDetail)
    }
    suspend fun update(orderDetail: OrderDetail){
        orderDoa.update(orderDetail)
    }
    suspend fun deleteAll(){
        orderDoa.deleteAll()
    }

}