package com.chiefdenis.carsart.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chiefdenis.carsart.data.database.ServiceType
import com.chiefdenis.carsart.ui.theme.CarSartMotion
import com.chiefdenis.carsart.ui.utils.ServiceTypeUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class ServiceIntervalType {
    DAYS, WEEKS, MONTHS, YEARS
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddServiceRecordScreen(viewModel: AddServiceRecordViewModel = hiltViewModel(), onServiceRecordAdded: () -> Unit) {
    val date by viewModel.date.collectAsState()
    val mileage by viewModel.mileage.collectAsState()
    val cost by viewModel.cost.collectAsState()
    val serviceType by viewModel.serviceType.collectAsState()
    val provider by viewModel.provider.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val receiptPhotos by viewModel.receiptPhotos.collectAsState()
    val nextServiceDueDate by viewModel.nextServiceDueDate.collectAsState()
    val nextServiceDueMileage by viewModel.nextServiceDueMileage.collectAsState()
    val interval by viewModel.interval.collectAsState()
    val intervalType by viewModel.intervalType.collectAsState()
    
    val scrollState = rememberLazyListState()
    var isContentVisible by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showPhotoPicker by remember { mutableStateOf(false) }
    
    val datePickerState = rememberDatePickerState()
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        isContentVisible = true
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    AnimatedVisibility(
        visible = isContentVisible,
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
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Add Service Record",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Handle back */ }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                state = scrollState,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    top = 80.dp,
                    bottom = 100.dp
                )
            ) {
                // Service Date Section
                item {
                    ServiceSectionCard(
                        title = "Service Date",
                        icon = Icons.Default.CalendarMonth,
                        description = "When was the service performed?"
                    ) {
                        OutlinedTextField(
                            value = date,
                            onValueChange = viewModel::onDateChange,
                            label = { Text("Date") },
                            placeholder = { Text("Select date") },
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Pick date"
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            readOnly = true
                        )
                    }
                }

                // Service Interval Section
                item {
                    ServiceSectionCard(
                        title = "Service Interval",
                        icon = Icons.Default.Schedule,
                        description = "How often should this service be performed?"
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = interval?.toString() ?: "",
                                onValueChange = { viewModel.onIntervalChange(it.toIntOrNull()) },
                                label = { Text("Interval") },
                                placeholder = { Text("Enter interval") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                            
                            ServiceIntervalTypeSelector(
                                selectedType = intervalType,
                                onTypeSelected = { viewModel.onIntervalTypeChange(it) }
                            )
                        }
                    }
                }

                // Service Type Section
                item {
                    ServiceSectionCard(
                        title = "Service Type",
                        icon = ServiceTypeUtils.getIcon(serviceType),
                        description = "What type of service was performed?"
                    ) {
                        ServiceTypeSelector(
                            selectedType = serviceType,
                            onTypeSelected = { viewModel.onServiceTypeChange(it) }
                        )
                    }
                }

                // Mileage Section
                item {
                    ServiceSectionCard(
                        title = "Mileage",
                        icon = Icons.Default.Edit,
                        description = "Current vehicle mileage"
                    ) {
                        OutlinedTextField(
                            value = mileage,
                            onValueChange = viewModel::onMileageChange,
                            label = { Text("Mileage") },
                            placeholder = { Text("Enter mileage") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }

                // Cost Section
                item {
                    ServiceSectionCard(
                        title = "Cost",
                        icon = Icons.Default.Receipt,
                        description = "How much did the service cost?"
                    ) {
                        OutlinedTextField(
                            value = cost,
                            onValueChange = viewModel::onCostChange,
                            label = { Text("Cost") },
                            placeholder = { Text("Enter cost") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Receipt,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }

                // Provider Section
                item {
                    ServiceSectionCard(
                        title = "Service Provider",
                        icon = Icons.Default.Person,
                        description = "Who performed the service?"
                    ) {
                        OutlinedTextField(
                            value = provider,
                            onValueChange = viewModel::onProviderChange,
                            label = { Text("Provider") },
                            placeholder = { Text("Enter service provider name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }

                // Notes Section
                item {
                    ServiceSectionCard(
                        title = "Notes",
                        icon = Icons.Default.Note,
                        description = "Additional details about the service"
                    ) {
                        OutlinedTextField(
                            value = notes,
                            onValueChange = viewModel::onNotesChange,
                            label = { Text("Notes") },
                            placeholder = { Text("Add any additional notes...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Note,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                    }
                }

                // Photos Section
                item {
                    ServiceSectionCard(
                        title = "Receipt Photos",
                        icon = Icons.Default.CameraAlt,
                        description = "Add photos of receipts"
                    ) {
                        PhotoUploadSection(
                            photos = receiptPhotos,
                            onAddPhoto = { viewModel.onAddPhoto(it) },
                            onRemovePhoto = { viewModel.onRemovePhoto(it) }
                        )
                    }
                }

                // Next Service Section
                item {
                    ServiceSectionCard(
                        title = "Next Service Due",
                        icon = Icons.Default.CalendarMonth,
                        description = "When should the next service be scheduled?"
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = nextServiceDueMileage ?: "",
                                onValueChange = { viewModel.onNextServiceDueMileageChange(it) },
                                label = { Text("Next Service Mileage") },
                                placeholder = { Text("Enter mileage for next service") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            
                            OutlinedTextField(
                                value = nextServiceDueDate ?: "",
                                onValueChange = { viewModel.onNextServiceDueDateChange(it) },
                                label = { Text("Next Service Date") },
                                placeholder = { Text("Select date for next service") },
                                trailingIcon = {
                                    IconButton(onClick = { showDatePicker = true }) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = "Pick date"
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                ),
                                readOnly = true
                            )
                        }
                    }
                }

                // Save Button Section
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    SaveButton(
                        onClick = {
                            viewModel.saveServiceRecord()
                            onServiceRecordAdded()
                        },
                        text = "Save Service Record"
                    )
                }
            }
        }

        // Floating Action Buttons
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PhotoFab(
                    onClick = { showPhotoPicker = true }
                )
                
                SaveFab(
                    onClick = {
                        viewModel.saveServiceRecord()
                        onServiceRecordAdded()
                    }
                )
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedDate ->
                            val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            val formattedDate = formatter.format(Date(selectedDate))
                            viewModel.onDateChange(formattedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun ServiceSectionCard(
    title: String,
    icon: ImageVector,
    description: String,
    content: @Composable () -> Unit
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
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
                
                content()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun ServiceTypeSelector(
    selectedType: ServiceType,
    onTypeSelected: (ServiceType) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ServiceType.values().forEach { type ->
            AssistChip(
                onClick = { onTypeSelected(type) },
                label = { Text(ServiceTypeUtils.getDisplayName(type)) },
                leadingIcon = {
                    Icon(
                        imageVector = ServiceTypeUtils.getIcon(type),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
                    containerColor = if (selectedType == type) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    },
                    labelColor = if (selectedType == type) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    leadingIconContentColor = if (selectedType == type) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                ),
                modifier = Modifier.wrapContentHeight()
            )
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun ServiceIntervalTypeSelector(
    selectedType: ServiceIntervalType,
    onTypeSelected: (ServiceIntervalType) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ServiceIntervalType.values().forEach { type ->
            AssistChip(
                onClick = { onTypeSelected(type) },
                label = { Text(getServiceIntervalTypeDisplayName(type)) },
                colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
                    containerColor = if (selectedType == type) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    },
                    labelColor = if (selectedType == type) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                ),
                modifier = Modifier.wrapContentHeight()
            )
        }
    }
}

@Composable
fun PhotoUploadSection(
    photos: List<String>,
    onAddPhoto: (String) -> Unit,
    onRemovePhoto: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (photos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .clickable { /* TODO: Open photo picker */ },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Tap to add receipt photos",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(photos) { photo ->
                    PhotoItem(
                        photo = photo,
                        onRemove = { onRemovePhoto(photo) }
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoItem(
    photo: String,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: View photo */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Receipt Photo",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
            
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove photo",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun SaveButton(
    onClick: () -> Unit,
    text: String
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.Save,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
fun PhotoFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Add photo",
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun SaveFab(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 8.dp
        )
    ) {
        Icon(
            imageVector = Icons.Default.Save,
            contentDescription = "Save service record",
            modifier = Modifier.size(24.dp)
        )
    }
}

fun getServiceIntervalTypeDisplayName(intervalType: ServiceIntervalType): String {
    return when (intervalType) {
        ServiceIntervalType.DAYS -> "Days"
        ServiceIntervalType.WEEKS -> "Weeks"
        ServiceIntervalType.MONTHS -> "Months"
        ServiceIntervalType.YEARS -> "Years"
    }
}
