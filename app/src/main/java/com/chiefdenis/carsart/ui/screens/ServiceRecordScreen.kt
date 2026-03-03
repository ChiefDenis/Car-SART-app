package com.chiefdenis.carsart.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chiefdenis.carsart.data.repository.UserPreferences
import com.chiefdenis.carsart.utils.CurrencyFormatter
import com.chiefdenis.carsart.utils.UnitConverter
import com.chiefdenis.carsart.data.repository.AppUnitSystem
import java.math.BigDecimal
import com.chiefdenis.carsart.data.database.ServiceType
import com.chiefdenis.carsart.ui.theme.CarSartMotion
import com.chiefdenis.carsart.ui.utils.ServiceTypeUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceRecordScreen(
    viewModel: ServiceRecordViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
    onEdit: (String) -> Unit = {}
) {
    val serviceRecord by viewModel.serviceRecord.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    // For now, use default preferences - this can be enhanced later
    val userPreferences = UserPreferences()
    
    LaunchedEffect(Unit) {
        viewModel.loadServiceRecord()
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    AnimatedVisibility(
        visible = !isLoading,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = CarSartMotion.tokens.durationMedium4,
                easing = EaseOutCubic
            )
        ) + slideInVertically(
            animationSpec = tween(
                durationMillis = CarSartMotion.tokens.durationMedium4,
                easing = EaseOutCubic
            ),
            initialOffsetY = { it / 4 }
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = CarSartMotion.tokens.durationShort2
            )
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Service Record Details",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { onEdit(serviceRecord?.id.toString() ?: "") }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
                
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    serviceRecord?.let { record ->
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 16.dp,
                                vertical = 16.dp
                            )
                        ) {
                            item {
                                ServiceRecordDetailCard(
                                    serviceRecord = record,
                                    userPreferences = userPreferences
                                )
                            }
                        }
                    } ?: run {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Service record not found",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceRecordDetailCard(
    serviceRecord: com.chiefdenis.carsart.data.database.ServiceRecord,
    userPreferences: UserPreferences
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = CarSartMotion.tokens.durationMedium3,
                easing = EaseOutCubic
            )
        ) + scaleIn(
            animationSpec = tween(
                durationMillis = CarSartMotion.tokens.durationMedium3,
                easing = EaseOutCubic
            ),
            initialScale = 0.9f
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = CarSartMotion.tokens.durationShort2
            )
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = ServiceTypeUtils.getIcon(serviceRecord.serviceType),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                    
                    Column {
                        Text(
                            text = ServiceTypeUtils.getDisplayName(serviceRecord.serviceType),
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = formatDate(serviceRecord.date),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
                
                // Service Details
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (serviceRecord.provider != null) {
                        ServiceDetailSection(
                            icon = Icons.Default.Person,
                            title = "Service Provider",
                            value = serviceRecord.provider!!
                        )
                    }
                    
                    ServiceDetailSection(
                        icon = Icons.Default.Money,
                        title = "Cost",
                        value = CurrencyFormatter.formatCurrency(
                            BigDecimal(serviceRecord.cost.toDouble()),
                            userPreferences.currency
                        )
                    )
                    
                    ServiceDetailSection(
                        icon = Icons.Default.Edit,
                        title = "Mileage",
                        value = UnitConverter.formatDistance(
                            serviceRecord.mileage,
                            userPreferences.unitSystem
                        )
                    )
                    
                    if (serviceRecord.notes != null) {
                        ServiceDetailSection(
                            icon = Icons.Default.Note,
                            title = "Notes",
                            value = serviceRecord.notes!!
                        )
                    }
                    
                    if (serviceRecord.receiptPhotoUris.isNotEmpty()) {
                        ServiceDetailSection(
                            icon = Icons.Default.Receipt,
                            title = "Receipt Photos",
                            value = "${serviceRecord.receiptPhotoUris.size} photos attached"
                        )
                    }
                    
                    if (serviceRecord.nextServiceDueDate != null || serviceRecord.nextServiceDueMileage != null) {
                        ServiceDetailSection(
                            icon = Icons.Default.CalendarMonth,
                            title = "Next Service Due",
                            value = buildString {
                                if (serviceRecord.nextServiceDueDate != null) {
                                    append(formatDate(serviceRecord.nextServiceDueDate!!))
                                }
                                if (serviceRecord.nextServiceDueDate != null && serviceRecord.nextServiceDueMileage != null) {
                                    append(" • ")
                                }
                                if (serviceRecord.nextServiceDueMileage != null) {
                                    append(UnitConverter.formatDistance(
                                        serviceRecord.nextServiceDueMileage,
                                        userPreferences.unitSystem
                                    ))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceDetailSection(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}

fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
