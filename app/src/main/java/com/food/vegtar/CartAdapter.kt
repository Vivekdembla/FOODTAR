package com.food.vegtar

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.food.vegtar.models.OrderDetail
import kotlinx.android.synthetic.main.cartlist.view.*
import java.math.RoundingMode
import java.text.DecimalFormat


class CartAdapter(val listener: ICartAdapter,val delivery:Int):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var cartItem = ArrayList<OrderDetail>()
    lateinit var view: View
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.bill, parent, false)
            return BillViewHolder(view)
        }
        view = LayoutInflater.from(parent.context).inflate(R.layout.cartlist, parent, false)
        val viewHolder = CartViewHolder(view)
        val quantity = viewHolder.quantity
        viewHolder.add.setOnClickListener {
            var num: Int = Integer.valueOf(quantity.text.toString())
            num++
            quantity.setText(num.toString())
            listener.onAddClick(viewHolder.adapterPosition, num, view)
        }
        viewHolder.minus.setOnClickListener {
            var num: Int = Integer.valueOf(quantity.text.toString())
            num--
            quantity.setText(num.toString())
            listener.onMinusClick(viewHolder.adapterPosition, num, view)
        }

        viewHolder.quantity.setOnEditorActionListener(TextView.OnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                if(viewHolder.quantity.text.toString()!="")
                listener.onQuantityClick(viewHolder.adapterPosition, viewHolder.quantity.text.toString(), view)
                return@OnEditorActionListener true
            }
            false
        })

        return viewHolder
    }

    override fun getItemViewType(position: Int): Int {
        if(position==cartItem.size){
            return 0
        }
        else
            return 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(position < cartItem.size) {
            var newPrice: Double = cartItem[position].EachFoodPrice!!.toDouble()

            newPrice *= cartItem[position].quantity!!
            val df = DecimalFormat("#.###")
            df.roundingMode = RoundingMode.CEILING

            newPrice = df.format(newPrice).toDouble()
            Log.e("Checking", "BindViewHolder")
            val holder1:CartViewHolder = holder as CartViewHolder
            Glide.with(holder1.itemImage.context).
            load(cartItem[position].EachFoodUrl).
            apply(RequestOptions.circleCropTransform()).
            into(holder1.itemImage)
            holder1.resname.text = cartItem[position].EachFoodName.toString()
            holder1.price.text = "₹${newPrice}"
            holder1.ordername.text = cartItem[position].EachShopName.toString()
            holder1.quantity.setText(cartItem[position].quantity.toString())
        }
        else{

            val holder2:BillViewHolder = holder as BillViewHolder

            var a=0.0
            for( i in 0..cartItem.size-1){
                a += (cartItem[i].EachFoodPrice!!.toDouble())*cartItem[i].quantity!!
            }
            val df = DecimalFormat("#.###")
            df.roundingMode = RoundingMode.CEILING

            a = df.format(a).toDouble()

            holder2.bill.text="₹${a}"
            holder2.total.text = "₹${a+delivery}"
            holder2.deliveryCharges.text = "₹$delivery"
        }
    }

    override fun getItemCount(): Int {
        return cartItem.size+1
    }

    fun updateList(list: List<OrderDetail>){
        cartItem.clear()
        cartItem.addAll(list)
        notifyDataSetChanged()
    }
}

class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val resname = itemView.findViewById<TextView>(R.id.resname)
    val ordername = itemView.findViewById<TextView>(R.id.ordername)
    val price = itemView.findViewById<TextView>(R.id.price1)
    val itemImage = itemView.findViewById<ImageView>(R.id.itemImage)
    val add = itemView.findViewById<Button>(R.id.add)
    val minus = itemView.findViewById<Button>(R.id.subtract)
    val quantity = itemView.findViewById<EditText>(R.id.quantity)
}
class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val bill = itemView.findViewById<TextView>(R.id.bill)
    val deliveryCharges = itemView.findViewById<TextView>(R.id.deliveryCharges)
    val total = itemView.findViewById<TextView>(R.id.total)
}

interface ICartAdapter{
    fun onAddClick(position: Int, value: Int, view: View){}
    fun onMinusClick(position: Int, value: Int,view: View){}
    fun onQuantityClick(position: Int, value: String,view: View)
}