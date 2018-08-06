package com.example.literalnon.autoreequipment.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public class DelegationAdapter<T> extends BaseAdapter<T, RecyclerView.ViewHolder> {
	private final AdapterManager<T> manager = new AdapterManager<>();

	public AdapterManager<T> getManager() {
		return manager;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return manager.onCreateViewHolder(parent, viewType);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (position < getItemCount()) {
			manager.onBindViewHolder(items, position, holder);
		}
	}

	@Override
	public int getItemViewType(int position) {
		return manager.getItemViewType(items, position);
	}

}
