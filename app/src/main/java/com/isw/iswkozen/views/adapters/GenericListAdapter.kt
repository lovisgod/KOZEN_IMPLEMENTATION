package com.isw.iswkozen.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.interswitchng.smartpos.shared.services.utils.IsoUtils
import com.isw.iswkozen.R
import com.isw.iswkozen.core.data.models.TerminalInfo
import com.isw.iswkozen.core.database.entities.TransactionResultData
import com.isw.iswkozen.core.utilities.DateUtils
import com.isw.iswkozen.core.utilities.DisplayUtils
import java.util.*

abstract class GenericListAdapter<T : Any>(
    @IdRes val layoutId: Int,
    inline val bind: (item: T, holder: BaseViewHolder, itemCount: Int) -> Unit
) : ListAdapter<T, BaseViewHolder>(BaseItemCallback<T>()) {
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bind(getItem(position), holder, itemCount)
    }

    override fun getItemViewType(position: Int) = layoutId
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val root = LayoutInflater.from(parent.context).inflate(
            viewType, parent, false
        )
        return BaseViewHolder(root as ViewGroup)
    }

    override fun getItemCount() = currentList.size

}


fun getGenericAdapter(layoutId: Int,  terminalInfo: TerminalInfo, clickListener: HistoryItemClickListener): GenericListAdapter<TransactionResultData> {
    return object : GenericListAdapter<TransactionResultData>(
        layoutId,
        bind = { item, holder, itemCount ->
            with(holder.itemView) {
                this.findViewById<TextView>(R.id.amount).text =
                    DisplayUtils.getAmountWithCurrency(item.amount, terminalInfo)
                this.findViewById<TextView>(R.id.paymentType).text = item.type.name
                var date = Date(item.txnDate)
                this.findViewById<TextView>(R.id.date).text = DateUtils.timeOfDateFormat.format(date)
                this.findViewById<TextView>(R.id.transactionType).text = item.paymentType
                val isSuccess = item.responseCode == IsoUtils.OK
                val textColor =
                    if (isSuccess) R.color.purple_700
                    else R.color.design_default_color_error

                val succeedMessage = if (isSuccess) "Success" else "${item.responseMessage}"
                this.findViewById<TextView>(R.id.status).apply {
                    text = succeedMessage
                    setTextColor(ContextCompat.getColor(this.context, textColor))
                }

                this.setOnClickListener {
                    clickListener.onItemClicked(item, terminalInfo)
                }

            }

        }
    ) {}
}

interface HistoryItemClickListener {
    fun onItemClicked(item: TransactionResultData, terminalInfo: TerminalInfo)
}