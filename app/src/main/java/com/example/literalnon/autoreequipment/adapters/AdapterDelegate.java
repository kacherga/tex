package com.example.literalnon.autoreequipment.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

public interface AdapterDelegate<T> {

	/**
	 * Called to determine whether this AdapterDelegate is the responsible for the given data
	 * element.
	 *
	 * @param items    The data source of the Adapter
	 * @param position The position in the datasource
	 * @return true, if this item is responsible, otherwise false
	 */
	boolean isForViewType(@NonNull List<T> items, int position);

	/**
	 * Creates the {@link RecyclerView.ViewHolder} for the given data source item
	 *
	 * @param parent The ViewGroup parent of the given datasource
	 * @return The new instantiated {@link RecyclerView.ViewHolder}
	 */
	@NonNull
	RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent);

	/**
	 * Called to bind the {@link RecyclerView.ViewHolder} to the item of the datas source set
	 *
	 * @param holder   The {@link RecyclerView.ViewHolder} to bind
	 * @param items    The data source
	 * @param position The position in the datasource
	 */
	void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @NonNull List<T> items, int position);

}