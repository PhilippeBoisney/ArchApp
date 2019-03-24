package io.philippeboisney.detail.views

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.philippeboisney.repository.Resource

object DetailBinding {

    @BindingAdapter("app:imageUrl")
    @JvmStatic fun loadImage(view: ImageView, url: String?) {
        Glide.with(view.context).load(url).apply(RequestOptions.circleCropTransform()).into(view)
    }

    @BindingAdapter("app:showWhenLoading")
    @JvmStatic
    fun showWhenLoading(view: SwipeRefreshLayout, status: Resource.Status?) {
        status?.let {
            view.isRefreshing = it == Resource.Status.LOADING
        }

    }
}