/*
 * Copyright (c) 2022 Olivier Patry
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.opatry.game.wordle.ui.compose.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import net.opatry.game.wordle.ui.compose.theme.AppIcon
import net.opatry.game.wordle.ui.compose.theme.painterResource


@ExperimentalAnimationApi
@Composable
fun Dialog(
    visible: Boolean,
    title: String? = null,
    modifier: Modifier = Modifier,
    modal: Boolean = false,
    onClose: () -> Unit,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(),
        exit = scaleOut() + fadeOut()
    ) {
        // fullscreen background for the dim effect, clickable to dismiss (unless modal)
        // the dialog itself is also clickable and does nothing to prevent dismiss when clicking on dialog area
        Box(
            Modifier
                .background(MaterialTheme.colors.surface.copy(alpha = ContentAlpha.medium))
                .clickable(MutableInteractionSource(), indication = null, onClick = {
                    if (!modal) {
                        onClose()
                    }
                })
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier
                    .shadow(24.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable(MutableInteractionSource(), indication = null, onClick = {})
                    .wrapContentHeight()
                    .background(MaterialTheme.colors.surface),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(Modifier.fillMaxWidth(), Alignment.TopEnd) {
                    IconButton(onClick = onClose) {
                        Icon(painterResource(AppIcon.Close), "Close")
                    }
                }

                if (title != null) {
                    Text(
                        title,
                        Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.h3
                    )
                }

                Box(
                    Modifier
                        .verticalScroll(rememberScrollState()).padding(16.dp)
                ) {
                    content()
                }
            }
        }
    }
}