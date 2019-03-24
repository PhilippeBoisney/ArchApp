package io.philippeboisney.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.philippeboisney.common.base.BaseFragment
import io.philippeboisney.common.base.BaseViewModel
import io.philippeboisney.home.databinding.FragmentHomeBinding
import io.philippeboisney.home.views.HomeAdapter
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass. *
 */
class HomeFragment : BaseFragment() {

    // FOR DATA
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        configureRecyclerView()
    }

    override fun getViewModel(): BaseViewModel = viewModel

    // ---

    private fun configureRecyclerView() {
        binding.fragmentHomeRv.adapter = HomeAdapter(viewModel)
    }
}
