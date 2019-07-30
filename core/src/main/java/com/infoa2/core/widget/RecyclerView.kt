package com.infoa2.core.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.infoa2.core.R
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout

/**
 * Created by caio on 23/05/17.
 */

class RecyclerView : FrameLayout {

    private var noRowView: View? = null
    var recyclerView: androidx.recyclerview.widget.RecyclerView? = null
        private set
    private var swipyRefreshLayout: SwipyRefreshLayout? = null
    private var inputId: Int = 0

    var adapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>?
        get() = recyclerView!!.adapter
        set(adapter) {
            recyclerView!!.adapter = adapter
        }

    var isRefreshing: Boolean
        get() = swipyRefreshLayout!!.isRefreshing
        set(refreshing) {
            swipyRefreshLayout!!.isRefreshing = refreshing
        }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setupAttribute(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        setupAttribute(context, attrs)

    }


    //Setup dos atributos do Layout xml
    private fun setupAttribute(context: Context, attrs: AttributeSet?) {
        noRowView = LayoutInflater.from(context).inflate(R.layout.recycler_no_row, null, false)
        addView(noRowView)
        hasRows(true)

        val a = getContext().theme.obtainStyledAttributes(
            attrs,
            R.styleable.RSRecyclerView,
            0, 0
        )
        recyclerView = androidx.recyclerview.widget.RecyclerView(context, attrs)

        try {
            inputId = a.getResourceId(R.styleable.RSRecyclerView_sou_rvInputId, 0)
            recyclerView!!.id = inputId
        } finally {
            a.recycle()
        }
        swipyRefreshLayout = SwipyRefreshLayout(context, attrs)
        swipyRefreshLayout!!.addView(recyclerView)
        addView(swipyRefreshLayout)

    }

    fun setLayoutManager(layout: androidx.recyclerview.widget.RecyclerView.LayoutManager) {
        recyclerView!!.layoutManager = layout

    }

    fun addItemDecoration(decoration: androidx.recyclerview.widget.RecyclerView.ItemDecoration) {
        recyclerView!!.addItemDecoration(decoration)
    }

    fun hasRows(hasRows: Boolean) {
        noRowView!!.visibility = if (hasRows) View.GONE else View.VISIBLE

    }

    fun setOnRefreshListener(onRefreshListener: SwipyRefreshLayout.OnRefreshListener) {
        swipyRefreshLayout!!.setOnRefreshListener(onRefreshListener)
    }

}
