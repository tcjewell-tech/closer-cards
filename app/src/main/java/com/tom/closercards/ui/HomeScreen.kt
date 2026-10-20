package com.tom.closercards.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.tom.closercards.R

@Composable
fun HomeScreen(onStart: () -> Unit) = ScreenColumn {
    Text(
        stringResource(R.string.app_name),
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
    )
    Text(stringResource(R.string.home_how), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
    Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.start)) }
}
