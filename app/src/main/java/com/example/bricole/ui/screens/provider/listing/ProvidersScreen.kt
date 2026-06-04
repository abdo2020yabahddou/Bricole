package com.example.bricole.ui.screens.provider.listing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bricole.R
import com.example.bricole.ui.screens.ErrorCard
import com.example.bricole.ui.screens.Loading
import com.example.bricole.ui.screens.provider.common.EmptyState
import com.example.bricole.ui.screens.provider.common.ProviderCard
import com.example.bricole.ui.screens.provider.common.TopBarProvider


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProvidersScreen(
    serviceId: Int,
    serviceName: String,
    onBack: () -> Unit
) {

    val viewModel: ProviderListViewModel = viewModel(factory = ProviderListViewModel.factory)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(serviceId) {
        viewModel.loadProviders(serviceId)
    }

    Scaffold(
        topBar = {
            TopBarProvider(onBack = onBack, title = serviceName)
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = contentPadding)
        ) {

            when {
                state.isLoading -> {
                    Loading(title = stringResource(R.string.loading_providers))
                }

                state.error != null -> {
                    ErrorCard(
                        error = state.error,
                        retryCount = state.retryCount,
                        onRetry = { viewModel.retryProviders(serviceId) }
                    )
                }

                state.providers.isEmpty() -> {
                    EmptyState(
                        text1 = stringResource(R.string.no_providers_available),
                        text2 = stringResource(R.string.check_back_later)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "${state.providers.size} provider ${if (state.providers.size != 1) "s" else ""} available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                            )
                        }
                        items(items = state.providers, key = { it.proId }) { providers ->
                            ProviderCard(provider = providers)
                        }
                    }
                }
            }
        }
    }
}

