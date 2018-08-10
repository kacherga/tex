package com.example.literalnon.autoreequipment.adapters;

import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

public class AdapterManager<T> {
    private final SparseArrayCompat<AdapterDelegate<T>> delegates = new SparseArrayCompat<>();

    AdapterManager() {
    }

    public AdapterManager<T> addDelegate(AdapterDelegate<T> delegate) {
        final int viewType = delegates.size();
        return addDelegate(viewType, delegate);
    }

    public AdapterManager<T> addDelegate(int viewType, @NonNull AdapterDelegate<T> delegate) {
        return addDelegate(viewType, delegate, false);
    }

    public AdapterManager<T> addDelegate(int viewType, @NonNull AdapterDelegate<T> delegate,
                                         boolean allowReplacing) {
        if (!allowReplacing && delegates.get(viewType) != null) {
            throw new IllegalArgumentException(
                    "An AdapterDelegate is already registered for the viewType = "
                            + viewType
                            + ". Already registered AdapterDelegate is "
                            + delegates.get(viewType));
        }
        delegates.put(viewType, delegate);
        return this;
    }

    public AdapterManager<T> removeDelegate(int viewType) {
        delegates.remove(viewType);
        return this;
    }

    public AdapterManager<T> removeDelegate(@NonNull AdapterDelegate<T> delegate) {
        final int index = delegates.indexOfValue(delegate);
        if (index >= 0) {
            delegates.removeAt(index);
        }
        return this;
    }

    public int getItemViewType(@NonNull List<T> items, int position) {
        for (int i = 0, size = delegates.size(); i < size; i++) {
            final AdapterDelegate<T> adapter = delegates.valueAt(i);
            if (adapter.isForViewType(items, position)) {
                return delegates.keyAt(i);
            }
        }
        throw new NullPointerException("No AdapterDelegate added that matches position=" + position
                + " in data source" + items.get(position));
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final AdapterDelegate<T> adapterDelegate = delegates.get(viewType);
        if (adapterDelegate == null) {
            throw new NullPointerException("No AdapterDelegate added for ViewType " + viewType);
        }
        final RecyclerView.ViewHolder viewHolder = adapterDelegate.onCreateViewHolder(parent);
        if (viewHolder == null) {
            throw new NullPointerException("ViewHolder returned from AdapterDelegate "
                    + adapterDelegate
                    + " for ViewType ="
                    + viewType
                    + " is null!");
        }
        return viewHolder;
    }

    public void onBindViewHolder(List<T> items, int position, RecyclerView.ViewHolder holder) {
        final AdapterDelegate<T> adapterDelegate = delegates.get(holder.getItemViewType());
        if (adapterDelegate == null) {
            throw new NullPointerException("No AdapterDelegate found for item at position = "
                    + position
                    + " for viewType = "
                    + holder.getItemViewType());
        }
        adapterDelegate.onBindViewHolder(holder, items, position);
    }

}
