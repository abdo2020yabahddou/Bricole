package com.example.bricole.ui.screens.provider.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bricole.R
import com.example.bricole.data.ProviderResponseDto
import com.example.bricole.data.ServiceResponseDto
import com.example.bricole.ui.screens.provider.listing.ProviderCard
import com.example.bricole.ui.theme.Primary
import com.example.bricole.ui.theme.TextSub


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onBack: () -> Unit) {

    val searchViewModel: SearchViewModel = viewModel(factory = SearchViewModel.factory)
    val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()

    var cityExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedCity by rememberSaveable { mutableStateOf("") }

    var serviceId by rememberSaveable { mutableIntStateOf(0) }
    var serviceName by rememberSaveable { mutableStateOf("") }
    var serviceExpanded by rememberSaveable { mutableStateOf(false) }

    var hasSearched by rememberSaveable { mutableStateOf(false) }

    val isValid = selectedCity.isNotBlank() && serviceName.isNotBlank()

    Scaffold(topBar = { SearchTopBar(onBack) }) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)

                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 22.dp)
                    .padding(top = 22.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                SearchService(
                    serviceExpanded = serviceExpanded,
                    serviceName = serviceName,
                    services = uiState.services,
                    onServiceExpanded = { serviceExpanded = it },
                    onServiceIdChanged = { newValue ->
                        serviceId = newValue
                        hasSearched = false
                    },
                    onServiceNameChanged = { newValue ->
                        serviceName = newValue
                        hasSearched = false
                    }
                )
                SearchCity(
                    cityExpanded = cityExpanded,
                    selectedCity = selectedCity,
                    cities = uiState.cities,
                    onCityExpanded = { cityExpanded = it },
                    onCityChanged = { newValue ->
                        selectedCity = newValue
                        hasSearched = false
                    })

                Button(
                    enabled = isValid && !uiState.isLoading && !hasSearched,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    onClick = {
                        hasSearched = true
                        searchViewModel.search(serviceId, selectedCity)
                    }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = TextSub,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Search",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            when {
                uiState.isLoading -> {
                    LoadingScreen(contentPadding)
                }

                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        uiState.error?.let {
                            Text(it, color = Color.Red.copy(alpha = 0.8f), fontSize = 22.sp)
                        }
                    }
                }

                else ->
                    if (hasSearched) {
                        if (uiState.providers.isEmpty()) {
                            EmptyCase()
                        } else {
                            Success(uiState.providers)
                        }
                    }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchCity(
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SearchService(
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

@Composable
private fun ColumnScope.EmptyCase() {
    Box(
        Modifier
            .weight(1f)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PersonOff,
                contentDescription = "Nothing found",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(64.dp)
            )
            Text(
                stringResource(R.string.no_pro_found),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 15.sp
            )
            Text(
                text = stringResource(R.string.try_again),
                color = MaterialTheme.colorScheme.outlineVariant,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ColumnScope.Success(providers: List<ProviderResponseDto>) {
    Text(
        text = "${providers.size} result${if (providers.size != 1) "s" else ""} found",
        fontSize = 20.sp,
        color = Primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(providers) { provider ->
            ProviderCard(provider)
        }
    }
}

@Composable
private fun ColumnScope.LoadingScreen(contentPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(paddingValues = contentPadding),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            trackColor = ProgressIndicatorDefaults.circularDeterminateTrackColor,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            strokeWidth = 4.dp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(onBack: () -> Unit) {
    TopAppBar(
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    stringResource(R.string.search_providers),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    color = colorResource(R.color.primary_blue)
                )
            }
        },
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