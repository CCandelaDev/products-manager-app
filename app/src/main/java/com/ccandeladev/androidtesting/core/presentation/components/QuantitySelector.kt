package com.ccandeladev.androidtesting.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccandeladev.androidtesting.R

@Composable
fun QuantitySelector(
    modifier: Modifier = Modifier,
    quantity: String,
    canDecrease: Boolean,
    canIncrease: Boolean,
    onDecreaseSelected: () -> Unit,
    onIncreaseSelected: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon to decrease
        IconButton(
            onClick = { onDecreaseSelected() },
            modifier = Modifier.size(24.dp),
            enabled = canDecrease
        ) {
            Icon(
                Icons.Default.Remove,
                contentDescription = stringResource(R.string.cd_drecrease_quantity)
            )
        }

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(32.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = quantity,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Icon to increase
        IconButton(
            onClick = { onIncreaseSelected() },
            modifier = Modifier.size(24.dp),
            enabled = canIncrease
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = stringResource(R.string.cd_increase_quantity)
            )
        }

    }
}