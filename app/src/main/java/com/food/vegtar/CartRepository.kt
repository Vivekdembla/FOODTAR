package com.food.vegtar

import androidx.lifecycle.LiveData
import com.food.vegtar.Dao.OrderDao
import com.food.vegtar.models.OrderDetail

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