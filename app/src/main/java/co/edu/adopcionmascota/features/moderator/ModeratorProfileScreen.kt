package co.edu.adopcionmascota.features.moderator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddModerator
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Beenhere
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import co.edu.adopcionmascota.core.component.AppBottomBar
import co.edu.adopcionmascota.core.component.AppDestination
import co.edu.adopcionmascota.domain.model.UserRole


private val ModeratorBlue = androidx.compose.ui.graphics.Color(0xFF4654F5)
private val ModeratorDark = androidx.compose.ui.graphics.Color(0xFF09243A)
private val ModeratorBackground = androidx.compose.ui.graphics.Color(0xFFF5F8FF)
private val ModeratorMuted = androidx.compose.ui.graphics.Color(0xFF647080)
private val ModeratorGreen = androidx.compose.ui.graphics.Color(0xFF00A879)
private val ModeratorRed = androidx.compose.ui.graphics.Color(0xFFD83D3D)
private val ModeratorYellow = androidx.compose.ui.graphics.Color(0xFFFFC943)
private val ModeratorLightBlue = androidx.compose.ui.graphics.Color(0xFFE6EFFF)

private data class Level(
    val name: String,
    val minPoints: Int,
    val icon: ImageVector
)
private  val levels= listOf(
    Level("Amigo Animal",0, Icons.Filled.VolunteerActivism),
    Level("Protector",100, Icons.Filled.Pets),
    Level("Guardian",500,Icons.Filled.AddModerator),
    Level("Hero de la mascotas",1000,Icons.Filled.Beenhere)

)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorScreen(
    onNavigationClick: (AppDestination) -> Unit = {},
    onEditCredentials: () -> Unit = {},
    onEthicalSettings: () -> Unit = {},
    onViewHistory: () -> Unit = {},
    onOpenManual: () -> Unit = {},
    onOpenTemplates: () -> Unit = {},
    onOpenShelters: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNotifications: () -> Unit = {},
) {

    val profileViewModel: ModeratorProfileViewModel = hiltViewModel()
    val state by profileViewModel.uiState.collectAsState()

    val user = state.user ?: return
    val isAdmin = user.role == UserRole.ADMIN

    val levelIndex = levels.indexOfLast {
        state.poinst >= it.minPoints
    }
    val level = levels[levelIndex]

    ModeratorScreenContent(
        name = user.name,
        email = user.email,
        roleLabel = if (isAdmin) "Moderador" else level.name,
        roleIcon = if (isAdmin) Icons.Filled.Gavel else level.icon,
        isAdmin = isAdmin,
        onNavigationClick = onNavigationClick,
        onEditCredentials = onEditCredentials,
        onEthicalSettings = onEthicalSettings,
        onViewHistory = onViewHistory,
        onOpenManual = onOpenManual,
        onOpenTemplates = onOpenTemplates,
        onOpenShelters = onOpenShelters,
        onLogout = onLogout,
        onNotifications = onNotifications
    )
}

@Composable
private fun ModeratorScreenContent(
    name: String,
    email: String,
    roleLabel: String,
    roleIcon: ImageVector,
    isAdmin: Boolean,
    onNavigationClick: (AppDestination) -> Unit = {},
    onEditCredentials: () -> Unit = {},
    onEthicalSettings: () -> Unit = {},
    onViewHistory: () -> Unit = {},
    onOpenManual: () -> Unit = {},
    onOpenTemplates: () -> Unit = {},
    onOpenShelters: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNotifications: () -> Unit = {}
) {
    Scaffold(
        containerColor = ModeratorBackground,
        bottomBar = {
            AppBottomBar(
                currentDestination = AppDestination.PROFILE,
                onDestinationSelected = onNavigationClick
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            ModeratorTopBar(
                onNotifications = onNotifications
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {

                Spacer(modifier = Modifier.height(12.dp))

                ModeratorProfileCard(
                    onEditCredentials = onEditCredentials,
                    onEthicalSettings = onEthicalSettings,
                    name = name,
                    email = email,
                    roleLabel = if(isAdmin) "Moderador" else roleLabel,
                    roleIcon = if (isAdmin) Icons.Filled.Gavel else roleIcon

                )

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle(
                    title = "Impacto Comunitario y Auditoría",
                    subtitle = "Últimos 30 días",
                    icon = {
                        Icon(
                            Icons.Default.Analytics,
                            contentDescription = null,
                            tint = ModeratorBlue
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCard(
                            title = "Casos Auditados",
                            value = "1,248",
                            description = "+42 esta semana",
                            icon = {
                                Icon(
                                    Icons.Default.Checklist,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.weight(1f),
                            accent = ModeratorBlue
                        )

                        MetricCard(
                            title = "Aprobados",
                            value = "1,089",
                            description = "87.2% tasa de aprobación",
                            icon = {
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.weight(1f),
                            accent = ModeratorGreen
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricCard(
                            title = "Subsanaciones",
                            value = "159",
                            description = "Carnet ilegible / sospecha",
                            icon = {
                                Icon(
                                    Icons.Default.Report,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.weight(1f),
                            accent = ModeratorYellow
                        )

                        MetricCard(
                            title = "Tiempo Promedio",
                            value = "18 min",
                            description = "Meta: <30 min (Excelente)",
                            icon = {
                                Icon(
                                    Icons.Default.Timer,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.weight(1f),
                            accent = ModeratorBlue
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                EthicalQueueCard()

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle(
                    title = "Últimas Acciones Registradas",
                    actionText = "Ver historial",
                    onAction = onViewHistory,
                    icon = {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ModeratorBlue
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                RecentActionsCard()

                Spacer(modifier = Modifier.height(24.dp))

                SectionTitle(
                    title = "Herramientas y Protocolo Ético",
                    icon = {
                        Icon(
                            Icons.Default.Gavel,
                            contentDescription = null,
                            tint = ModeratorBlue
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ToolsCard(
                    onOpenManual = onOpenManual,
                    onOpenTemplates = onOpenTemplates,
                    onOpenShelters = onOpenShelters
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.ui.graphics.Color(
                            0xFFFFDAD7
                        ),
                        contentColor = androidx.compose.ui.graphics.Color(
                            0xFFB42318
                        )
                    )
                ) {
                    Icon(
                        Icons.Default.Logout,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "CERRAR SESIÓN DE MODERADOR",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}



@Composable
private fun ModeratorTopBar(
    onNotifications: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.size(46.dp),
            shape = CircleShape,
            color = androidx.compose.ui.graphics.Color(0xFFE4E8FF)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = "Patitas Conectadas",
                    tint = ModeratorBlue,
                    modifier = Modifier.size(27.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PATITAS CONECTADAS",
                    color = ModeratorBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = ModeratorBlue,
                    modifier = Modifier.size(17.dp)
                )
            }

            Text(
                text = "Perfil De Moderador",
                color = ModeratorDark,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }

        IconButton(onClick = onNotifications) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notificaciones",
                tint = ModeratorDark
            )
        }
    }
}

@Composable
private fun ModeratorProfileCard(
    onEditCredentials: () -> Unit,
    onEthicalSettings: () -> Unit,
    name: String,
    email: String,
    roleLabel: String,
    roleIcon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(88.dp),
                    shape = CircleShape,
                    color = ModeratorLightBlue
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Foto del moderador",
                            tint = ModeratorBlue,
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = androidx.compose.ui.graphics.Color(0xFFE9ECFF)
                    ) {
                        Text(
                            text = "MOD NIVEL 3 • VERIFICADO",
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),
                            color = ModeratorBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = name,
                        fontSize = 19.sp,
                        lineHeight = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = ModeratorDark
                    )

                    Text(
                        text = roleLabel,
                        fontSize = 12.sp,
                        color = ModeratorMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Mail,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = ModeratorMuted
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = email,
                            fontSize = 10.sp,
                            color = ModeratorMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = ModeratorBackground
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InfoChip(
                            text = "Activo en Turno",
                            dotColor = ModeratorGreen
                        )

                        InfoChip(
                            text = "ID: #MOD-4092"
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        InfoChip(
                            text = "🏅 99.4% Rigor"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilledTonalButton(
                    onClick = onEditCredentials,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(30.dp),
                    contentPadding = PaddingValues(
                        horizontal = 10.dp,
                        vertical = 12.dp
                    ),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = ModeratorLightBlue,
                        contentColor = ModeratorDark
                    )
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Editar Credenciales",
                        fontSize = 12.sp,
                        lineHeight = 15.sp
                    )
                }

                FilledTonalButton(
                    onClick = onEthicalSettings,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(30.dp),
                    contentPadding = PaddingValues(
                        horizontal = 10.dp,
                        vertical = 12.dp
                    ),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = ModeratorLightBlue,
                        contentColor = ModeratorDark
                    )
                ) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Configuración Ética",
                        fontSize = 12.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoChip(
    text: String,
    dotColor: androidx.compose.ui.graphics.Color? = null
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = androidx.compose.ui.graphics.Color.White
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 8.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (dotColor != null) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )

                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = text,
                fontSize = 12.sp,
                color = ModeratorDark,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onAction: () -> Unit = {},
    icon: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = ModeratorDark,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 20.sp
        )

        if (subtitle != null) {
            Text(
                text = subtitle,
                color = ModeratorMuted,
                fontSize = 10.sp
            )
        }

        if (actionText != null) {
            androidx.compose.material3.TextButton(
                onClick = onAction,
                contentPadding = PaddingValues(
                    horizontal = 4.dp,
                    vertical = 4.dp
                )
            ) {
                Text(
                    text = actionText,
                    color = ModeratorBlue,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    description: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    accent: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(23.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    color = androidx.compose.ui.graphics.Color(0xFF444A53),
                    maxLines = 2,
                    lineHeight = 15.sp
                )

                Surface(
                    shape = CircleShape,
                    color = accent.copy(alpha = 0.13f)
                ) {
                    Box(
                        modifier = Modifier.size(34.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.runtime.CompositionLocalProvider(
                            androidx.compose.material3.LocalContentColor provides accent
                        ) {
                            icon()
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = value,
                fontSize = 29.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.Bold,
                color = ModeratorDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = accent
            )
        }
    }
}

@Composable
private fun EthicalQueueCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color.Black
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = ModeratorBlue
                ) {
                    Text(
                        text = "TURNO BOGOTÁ NORTE",
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp
                        ),
                        color = androidx.compose.ui.graphics.Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = ModeratorBlue
                ) {
                    Text(
                        text = "4",
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 9.dp
                        ),
                        color = androidx.compose.ui.graphics.Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Cola de Revisión Ética Activa",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "4 expedientes pendientes de validación veterinaria y legal.",
                color = androidx.compose.ui.graphics.Color(0xFF9CA3AF),
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            QueueItem(
                emoji = "✱",
                title = "Adopción Urgente (Cachorros en calle)",
                count = "2 casos",
                accent = androidx.compose.ui.graphics.Color(0xFFFF8AA2)
            )

            Spacer(modifier = Modifier.height(9.dp))

            QueueItem(
                emoji = "☻",
                title = "Mascota Perdida • Caso Luna",
                count = "1 caso",
                accent = ModeratorYellow
            )

            Spacer(modifier = Modifier.height(9.dp))

            QueueItem(
                emoji = "⚑",
                title = "Reporte de Comunidad (Posible Criadero)",
                count = "1 caso",
                accent = androidx.compose.ui.graphics.Color(0xFF55D5FF)
            )
        }
    }
}

@Composable
private fun QueueItem(
    emoji: String,
    title: String,
    count: String,
    accent: androidx.compose.ui.graphics.Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        color = androidx.compose.ui.graphics.Color(0xFF191919)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 22.sp,
                color = accent
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = title,
                modifier = Modifier.weight(1f),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = androidx.compose.ui.graphics.Color.White
            )

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(9.dp),
                color = accent.copy(alpha = 0.15f)
            ) {
                Text(
                    text = count,
                    modifier = Modifier.padding(
                        horizontal = 9.dp,
                        vertical = 6.dp
                    ),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = accent
                )
            }
        }
    }
}

@Composable
private fun RecentActionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(27.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            RecentActionItem(
                title = "Milo #204 (Golden Mestizo)",
                time = "Hace 20 min",
                status = "Aprobación concedida:",
                description = "Carnet de vacunación",
                tag = "Adopción Responsable",
                secondaryTag = "Usaquén",
                icon = {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = ModeratorGreen
                    )
                },
                accent = ModeratorGreen
            )

            Divider(
                modifier = Modifier.padding(vertical = 14.dp),
                color = ModeratorBackground
            )

            RecentActionItem(
                title = "Luna #102 (Gatita Carey)",
                time = "Hace 2 h",
                status = "Rechazo subsanable:",
                description = "Carnet de vacunación...",
                tag = "Falta Doc Oficial",
                secondaryTag = "Suba",
                icon = {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        tint = androidx.compose.ui.graphics.Color(0xFFB45309)
                    )
                },
                accent = androidx.compose.ui.graphics.Color(0xFFB45309)
            )

            Divider(
                modifier = Modifier.padding(vertical = 14.dp),
                color = ModeratorBackground
            )

            RecentActionItem(
                title = "Toby (Schnauzer Adulto)",
                time = "Hace 1 día",
                status = "Caso cerrado exitoso:",
                description = "Adopción formalizada...",
                tag = "Final Feliz",
                icon = {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = ModeratorBlue
                    )
                },
                accent = ModeratorBlue
            )
        }
    }
}

@Composable
private fun RecentActionItem(
    title: String,
    time: String,
    status: String,
    description: String,
    tag: String,
    secondaryTag: String? = null,
    icon: @Composable () -> Unit,
    accent: androidx.compose.ui.graphics.Color
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = CircleShape,
            color = accent.copy(alpha = 0.15f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                icon()
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ModeratorDark
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = time,
                    fontSize = 10.sp,
                    color = ModeratorMuted
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Row {
                Text(
                    text = status,
                    fontSize = 12.sp,
                    color = accent
                )
            }

            Text(
                text = description,
                fontSize = 12.sp,
                color = ModeratorMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SmallTag(
                    text = tag,
                    color = accent
                )

                if (secondaryTag != null) {
                    SmallTag(
                        text = secondaryTag,
                        color = ModeratorBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun SmallTag(
    text: String,
    color: androidx.compose.ui.graphics.Color
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 9.dp,
                vertical = 5.dp
            ),
            color = color,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun ToolsCard(
    onOpenManual: () -> Unit,
    onOpenTemplates: () -> Unit,
    onOpenShelters: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(27.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 8.dp
            )
        ) {
            ToolItem(
                title = "Manual de Criterios Éticos",
                description = "Políticas anti-lucro y Ley de Protección Animal Col.",
                icon = {
                    Icon(
                        Icons.Default.Book,
                        contentDescription = null
                    )
                },
                onClick = onOpenManual
            )

            ToolItem(
                title = "Plantillas de Subsanación",
                description = "Gestionar motivos de rechazo y feedback amable",
                icon = {
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null
                    )
                },
                onClick = onOpenTemplates
            )

            ToolItem(
                title = "Registro de Refugios Avalados",
                description = "Base de fundaciones y veterinarias asociadas",
                icon = {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null
                    )
                },
                onClick = onOpenShelters
            )
        }
    }
}

@Composable
private fun ToolItem(
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(androidx.compose.ui.graphics.Color.White)
            .padding(vertical = 16.dp)
            .then(
                Modifier
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = ModeratorLightBlue
        ) {
            Box(contentAlignment = Alignment.Center) {
                androidx.compose.runtime.CompositionLocalProvider(
                    androidx.compose.material3.LocalContentColor provides ModeratorDark
                ) {
                    icon()
                }
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ModeratorDark
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = ModeratorMuted
            )
        }

        IconButton(onClick = onClick) {
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Abrir $title",
                tint = ModeratorMuted
            )
        }
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ModeratorScreenPreview() {
    MaterialTheme {
        ModeratorScreenContent(
            name = "María González",
            email = "moderadora@ejemplo.com",
            roleLabel = "Moderador",
            roleIcon = Icons.Filled.Gavel,
            isAdmin = true
        )
    }
}