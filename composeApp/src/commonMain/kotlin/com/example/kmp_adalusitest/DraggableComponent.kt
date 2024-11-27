import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun DraggableComponent() {
    // Component dimensions
    val screenWidth = 78.dp
    val horizontalDragComponentWidth = 65.dp
    val horizontalDragComponentHeight = 300.dp
    val verticalDragComponentWidth = 40.dp
    val verticalDragComponentHeight = 35.dp

    // Offsets for animations
    var horizontalDragOffsetX by remember { mutableStateOf((-65).dp) }
    var verticalDragOffsetY by remember { mutableStateOf(16.dp) }

    // Smooth animation states (spring animation for overscroll effect)
    val animatedHorizontalDragOffsetX by animateDpAsState(
        targetValue = horizontalDragOffsetX,
        animationSpec = spring(
            dampingRatio = 0.5f,
            stiffness = 350f
        )
    )
    val animatedVerticalDragOffsetY by animateDpAsState(
        targetValue = verticalDragOffsetY
    )

    // Dragging states
    var dragging by remember { mutableStateOf(false) }
    var horizontalDragEnabled by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(top = 32.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        // Horizontal drag component (the background box)
        Box(
            modifier = Modifier
                .offset(x = animatedHorizontalDragOffsetX)
                .size(
                    width = horizontalDragComponentWidth,
                    height = horizontalDragComponentHeight
                )
                .background(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(48.dp)
                    .border(
                        width = 2.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .background(
                        Color.Black.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(10.dp)
                    )
            )
        }

        // Vertical drag component (the smaller box)
        Box(
            modifier = Modifier
                .offset(
                    x = animatedHorizontalDragOffsetX,
                    y = animatedVerticalDragOffsetY
                )
                .size(width = verticalDragComponentWidth, height = verticalDragComponentHeight)
                .background(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                )
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            dragging = true
                        },
                        onDragEnd = {
                            dragging = false
                            horizontalDragEnabled =
                                true // Re-enable horizontal dragging after drag ends
                        }
                    ) { change, dragAmount ->
                        // Determine drag direction
                        if (horizontalDragEnabled && kotlin.math.abs(dragAmount.y) > kotlin.math.abs(
                                dragAmount.x
                            )
                        ) {
                            horizontalDragEnabled =
                                false // Disable horizontal dragging when vertical drag starts
                        }

                        if (horizontalDragEnabled) {
                            // Handle horizontal dragging
                            val newHorizontalOffset = horizontalDragOffsetX + dragAmount.x.dp
                            horizontalDragOffsetX = when {
                                newHorizontalOffset > screenWidth - horizontalDragComponentWidth -> {
                                    (screenWidth - horizontalDragComponentWidth) +
                                            (newHorizontalOffset - (screenWidth - horizontalDragComponentWidth)) / 3
                                }

                                newHorizontalOffset < (-65).dp -> {
                                    (-65).dp
                                }

                                else -> newHorizontalOffset
                            }
                        }

                        // Handle vertical dragging
                        val newVerticalOffset = verticalDragOffsetY + dragAmount.y.dp
                        verticalDragOffsetY = newVerticalOffset.coerceIn(
                            16.dp,
                            (horizontalDragComponentHeight - verticalDragComponentHeight) - 16.dp
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Edit,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    LaunchedEffect(dragging) {
        if (!dragging && horizontalDragOffsetX > (screenWidth - horizontalDragComponentWidth)) {
            horizontalDragOffsetX = screenWidth - horizontalDragComponentWidth
        }
    }
}
