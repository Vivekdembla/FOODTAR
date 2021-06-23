package com.food.testing

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.food.testing.models.OrderDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartViewModel(application: Application):AndroidViewModel(application) {
    var allCart : LiveData<List<OrderDetail>>
    private val repository:CartRepository
    init {
        val dao = CartDataBase.getDataBase(application).getCartDao()
        repository = CartRepository(dao)
        allCart = repository.allcart
    }

    fun deleteCart(orderDetail: OrderDetail)=viewModelScope.launch(Dispatchers.IO){
        repository.delete(orderDetail)
    }
    fun insertCart(orderDetail: OrderDetail) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(orderDetail)
    }
    fun updateCart(orderDetail: OrderDetail) = viewModelScope.launch(Dispatchers.IO){
        repository.update(orderDetail)
    }
    fun deleteAll() = viewModelScope.launch(Dispatchers.IO){
        repository.deleteAll()
    }
}