package io.philippeboisney.common.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import io.philippeboisney.common.extension.setupSnackbar
import io.philippeboisney.common.navigation.NavigationCommand
import io.philippeboisney.common.utils.Event

abstract class BaseFragment: Fragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeNavigation(getViewModel())
        view?.setupSnackbar(this, getViewModel().getSnackBarError(), Snackbar.LENGTH_LONG)
    }

    abstract fun getViewModel(): BaseViewModel

    // UTILS METHODS ---

    /**
     * TODO:
     */
    private fun observeNavigation(viewModel: BaseViewModel) {
        viewModel.getNavigationCommands().observe(this, Observer {
            it?.getContentIfNotHandled()?.let { command ->
                when (command) {
                    is NavigationCommand.To -> findNavController().navigate(command.directions)
                }
            }
        })
    }
}