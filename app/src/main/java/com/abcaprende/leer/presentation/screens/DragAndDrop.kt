package com.abcaprende.leer.presentation.screens

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Size // Added import
import androidx.compose.ui.unit.toSize // Added import
import androidx.compose.ui.geometry.Rect // Added import
import androidx.compose.ui.layout.positionInWindow // Added import
import kotlin.math.roundToInt

internal class DragTargetInfo {
    var isDragging: Boolean by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var dataToDrop by mutableStateOf<Any?>(null)
}

internal val LocalDragTargetInfo = compositionLocalOf { DragTargetInfo() }

@Composable
fun LongPressDraggable(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val state = remember { DragTargetInfo() }
    CompositionLocalProvider(LocalDragTargetInfo provides state) {
        Box(modifier = modifier.fillMaxSize()) {
            content()
            if (state.isDragging) {
                var targetSize by remember { mutableStateOf(IntOffset.Zero) }
                val density = LocalDensity.current
                Box(modifier = Modifier
                    .onGloballyPositioned {
                        targetSize = IntOffset(it.size.width, it.size.height)
                    }
                    .offset {
                        IntOffset(
                            (state.dragPosition.x + state.dragOffset.x).roundToInt(),
                            (state.dragPosition.y + state.dragOffset.y).roundToInt()
                        )
                    }
                    .graphicsLayer {
                        alpha = 0.8f
                    }
                ) {
                    state.draggableComposable?.invoke()
                }
            }
        }
    }
}

@Composable
fun <T> DragTarget(
    modifier: Modifier = Modifier,
    dataToDrop: T,
    content: @Composable () -> Unit
) {
    val dragTargetInfo = LocalDragTargetInfo.current
    val currentPosition = remember { mutableStateOf(Offset.Zero) }

    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition.value = it.localToWindow(Offset.Zero)
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDragStart = { offset ->
                    dragTargetInfo.dataToDrop = dataToDrop
                    dragTargetInfo.draggableComposable = content
                    dragTargetInfo.dragPosition = currentPosition.value + offset
                    dragTargetInfo.isDragging = true
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    dragTargetInfo.dragOffset += dragAmount
                },
                onDragEnd = {
                    dragTargetInfo.isDragging = false
                    dragTargetInfo.dragOffset = Offset.Zero
                },
                onDragCancel = {
                    dragTargetInfo.isDragging = false
                    dragTargetInfo.dragOffset = Offset.Zero
                }
            )
        }) {
        content()
    }
}

@Composable
fun <T> DropTarget(
    modifier: Modifier = Modifier,
    onDrop: (T) -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val dragTargetInfo = LocalDragTargetInfo.current
    val dragPosition = dragTargetInfo.dragPosition
    val dragOffset = dragTargetInfo.dragOffset
    val isDragging = dragTargetInfo.isDragging
    val dataToDrop = dragTargetInfo.dataToDrop

    var isCurrentDropTarget by remember { mutableStateOf(false) }

    Box(modifier = modifier.onGloballyPositioned { layoutCoordinates ->
        val rect = Rect(
            offset = layoutCoordinates.positionInWindow(),
            size = layoutCoordinates.size.toSize()
        )
        isCurrentDropTarget = rect.contains(dragPosition + dragOffset)
    }) {
        content()
        if (isCurrentDropTarget && !isDragging && dataToDrop != null) {
            @Suppress("UNCHECKED_CAST")
            onDrop(dataToDrop as T)
            dragTargetInfo.dataToDrop = null
        }
    }
}
