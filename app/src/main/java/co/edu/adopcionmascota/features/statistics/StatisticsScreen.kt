package co.edu.adopcionmascota.features.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.MarkEmailUnread
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.domain.model.PetType
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.model.Statistics
import co.edu.adopcionmascota.features.publication.label

private val StatsBackground = Color(0xFFF3F7FF)
private val StatsCard = Color(0xFFEAF2FF)
private val StatsDark = Color(0xFF17365D)
private val StatsMuted = Color(0xFF64748B)

// Estadísticas de la plataforma con los datos reales de la base de datos
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = StatsBackground,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.statistics_title), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            StatisticsContent(statistics = state.statistics, contentPadding = padding)
        }
    }
}

@Composable
private fun StatisticsContent(statistics: Statistics, contentPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            Text(text = stringResource(R.string.statistics_detail), color = StatsMuted, fontSize = 13.sp)
        }

        // Indicadores principales
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Icons.Outlined.Inventory2, statistics.totalPublications.toString(), stringResource(R.string.stats_publications), Modifier.weight(1f))
                StatCard(Icons.Outlined.Favorite, statistics.completedAdoptions.toString(), stringResource(R.string.stats_adoptions), Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Icons.Outlined.MarkEmailUnread, statistics.totalRequests.toString(), stringResource(R.string.stats_requests, statistics.pendingRequests), Modifier.weight(1f))
                StatCard(Icons.Outlined.ChatBubbleOutline, statistics.totalComments.toString(), stringResource(R.string.comments), Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Icons.Outlined.Groups, statistics.totalUsers.toString(), stringResource(R.string.stats_users), Modifier.weight(1f))
                StatCard(Icons.Outlined.Timer, "${statistics.averageReviewMinutes} min", stringResource(R.string.average_time_lower), Modifier.weight(1f))
            }
        }

        // Publicaciones por estado
        item {
            BarSection(
                title = stringResource(R.string.stats_by_status),
                rows = listOf(
                    PublicationStatus.PENDING, PublicationStatus.VERIFIED, PublicationStatus.REJECTED,
                    PublicationStatus.RESOLVED, PublicationStatus.PAUSED
                ).map { stringResource(it.label) to statistics.countFor(it) },
                color = AppPrimary
            )
        }

        // Publicaciones por categoría (incluye Veterinaria)
        item {
            BarSection(
                title = stringResource(R.string.stats_by_category),
                rows = PetType.entries.map { it.label() to statistics.countFor(it) },
                color = Color(0xFF067647)
            )
        }
    }
}

@Composable
private fun StatCard(icon: ImageVector, value: String, title: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = StatsCard)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(22.dp))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = StatsDark)
            Text(text = title, fontSize = 12.sp, color = StatsMuted)
        }
    }
}

// Barras horizontales sencillas, proporcionales al valor más alto
@Composable
private fun BarSection(title: String, rows: List<Pair<String, Int>>, color: Color) {
    val max = rows.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1
    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, color = StatsDark)
            rows.forEach { (label, value) ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row {
                        Text(text = label, fontSize = 13.sp, color = StatsDark, modifier = Modifier.weight(1f))
                        Text(text = value.toString(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = StatsDark)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color.copy(alpha = 0.12f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(value.toFloat() / max)
                                .clip(RoundedCornerShape(4.dp))
                                .background(color)
                        )
                    }
                }
            }
        }
    }
}
