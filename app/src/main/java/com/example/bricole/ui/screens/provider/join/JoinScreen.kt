package com.example.bricole.ui.screens.provider.join

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PhoneCallback
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bricole.R
import com.example.bricole.data.JoinRequest
import com.example.bricole.ui.screens.ErrorCard
import com.example.bricole.ui.screens.Loading
import com.example.bricole.ui.screens.provider.common.SearchCity
import com.example.bricole.ui.screens.provider.common.SearchService
import com.example.bricole.ui.screens.provider.common.TopBarProvider
import com.example.bricole.ui.theme.OnPrimary
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinScreen(onBack: () -> Unit) {

    val viewModel: JoinViewModel = viewModel(factory = JoinViewModel.factory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }

    var selectedCity by rememberSaveable { mutableStateOf("") }
    var cityExpanded by remember { mutableStateOf(false) }

    var serviceId: Int? by remember { mutableStateOf(null) }
    var serviceExpanded by remember { mutableStateOf(false) }
    var serviceName by rememberSaveable { mutableStateOf("") }

    fun clearJoinFields() {
        name = ""
        phone = ""
        selectedCity = ""
        serviceName = ""
        serviceId = null
    }

    LaunchedEffect(state.joinSuccess) {
        if (state.joinSuccess) {
            delay(10000)
            clearJoinFields()
            viewModel.clearJoin()
        }
    }

    val isValid =
        name.isNotBlank() && phone.isNotBlank() && serviceName.isNotBlank() && selectedCity.isNotBlank()

    Scaffold(topBar = {
        TopBarProvider(
            onBack = onBack,
            title = stringResource(R.string.join_bricole)
        )
    }) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(22.dp)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                NameInput(name = name, onNameChanged = { name = it })
                PhoneInput(phone = phone, onPhoneChanged = { phone = it })
                SearchService(
                    serviceExpanded = serviceExpanded,
                    onServiceExpanded = { serviceExpanded = it },
                    serviceName = serviceName,
                    services = state.services,
                    onServiceNameChanged = { newValue -> serviceName = newValue },
                    onServiceIdChanged = { newValue -> serviceId = newValue })
                SearchCity(
                    cityExpanded = cityExpanded,
                    onCityExpanded = { cityExpanded = it },
                    selectedCity = selectedCity,
                    onCityChanged = { selectedCity = it },
                    cities = state.cities
                )
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = isValid && !state.isLoading,
                onClick = {
                    viewModel.join(
                        request = JoinRequest(
                            name = name,
                            phone = phone,
                            city = selectedCity,
                            serviceId = serviceId
                        )
                    )
                }) {
                Text("Join")
            }

            when {
                state.isLoading -> {
                    Loading(title = "loading")
                }

                state.error != null -> {
                    ErrorCard(
                        error = state.error,
                        retryCount = state.retryCount,
                        onRetry = {
                            viewModel.retryJoin(
                                request = JoinRequest(
                                    name = name,
                                    phone = phone,
                                    city = selectedCity,
                                    serviceId = serviceId
                                )
                            )
                        })
                }

                state.joinSuccess -> {
                    Spacer(Modifier.height(14.dp))
                    JoinSuccess(onDismiss = {
                        clearJoinFields()
                        viewModel.clearJoin()
                    })
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JoinSuccess(onDismiss: () -> Unit) {
    BasicAlertDialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(),
        content = {
            Card(
                modifier = Modifier.padding(24.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.divider_dark)
                ),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                color = colorResource(id = R.color.primary_blue).copy(alpha = 0.15f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = colorResource(R.color.primary_blue),
                            modifier = Modifier.size(60.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.confirm_join),
                        color = OnPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.welcome_to_bricole),
                        color = OnPrimary.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        })
}

@Composable
private fun NameInput(name: String, onNameChanged: (String) -> Unit) {
    OutlinedTextField(
        value = name,
        onValueChange = { newName -> onNameChanged(newName) },
        textStyle = LocalTextStyle.current,
        label = { Text(text = "name") },
        singleLine = true,
        placeholder = { Text(stringResource(R.string.name_example)) },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Person, contentDescription = "name")
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun PhoneInput(phone: String, onPhoneChanged: (String) -> Unit) {
    val prefix = "+2126"
    val maxLength = 13
    val isValidPhone = phone.length == maxLength && phone.startsWith(prefix)
    val isError = phone.isNotEmpty() && !isValidPhone

    OutlinedTextField(
        value = phone,
        onValueChange = { newPhone ->
            if (newPhone.isEmpty()) {
                onPhoneChanged("")
            } else if (newPhone.startsWith(prefix)) {
                val digitsOnly = newPhone  //.substring(prefix.length).filter { it.isDigit() }

                // the max is max length we limited
                if (digitsOnly.length <= maxLength) {
                    onPhoneChanged(digitsOnly)
                }
            } else {
                // If user tries to type without prefix,add it
                val digitsOnly = newPhone    //.filter { it.isDigit() }

                if (digitsOnly.length <= maxLength) {
                    onPhoneChanged(digitsOnly)
                }
            }
        },
        isError = isError,
        label = { Text(text = "phone") },
        leadingIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.PhoneCallback,
                contentDescription = "Phone"
            )
        },
        supportingText = {
            when {
                isError -> Text(
                    text = stringResource(R.string.phone_error),
                    color = MaterialTheme.colorScheme.error
                )

                isValidPhone -> Text(
                    text = stringResource(R.string.valid_phone),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}