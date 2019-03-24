package io.philippeboisney.detail


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import io.philippeboisney.common.base.BaseFragment
import io.philippeboisney.common.base.BaseViewModel
import io.philippeboisney.common.extension.setupSnackbar
import io.philippeboisney.detail.databinding.FragmentDetailBinding
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 *
 */
class DetailFragment : BaseFragment() {

    // FOR DATA
    private val viewModel: DetailViewModel by viewModel()
    private lateinit var binding: FragmentDetailBinding
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.loadDataWhenActivityStarts(args.login)
    }

    override fun getViewModel(): BaseViewModel = viewModel
}
