package io.philippeboisney.detail

import android.os.Bundle
import android.transition.ChangeBounds
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import io.philippeboisney.common.base.BaseFragment
import io.philippeboisney.common.base.BaseViewModel
import io.philippeboisney.detail.databinding.FragmentDetailImageBinding
import io.philippeboisney.model.User
import org.koin.android.viewmodel.ext.android.viewModel


/**
 * A simple [BaseFragment] subclass
 * that will print the [User] profile picture in full screen
 */
class DetailImageFragment : BaseFragment() {

    // FOR DATA
    private lateinit var binding: FragmentDetailImageBinding
    private val args: DetailImageFragmentArgs by navArgs()
    private val viewModel: DetailImageViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        configureTransition()
        binding = FragmentDetailImageBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadDataWhenFragmentStarts(args.imageUrl)
    }

    override fun getViewModel(): BaseViewModel = viewModel

    // ---

    private fun configureTransition() {
        val transition = ChangeBounds().apply { duration = 300 }
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition
    }
}
