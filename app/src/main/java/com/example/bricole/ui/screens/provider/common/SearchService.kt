package com.example.bricole.ui.screens.provider.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HomeRepairService
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
import androidx.compose.ui.unit.dp
import com.example.bricole.data.ServiceResponseDto
import kotlin.collections.forEach

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SearchService(
    serviceExpanded: Boolean,
    onServiceExpanded: (Boolean) -> Unit,
    serviceName: String,
    onServiceNameChanged: (String) -> Unit,
    services: List<ServiceResponseDto>,
    onServiceIdChanged: (Int) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = serviceExpanded,
        onExpandedChange = { onServiceExpanded(!serviceExpanded) }) {
        OutlinedTextField(
            value = serviceName,
            onValueChange = {},
            label = { Text("service") },
            readOnly = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.HomeRepairService,
                    contentDescription = "service type"
                )
            },
            shape = RoundedCornerShape(12.dp),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceExpanded)
            },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            onDismissRequest = { onServiceExpanded(false) },
            expanded = serviceExpanded
        ) {
            services.forEach { service ->
                DropdownMenuItem(text = { Text(service.name) }, onClick = {
                    onServiceIdChanged(service.id)
                    onServiceNameChanged(service.name)
                    onServiceExpanded(false)
                })
            }
        }
    }
}