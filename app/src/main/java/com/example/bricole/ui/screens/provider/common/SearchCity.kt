package com.example.bricole.ui.screens.provider.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SearchCity(
    cityExpanded: Boolean,
    onCityExpanded: (Boolean) -> Unit,
    selectedCity: String,
    onCityChanged: (String) -> Unit,
    cities: List<String>,
) {
    ExposedDropdownMenuBox(
        expanded = cityExpanded,
        onExpandedChange = { onCityExpanded(!cityExpanded) }) {
        OutlinedTextField(
            value = selectedCity,
            onValueChange = { },
            label = { Text("city") },
            readOnly = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = "Location"
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded)
            },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            onDismissRequest = { onCityExpanded(false) },
            expanded = cityExpanded
        ) {
            cities.forEach { city ->
                DropdownMenuItem(text = { Text(city) }, onClick = {
                    onCityChanged(city)
                    onCityExpanded(false)
                })
            }
        }
    }
}