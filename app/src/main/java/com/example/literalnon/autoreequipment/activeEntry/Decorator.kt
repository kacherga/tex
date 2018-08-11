package com.example.literalnon.autoreequipment.activeEntry

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class SpaceItemDecoration(private val context: Context?) : RecyclerView.ItemDecoration() {

    companion object {
        val spanCount = 1
        val DISTANCE = 16f
        val distance: Int = pxFromDp(DISTANCE).toInt()

        fun pxFromDp(dp: Float): Float {
            return dp * (Resources.getSystem().displayMetrics?.density ?: 1f)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {

        val count = parent.adapter.itemCount

        if (count != 0) {
            val verticalDelimiterCount = count + 1

            //val row = parent.getChildAdapterPosition(view) % spanCount
            val collumn = parent.getChildAdapterPosition(view) / spanCount

            /*when {
                row == 1 -> {
                    outRect.left = distance * 2 / 3
                    outRect.right = distance * 2 / 3
                }
                row == 0 -> {
                    outRect.left = distance
                    outRect.right = distance / 3
                }
                row == 2 -> {
                    outRect.right = distance
                    outRect.left = distance / 3
                }
            }*/

            outRect.right = distance
            outRect.left = distance
            outRect.top = distance * (verticalDelimiterCount - collumn) / count
            outRect.bottom = distance * collumn / count
        }
    }

    fun convertPixelsToDp(px: Float): Float {
        val metrics = Resources.getSystem().getDisplayMetrics()
        val dp = px / (metrics.densityDpi / 160f)
        return Math.round(dp).toFloat()
    }

}
