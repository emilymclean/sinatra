// TODO replace with real once bottom sheet is fixed
package com.arkivanov.decompose.extensions.compose.stack

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.stack.ChildStack

@Composable
fun <C : Any, T : Any> Children(
    stack: ChildStack<C, T>,
    modifier: Modifier = Modifier,
    content: @Composable (child: Child.Created<C, T>) -> Unit,
) {
    content(stack.active)
}