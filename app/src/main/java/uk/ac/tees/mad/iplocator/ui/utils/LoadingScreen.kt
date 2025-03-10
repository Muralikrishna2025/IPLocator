package uk.ac.tees.mad.iplocator.ui.utils

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Composable function to display a loading screen with an animated indicator.
 *
 * @param modifier Modifier for the layout.
 */
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Animated loading indicator
            AnimatedLoadingIndicator(
                modifier = Modifier.size(100.dp), color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Loading message
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * Composable function to display an animated loading indicator.
 *
 * @param modifier Modifier for the layout.
 * @param color Color of the loading indicator.
 */
@Composable
fun AnimatedLoadingIndicator(
    modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.primary
) {
    // Constants for animation and drawing
    val animationDuration = 1000 // Duration of one rotation in milliseconds
    val sweepAngle = 270f // Sweep angle of the arc

    // Create an infinite transition for the rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
    // Animate the rotation value from 0 to 360 degrees infinitely
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        // `infiniteRepeatable` is used to make the animation repeat infinitely.
        animationSpec = infiniteRepeatable(
            // `tween` is used to create a smooth animation.
            animation = tween(durationMillis = animationDuration)
        ), label = "rotation"
    )

    // Draw the loading indicator on a Canvas
    Canvas(modifier = modifier) {
        drawLoadingIndicator(rotation, color, sweepAngle)
    }
}

/**
 * Draws the loading indicator on the canvas.
 *
 * @param rotation The current rotation angle of the indicator.
 * @param color The color of the indicator.
 * @param sweepAngle The sweep angle of the arc.
 */
private fun DrawScope.drawLoadingIndicator(rotation: Float, color: Color, sweepAngle: Float) {
    val canvasWidth = size.width
    val canvasHeight = size.height
    // Calculate the stroke width based on the canvas width.
    val strokeWidth = canvasWidth / 10
    // Calculate the radius of the arc.
    val radius = (canvasWidth - strokeWidth) / 2
    // Calculate the center of the canvas.
    val center = Offset(canvasWidth / 2, canvasHeight / 2)
    // Calculate the start angle of the arc.
    val startAngle = -90f + rotation
    // Calculate the top left offset of the arc.
    val topLeft = Offset(center.x - radius, center.y - radius)
    // Calculate the size of the arc.
    val size = Size(radius * 2, radius * 2)

    // `drawArc` is used to draw the arc.
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = topLeft,
        size = size,
        // `Stroke` is used to draw the arc with a stroke.
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
}