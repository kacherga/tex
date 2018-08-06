package com.example.literalnon.autoreequipment.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

public abstract class AbstractAdapterDelegate<I extends T, T, VH extends RecyclerView.ViewHolder> implements AdapterDelegate<T> {

	@Override
	public final boolean isForViewType(@NonNull List<T> items, int position) {
		return isForViewType(items.get(position), items, position);
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @NonNull List<T> items, int position) {
		onBindViewHolder((VH) holder, (I) items.get(position));
		// May be replace to it
		onBindViewHolder((VH) holder, (I) items.get(position), items, position);
	}

	/**
	 * Called to determine whether this AdapterDelegate is the responsible for the given item in the list or not
	 * element
	 *
	 * @param item     The item from the list at the given position
	 * @param items    The items from adapters dataset
	 * @param position The items position in the dataset (list)
	 * @return true if this AdapterDelegate is responsible for that, otherwise false
	 */
	protected abstract boolean isForViewType(@NonNull T item, @NonNull List<T> items, int position);

	/**
	 * Creates the  {@link RecyclerView.ViewHolder} for the given data source item
	 *
	 * @param parent The ViewGroup parent of the given datasource
	 * @return ViewHolder
	 */
	@NonNull
	@Override
	public abstract VH onCreateViewHolder(@NonNull ViewGroup parent);

	/**
	 * Called to bind the {@link RecyclerView.ViewHolder} to the item of the dataset
	 *
	 * @param holder The ViewHolder
	 * @param item   The data item
	 */
	protected void onBindViewHolder(@NonNull VH holder, @NonNull I item) {

	}

	protected void onBindViewHolder(@NonNull VH holder, @NonNull I item, @NonNull List<T> items, int position) {

	}
}
