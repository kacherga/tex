package com.example.literalnon.autoreequipment.adapters.pagination;

import android.support.v7.widget.RecyclerView;

abstract class LoadDetector {
	static final int DEFAULT_ITEM_THRESHOLD = 5;

	private volatile boolean isLoading;
	private final int itemThreshold;

	protected RecyclerView.LayoutManager layoutManager;
	protected PaginationAdapter adapter;
	private RecyclerView.OnScrollListener scrollListener;
	protected final PaginationAdapter.ProgressBarAdapter.Progress progressItem
			= new PaginationAdapter.ProgressBarAdapter.Progress();

	LoadDetector(int itemThreshold) {
		this.itemThreshold = itemThreshold;
	}

	void onAttachedToRecyclerView(RecyclerView recyclerView, PaginationAdapter paginationAdapter) {
		this.layoutManager = recyclerView.getLayoutManager();
		// TODO: 09.03.16 maybe create LoadManager - and pass into Loader and LoadDetector
		this.adapter = paginationAdapter;
		scrollListener = getScrollListener();
		recyclerView.addOnScrollListener(scrollListener);
	}

	void onDetachedFromRecyclerView(RecyclerView recyclerView) {
		recyclerView.removeOnScrollListener(scrollListener);
		this.scrollListener = null;
		this.layoutManager = null;
		this.adapter = null;
	}

	public abstract void enableProgressItem(boolean isEnable);

	public abstract RecyclerView.OnScrollListener getScrollListener();

	final void setLoadingState(boolean isLoading) {
		//Log.d("TAG", "isLoading " + isLoading);
		this.isLoading = isLoading;
		enableProgressItem(isLoading);
	}

	public final boolean isLoading() {
		return isLoading;
	}

	int getItemThreshold() {
		return itemThreshold;
	}

	/**
	 * Check if items fit screen
	 *
	 * @param loadedItemsCount loaded items count
	 * @return <tt>true</tt>, if items don't fit screen and need downloading, <tt>false</tt> - otherwise
	 */
	boolean isItemsNotFitScreen(int loadedItemsCount) {
		final int childCount = layoutManager.getChildCount();
		return childCount == 0 || adapter.getItemCount() - loadedItemsCount <= childCount;
	}
}
