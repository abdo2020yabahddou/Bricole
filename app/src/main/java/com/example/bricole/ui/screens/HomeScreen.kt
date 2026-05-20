package com.example.bricole.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.bricole.R
import com.example.bricole.data.HomeState
import com.example.bricole.data.ServiceResponseDto
import com.example.bricole.ui.theme.topHomeColor
import com.example.bricole.viewModels.ServiceViewModel


@Composable
fun HomeScreen(
    onServiceClick: (ServiceResponseDto) -> Unit,
    onSearchClick: () -> Unit,
    onJoinClick: () -> Unit
) {

    val serviceViewModel: ServiceViewModel = viewModel(factory = ServiceViewModel.factory)
    val state by serviceViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { HomeAppBar() },
        modifier = Modifier,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .background(topHomeColor)
                    .heightIn(max = 42.dp),
                containerColor = topHomeColor
            ) { }
        }
    ) { contentPadding ->

        when {
            state.isLoading -> {
                LoadingScreen()
            }

            state.error != null -> {
                ErrorCard(state.error, state, serviceViewModel)
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    userScrollEnabled = true,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(12.dp),
                    modifier = Modifier
                        .padding(contentPadding)
                        .background(topHomeColor)
                ) {
                    item(span = { GridItemSpan(2) }) {
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))
                            SearchBricoleCard(onSearchClick = {
                                onSearchClick()
                            })
                        }
                    }

                    item(span = { GridItemSpan(2) }) {
                        Box {
                            Text(
                                text = stringResource(R.string.our_services),
                                fontSize = 30.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                    items(state.services) {
                        ServiceItem(dto = it, onClick = { onServiceClick(it) })
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    item(span = { GridItemSpan(2) }) {
                        Column {
                            Spacer(modifier = Modifier.height(20.dp))
                            JoinBricoleCard(onJoinClick = {
                                onJoinClick()
                            })
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(
    error: String?,
    state: HomeState,
    serviceViewModel: ServiceViewModel
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        error?.let { message ->
            Text(
                text = message,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(22.dp))
            OutlinedButton(
                modifier = Modifier.height(42.dp), shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp
                ),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ), enabled = state.retryCount < 5, onClick = {
                    serviceViewModel.retryServices()
                }) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Retry"
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Refresh",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun ServiceItem(dto: ServiceResponseDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(0.85f)
            .fillMaxWidth()
            .clickable(onClick = { onClick() }),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = servicePhotoUrl(dto.name),
                contentDescription = dto.name,
                contentScale = ContentScale.FillBounds,
                alpha = 0.9f,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = RoundedCornerShape(8.dp))
                    .weight(0.55f)
                    .background(colorResource(R.color.card_dark))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.22f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dto.name,
                    textAlign = TextAlign.Left,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .padding(bottom = 8.dp, start = 8.dp)
                        .weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .padding(end = 1.dp)
                        .clip(shape = CircleShape)
                        .background(colorResource(R.color.primary_blue)),
                    contentAlignment = Alignment.Center
                ) {
                    dto.icon?.let { Text(text = it, fontSize = 18.sp) }
                }
            }
        }
    }
}

fun servicePhotoUrl(serviceName: String?): Int {

    return when (serviceName?.lowercase()) {
        "plumbing" -> R.drawable.plumbing
        "electrical" -> R.drawable.electricity
        "carpentry" -> R.drawable.carpentry
        "painting" -> R.drawable.painting
        "cleaning" -> R.drawable.cleaning
        "hvac" -> R.drawable.hvac
        "locksmith" -> R.drawable.locksmith
        "glass repair" -> R.drawable.glass_repair
        "roofing" -> R.drawable.roofing
        "masonry" -> R.drawable.masonry
        else -> R.drawable.plumbing
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
            Text(
                text = stringResource(R.string.loading_services),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeAppBar() {
    CenterAlignedTopAppBar(
        title = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    color = colorResource(R.color.primary_blue),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = stringResource(R.string.find),
                    color = colorResource(R.color.text_secondary),
                    fontSize = 16.sp
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = topHomeColor
        )
    )
}

@Composable
private fun JoinBricoleCard(onJoinClick: () -> Unit) {
    Card(
        border = BorderStroke(0.1.dp, Color.Gray),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.card_dark)
        )
    ) {
        Column(
            modifier = Modifier
                .heightIn(max = 150.dp)
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.join_provider),
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.weight(1.0f))
            FloatingActionButton(
                onClick = { onJoinClick() },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add provider")
            }
        }
    }
}

@Composable
private fun SearchBricoleCard(onSearchClick: () -> Unit) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clickable {
                    onSearchClick()
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Search, contentDescription = "search")
                Text(
                    text = stringResource(R.string.search_by_service_city),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}