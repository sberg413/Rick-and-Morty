package com.sberg413.rickandmorty.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.sberg413.rickandmorty.ui.LocalNavAnimatedVisibilityScope
import com.sberg413.rickandmorty.ui.LocalSharedTransitionScope
import com.sberg413.rickandmorty.ui.theme.AppTheme


tailrec fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> {
        this
    }
    is ContextWrapper -> {
        baseContext.findActivity()
    }
    else -> {
        null
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RMPreviewWrapper(content: @Composable () -> Unit) {
    AppTheme {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this@SharedTransitionLayout,
                    LocalNavAnimatedVisibilityScope provides this
                ) {
                    content()
                }
            }
        }
    }
}