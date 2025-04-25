package com.spiralworks.napachat.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun NetworkImage(url: String?, contentDescription: String?, modifier: Modifier = Modifier) {
    url?.let {
        val resource = asyncPainterResource(it)

        KamelImage(
            { resource }, contentDescription = contentDescription ?: "",
            modifier = modifier
        )
    }
}