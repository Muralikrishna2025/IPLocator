package uk.ac.tees.mad.iplocator.ui.utils

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

/**
 * Applies a shimmer effect to the composable it's applied to.
 *
 * This modifier creates a shimmering, gradient animation that moves across the
 * composable's background. It's commonly used as a placeholder effect to indicate
 * that content is loading or being processed.
 *
 * The effect consists of a linear gradient with three shades of gray,
 * creating the illusion of a moving highlight. The animation is infinite and
 * repeats smoothly.
 *
 * @return A [Modifier] that applies the shimmer effect to the composable.
 *
 * Example Usage:
 * ```
 * Box(modifier = Modifier
 *      .fillMaxWidth()
 *      .height(100.dp)
 *      .shimmerEffect()
 * )
 * ```
 * In this example, a Box will have the shimmer effect applied to its background.
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition()
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000)
        )
    )
    background(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFB8B5B5), Color(0xFF8F8B8B), Color(0xFFB8B5B5)
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    ).onGloballyPositioned { size = it.size }
}