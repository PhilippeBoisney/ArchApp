package io.philippeboisney.detail.views

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.philippeboisney.repository.utils.Resource

object DetailBinding {

    @BindingAdapter("app:imageUrlRounded")
    @JvmStatic fun loadImageRounded(view: ImageView, url: String?) {
        Glide.with(view.context).load(url).apply(RequestOptions.circleCropTransform()).into(view)
    }

    @BindingAdapter("app:showWhenLoading")
    @JvmStatic
    fun showWhenLoading(view: SwipeRefreshLayout, status: Resource.Status?) {
        Log.d(DetailBinding::class.java.simpleName, "Status: $status")
        status?.let {
            view.isRefreshing = it == Resource.Status.LOADING
        }
    }

    @BindingAdapter("app:imageUrl")
    @JvmStatic fun loadImage(view: ImageView, url: String?) {
        Glide.with(view.context).load(url).into(view)
    }
}