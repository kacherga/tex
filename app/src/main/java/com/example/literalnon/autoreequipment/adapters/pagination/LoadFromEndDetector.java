package com.example.literalnon.autoreequipment.adapters.pagination;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

class LoadFromEndDetector extends LoadDetector {

	LoadFromEndDetector(int itemThreshold) {
		super(itemThreshold);
	}

	private final String TAG = "LoadFromEndDetector";

	@Override
	public void enableProgressItem(boolean isEnable) {
		if (isEnable) {
			adapter.add(progressItem);
		} else {
			adapter.remove(adapter.getItemCount() - 1);
		}
	}

	@Override
	public RecyclerView.OnScrollListener getScrollListener() {
		return new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				if (dy <= 0) {
					//scroll up, all visible items already loaded
					return;
				}

				final int loadedItemsCount = adapter.getItemCount();
				final int lastVisibleItem = findLastVisibleItemPosition(layoutManager);

				Log.d(TAG, "scroll " + loadedItemsCount + " : " + lastVisibleItem + " : " +isLoading() + " : " + getItemThreshold());
				if (!isLoading() && loadedItemsCount <= (lastVisibleItem + getItemThreshold())) {
					Log.d(TAG, "scroll true" + loadedItemsCount);
					adapter.loadItems(loadedItemsCount);
				}
			}
		};
	}

	// TODO: 11.09.15 refactor this
	private int findLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
		if (layoutManager instanceof LinearLayoutManager) {
			return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
		} else {
			int[] pos = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
			// TODO: 09.03.16 refactor this to find max
			return pos[pos.length - 1];
		}
	}
}

class NestedLoadDetector extends LoadFromEndDetector {

	NestedLoadDetector(int itemThreshold) {
		super(itemThreshold);
	}

	protected
	void onAttachedToRecyclerView(RecyclerView recyclerView, PaginationAdapter paginationAdapter) {
		this.layoutManager = recyclerView.getLayoutManager();
		// TODO: 09.03.16 maybe create LoadManager - and pass into Loader and LoadDetector
		this.adapter = paginationAdapter;
	}

	/*public getScrollListener() {
		return new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				if (dy <= 0) {
					//scroll up, all visible items already loaded
					return;
				}

				final int loadedItemsCount = adapter.getItemCount();
				final int lastVisibleItem = findLastVisibleItemPosition(layoutManager);

				Log.d(TAG, "scroll " + loadedItemsCount + " : " + lastVisibleItem + " : " +isLoading() + " : " + getItemThreshold());
				if (!isLoading() && loadedItemsCount <= (lastVisibleItem + getItemThreshold())) {
					Log.d(TAG, "scroll true" + loadedItemsCount);
					adapter.loadItems(loadedItemsCount);
				}
			}
		};
	}*/

	// TODO: 11.09.15 refactor this
	private int findLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
		if (layoutManager instanceof LinearLayoutManager) {
			return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
		} else {
			int[] pos = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
			// TODO: 09.03.16 refactor this to find max
			return pos[pos.length - 1];
		}
	}
}