package io.philippeboisney.common.navigation

import androidx.navigation.NavDirections

/**
 * TODO:
 */
sealed class NavigationCommand {
    data class To(val directions: NavDirections): NavigationCommand()
    object Back: NavigationCommand()
    data class BackTo(val destinationId: Int): NavigationCommand()
    object ToRoot: NavigationCommand()
}