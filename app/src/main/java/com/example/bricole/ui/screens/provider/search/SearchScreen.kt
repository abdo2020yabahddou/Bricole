package com.example.bricole.ui.screens.provider.search

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bricole.R
import com.example.bricole.data.ProviderResponseDto
import com.example.bricole.ui.screens.ErrorCard
import com.example.bricole.ui.screens.Loading
import com.example.bricole.ui.screens.provider.common.EmptyState
import com.example.bricole.ui.screens.provider.common.ProviderCard
import com.example.bricole.ui.screens.provider.common.SearchCity
import com.example.bricole.ui.screens.provider.common.SearchService
import com.example.bricole.ui.screens.provider.common.TopBarProvider
import com.example.bricole.ui.theme.Primary
import com.example.bricole.ui.theme.TextSub


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onBack: () -> Unit) {

    val viewModel: SearchViewModel = viewModel(factory = SearchViewModel.factory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var cityExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedCity by rememberSaveable { mutableStateOf("") }

    var serviceId by rememberSaveable { mutableIntStateOf(0) }
    var serviceName by rememberSaveable { mutableStateOf("") }
    var serviceExpanded by rememberSaveable { mutableStateOf(false) }

    var hasSearched by rememberSaveable { mutableStateOf(false) }

    val isValid = selectedCity.isNotBlank() && serviceName.isNotBlank()

    Scaffold(topBar = {
        TopBarProvider(
            onBack = onBack,
            title = stringResource(R.string.search_providers)
        )
    }) { contentPadding ->
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
                    services = state.services,
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
                    cities = state.cities,
                    onCityExpanded = { cityExpanded = it },
                    onCityChanged = { newValue ->
                        selectedCity = newValue
                        hasSearched = false
                    })

                Button(
                    enabled = isValid && !state.isLoading && !hasSearched,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    onClick = {
                        hasSearched = true
                        viewModel.search(serviceId, selectedCity)
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
                state.isLoading -> {
                    Loading(title = "loading")
                }

                state.error != null -> {
                    ErrorCard(
                        error = state.error,
                        retryCount = state.retryCount,
                        onRetry = {
                            viewModel.retrySearch(
                                serviceId = serviceId,
                                city = selectedCity
                            )
                        }
                    )
                }
                else ->
                    if (hasSearched) {
                        if (state.providers.isEmpty()) {
                            EmptyState(
                                text1 = stringResource(R.string.no_pro_found),
                                text2 = stringResource(R.string.try_again)
                            )
                        } else {
                            Success(providers = state.providers)
                        }
                    }
            }
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
            ProviderCard(provider = provider)
        }
    }
}