package com.example.bricole.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PhoneCallback
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.bricole.data.HomeState
import com.example.bricole.data.JoinRequest
import com.example.bricole.data.SearchState
import com.example.bricole.ui.theme.OnPrimary
import com.example.bricole.viewModels.ProviderViewModel
import com.example.bricole.viewModels.ServiceViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinScreen(onBack: () -> Unit) {

    val providerViewModel: ProviderViewModel = viewModel(factory = ProviderViewModel.factory)
    val proJoinState by providerViewModel.proJoinState.collectAsStateWithLifecycle()
    val proSearchState by providerViewModel.proSearchState.collectAsStateWithLifecycle()

    val serviceViewModel: ServiceViewModel = viewModel(factory = ServiceViewModel.factory)
    val serviceState by serviceViewModel.uiState.collectAsStateWithLifecycle()

    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }

    var selectedCity by rememberSaveable { mutableStateOf("") }
    var cityExpanded by remember { mutableStateOf(false) }

    var serviceId: Int? by remember { mutableStateOf(null) }
    var serviceExpanded by remember { mutableStateOf(false) }
    var serviceName by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(proJoinState.joinSuccess) {
        if (proJoinState.joinSuccess) {
            delay(10000)
            name = ""
            phone = ""
            selectedCity = ""
            serviceName = ""
            serviceId = null
            providerViewModel.clearJoin()
        }
    }

    val isValid =
        name.isNotBlank() && phone.isNotBlank() && serviceName.isNotBlank() && selectedCity.isNotBlank()

    Scaffold(topBar = { JoinTopBar(onBack) }) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(22.dp)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                NameInput(name, onNameChanged = { name = it })
                PhoneInput(phone, onPhoneChanged = { phone = it })
                ServiceInput(
                    serviceExpanded = serviceExpanded,
                    onExpandedChange = { serviceExpanded = it },
                    serviceName = serviceName,
                    serviceState = serviceState,
                    onServiceNameChanged = { newValue -> serviceName = newValue },
                    onServiceIdChanged = { newValue -> serviceId = newValue })
                SearchCity(
                    cityExpanded = cityExpanded,
                    onExpandedChange = { cityExpanded = it },
                    selectedCity = selectedCity,
                    onCitySelected = { selectedCity = it },
                    searchState = proSearchState
                )
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = isValid && !proJoinState.isLoading,
                onClick = {
                    providerViewModel.join(
                        request = JoinRequest(
                            name,
                            phone,
                            selectedCity,
                            serviceId
                        )
                    )
                }) {
                Text("Join")
            }

            when {
                proJoinState.isLoading -> {
                    LoadingScreen()
                }

                proJoinState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        proJoinState.error?.let {
                            Text(it, color = Color.Red.copy(alpha = 0.8f), fontSize = 22.sp)
                        }
                    }
                }

                proJoinState.joinSuccess -> {
                    Spacer(Modifier.height(14.dp))
                    JoinSuccess(onDismiss = {
                        name = ""
                        phone = ""
                        selectedCity = ""
                        serviceName = ""
                        serviceId = null
                        providerViewModel.clearJoin()
                    })
                }
            }
        }
    }

}

//@Composable
//private fun JoinSuccess() {
//    Card(
//        modifier = Modifier
//            .padding(24.dp),
//        shape = RoundedCornerShape(32.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = colorResource(R.color.divider_dark)
//        ),
//        elevation = CardDefaults.cardElevation(10.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(32.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//
//            Box(
//                modifier = Modifier
//                    .size(100.dp)
//                    .background(
//                        colorResource(R.color.primary_blue).copy(alpha = 0.15f),
//                        CircleShape
//                    ),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Check,
//                    contentDescription = "Success",
//                    tint = colorResource(R.color.primary_blue),
//                    modifier = Modifier.size(60.dp)
//                )
//            }
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Text(
//                text = stringResource(R.string.confirm_join),
//                color = OnPrimary,
//                fontWeight = FontWeight.Bold,
//                fontSize = 24.sp,
//                textAlign = TextAlign.Center
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Text(
//                text = stringResource(R.string.welcome_to_bricole),
//                color = OnPrimary.copy(alpha = 0.8f),
//                fontSize = 16.sp,
//                textAlign = TextAlign.Center
//            )
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JoinSuccess(onDismiss: () -> Unit) {
    BasicAlertDialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(),
        content = {
            Card(
                modifier = Modifier
                    .padding(24.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(R.color.divider_dark)
                ),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                colorResource(R.color.primary_blue).copy(alpha = 0.15f),
                                CircleShape
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

//@Composable
//private fun JoinSuccess1() {
//    Card(
//        modifier = Modifier
//            .padding(24.dp)
//            .clip(CircleShape),
//        shape = RoundedCornerShape(22.dp),
//        colors = CardDefaults.cardColors(
//            containerColor = colorResource(R.color.divider_dark)
//        ),
//        elevation = CardDefaults.cardElevation(10.dp)
//    ) {
//        Column(
//            modifier = Modifier.padding(32.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Box(
//                contentAlignment = Alignment.Center,
//                modifier = Modifier
//                    .size(70.dp)
//                    .background(
//                        colorResource(R.color.divider_dark).copy(alpha = 0.15f),
//                        CircleShape
//                    ),
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Check,
//                    contentDescription = "Success",
//                    tint = OnPrimary,
//                    modifier = Modifier.size(60.dp)
//                )
//            }
//
//            Text(
//                stringResource(R.string.confirm_join),
//                color = OnPrimary,
//                fontWeight = FontWeight.Bold,
//                fontSize = 20.sp,
//                textAlign = TextAlign.Center
//            )
//            Text(
//                text = stringResource(R.string.welcome_to_bricole),
//                color = colorResource(R.color.primary_blue),
//                fontSize = 16.sp,
//                textAlign = TextAlign.Center
//            )
//        }
//    }
//}


@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.Red,
            trackColor = ProgressIndicatorDefaults.circularDeterminateTrackColor,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            strokeWidth = 2.dp
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ServiceInput(
    serviceExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    serviceName: String,
    onServiceNameChanged: (String) -> Unit,
    serviceState: HomeState,
    onServiceIdChanged: (Int) -> Unit
) {

    ExposedDropdownMenuBox(
        expanded = serviceExpanded,
        onExpandedChange = { onExpandedChange(!serviceExpanded) }) {
        OutlinedTextField(
            value = serviceName,
            onValueChange = {},
            label = { Text("service") },
            readOnly = true,
            placeholder = { Text("Select a service") },
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
                .menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            onDismissRequest = { onExpandedChange(false) },
            expanded = serviceExpanded
        ) {
            serviceState.services.forEach { service ->
                DropdownMenuItem(text = { Text(service.name) }, onClick = {
                    onServiceIdChanged(service.id)
                    onServiceNameChanged(service.name)
                    onExpandedChange(false)
                })
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchCity(
    cityExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedCity: String,
    onCitySelected: (String) -> Unit,
    searchState: SearchState
) {
    ExposedDropdownMenuBox(
        expanded = cityExpanded,
        onExpandedChange = { onExpandedChange(!cityExpanded) }) {
        OutlinedTextField(
            value = selectedCity,
            onValueChange = {},
            label = { Text(text = "city") },
            placeholder = { Text("Select your city") },
            readOnly = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Place,
                    contentDescription = "Location"
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded)
            }, modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = cityExpanded,
            onDismissRequest = { onExpandedChange(false) }) {
            searchState.cities.forEach { city ->
                DropdownMenuItem(text = { Text(city) }, onClick = {
                    onCitySelected(city)
                    onExpandedChange(false)
                })
            }
        }

    }
}

@Composable
private fun NameInput(name: String, onNameChanged: (String) -> Unit) {
    OutlinedTextField(
        value = name,
        onValueChange = { newName -> onNameChanged(newName) },
        textStyle = LocalTextStyle.current,
        label = { Text(text = "name") },
        singleLine = true,
        placeholder = { Text("Ayman Saad") },
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Person, contentDescription = "name")
        }, modifier = Modifier.fillMaxWidth()
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
            // Only allow input that matches the pattern
            if (newPhone.isEmpty()) {
                onPhoneChanged("")
            } else if (newPhone.startsWith(prefix)) {
                // only numbers are allowed after prefix
                val digitsOnly = newPhone//.substring(prefix.length).filter { it.isDigit() }

                // the max is max length we limited
                if (digitsOnly.length <= maxLength) {
                    onPhoneChanged(digitsOnly)
                }
            } else {
                // If user tries to type without prefix,add it
                val digitsOnly = newPhone//.filter { it.isDigit() }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinTopBar(onBack: () -> Unit) {
    TopAppBar(
        modifier = Modifier,
        title = { Text(text = "Join Bricole", color = colorResource(R.color.primary_blue)) },
        navigationIcon = {
            IconButton(onClick = {
                onBack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colorResource(R.color.white)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorResource(R.color.divider_dark)
        )
    )
}