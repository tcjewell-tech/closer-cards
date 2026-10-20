package com.tom.closercards.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.tom.closercards.R

@Composable
fun SetupScreen(onStart: (nameA: String, nameB: String) -> Unit) {
    var nameA by rememberSaveable { mutableStateOf("") }
    var nameB by rememberSaveable { mutableStateOf("") }
    val defaultA = stringResource(R.string.default_a)
    val defaultB = stringResource(R.string.default_b)

    ScreenColumn {
        Text(stringResource(R.string.setup_title), style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(R.string.setup_hint), style = MaterialTheme.typography.bodyMedium)
        NameField(nameA, { nameA = it }, R.string.name_a_label, defaultA, ImeAction.Next)
        NameField(nameB, { nameB = it }, R.string.name_b_label, defaultB, ImeAction.Done)
        Button(
            onClick = { onStart(nameA.trim().ifEmpty { defaultA }, nameB.trim().ifEmpty { defaultB }) },
            modifier = Modifier.fillMaxWidth(),
        ) { Text(stringResource(R.string.start_set_1)) }
    }
}

@Composable
private fun NameField(value: String, onChange: (String) -> Unit, label: Int, placeholder: String, ime: ImeAction) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(stringResource(label)) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ime),
        modifier = Modifier.fillMaxWidth(),
    )
}
