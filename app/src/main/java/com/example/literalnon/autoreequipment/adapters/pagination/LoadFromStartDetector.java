package com.example.literalnon.autoreequipment.adapters.pagination;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

final class LoadFromStartDetector extends LoadDetector {
	private static final int HEADER_POSITION = 0;

	LoadFromStartDetector(int itemThreshold) {
		super(itemThreshold);
	}

	private static final String TAG = "LoadFromStartDetector";

	@Override
	public void enableProgressItem(boolean isEnable) {
		if (isEnable) {
			adapter.add(progressItem, HEADER_POSITION);
		} else {
			adapter.remove(HEADER_POSITION);
		}
	}

	@Override
	public RecyclerView.OnScrollListener getScrollListener() {
		return new RecyclerView.OnScrollListener() {
			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				if (dy >= 0) {
					//scroll down, all visible items already loaded
					return;
				}

				final int firstVisibleItem = findFirstVisibleItemPosition(layoutManager);
				if (!isLoading() && firstVisibleItem <= getItemThreshold()) {
					adapter.loadItems(adapter.getItemCount());
				}
			}
		};
	}

	private int findFirstVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
		if (layoutManager instanceof LinearLayoutManager) {
			return ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
		} else {
			int[] pos = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
			// TODO: 09.03.16 refactor this to find min
			return pos[0];
		}
	}
}
