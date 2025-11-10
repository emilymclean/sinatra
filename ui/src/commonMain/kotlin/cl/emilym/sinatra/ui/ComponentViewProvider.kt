package cl.emilym.sinatra.ui

import androidx.compose.runtime.Composable

interface ComponentViewProvider<T> {

    @Composable
    fun Content(component: T)

}