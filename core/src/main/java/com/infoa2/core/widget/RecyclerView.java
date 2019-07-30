package com.infoa2.core.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import com.infoa2.core.R;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;

/**
 * Created by caio on 23/05/17.
 */

public class RecyclerView extends FrameLayout {

    private View noRowView;
    private androidx.recyclerview.widget.RecyclerView recyclerView;
    private SwipyRefreshLayout swipyRefreshLayout;
    private int inputId;

    public RecyclerView(Context context) {
        super(context);
    }

    public RecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupAttribute(context, attrs);
    }

    public RecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupAttribute(context, attrs);

    }


    //Setup dos atributos do Layout xml
    private void setupAttribute(Context context, AttributeSet attrs) {
        noRowView = LayoutInflater.from(context).inflate(R.layout.recycler_no_row, null, false);
        addView(noRowView);
        hasRows(true);

        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RSRecyclerView,
                0, 0);
        recyclerView = new androidx.recyclerview.widget.RecyclerView(context, attrs);

        try {
            inputId = a.getResourceId(R.styleable.RSRecyclerView_sou_rvInputId, 0);
            recyclerView.setId(inputId);
        } finally {
            a.recycle();
        }
        swipyRefreshLayout = new SwipyRefreshLayout(context, attrs);
        swipyRefreshLayout.addView(recyclerView);
        addView(swipyRefreshLayout);

    }

    public void setAdapter(androidx.recyclerview.widget.RecyclerView.Adapter adapter){
        recyclerView.setAdapter(adapter);
    }

    public androidx.recyclerview.widget.RecyclerView.Adapter getAdapter(){
        return recyclerView.getAdapter();
    }

    public void setLayoutManager(androidx.recyclerview.widget.RecyclerView.LayoutManager layout){
        recyclerView.setLayoutManager(layout);

    }

    public void addItemDecoration(androidx.recyclerview.widget.RecyclerView.ItemDecoration decoration){
        recyclerView.addItemDecoration(decoration);
    }

    public androidx.recyclerview.widget.RecyclerView getRecyclerView(){
        return this.recyclerView;
    }

    public void hasRows(boolean hasRows){
        noRowView.setVisibility(hasRows ? View.GONE : View.VISIBLE);

    }

    public void setOnRefreshListener(SwipyRefreshLayout.OnRefreshListener onRefreshListener){
        swipyRefreshLayout.setOnRefreshListener(onRefreshListener);
    }

    public void setRefreshing(boolean refreshing){
        swipyRefreshLayout.setRefreshing(refreshing);
    }

    public boolean isRefreshing(){
        return swipyRefreshLayout.isRefreshing();
    }

}
