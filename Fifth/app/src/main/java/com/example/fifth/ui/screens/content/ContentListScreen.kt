package com.example.fifth.ui.screens.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fifth.R
import com.example.fifth.navigation.Screens
import com.example.fifth.ui.viewmodel.ContentListViewModel
import com.example.fifth.ui.viewmodel.ContentListViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentListScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: ContentListViewModel = viewModel(
        factory = ContentListViewModelFactory(context.applicationContext as com.example.fifth.FifthApplication)
    )

    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var contentToDelete by remember { mutableStateOf<com.example.fifth.data.database.ContentEntity?>(null) }
    var showSearchBar by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showSortSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            scope.launch {
                snackbarHostState.showSnackbar(error)
                viewModel.clearError()
            }
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery != uiState.searchQuery) {
            viewModel.searchContent(searchQuery)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    if (showSearchBar) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text(stringResource(R.string.search_hint)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = {
                                    searchQuery = ""
                                    showSearchBar = false
                                    viewModel.clearSearch()
                                }) {
                                    Icon(
                                        Icons.Outlined.Close,
                                        contentDescription = "Очистить"
                                    )
                                }
                            }
                        )
                    } else {
                        Text(stringResource(R.string.content_list_title))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { showSearchBar = !showSearchBar }) {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = "Поиск"
                        )
                    }

                    IconButton(onClick = { showSortSheet = true }) {
                        Icon(
                            Icons.Outlined.Sort,
                            contentDescription = "Сортировка"
                        )
                    }

                    IconButton(onClick = { navController.navigate(Screens.Profile.route) }) {
                        Icon(
                            Icons.Outlined.MoreVert,
                            contentDescription = "Профиль"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screens.AddContent.route) }
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = "Добавить"
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.contentList.isEmpty()) {
                Text(
                    text = stringResource(R.string.empty_content),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.contentList) { content ->
                        ContentItem(
                            content = content,
                            onDeleteClick = {
                                contentToDelete = content
                                showDeleteDialog = true
                            }
                        )
                    }
                }
            }
        }

        if (showDeleteDialog && contentToDelete != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(stringResource(R.string.confirm_delete)) },
                text = { Text("Вы уверены, что хотите удалить \"${contentToDelete?.title}\"?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            contentToDelete?.let { content ->
                                viewModel.deleteContent(content.id)
                            }
                            showDeleteDialog = false
                            contentToDelete = null
                        }
                    ) {
                        Text("Удалить")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            contentToDelete = null
                        }
                    ) {
                        Text("Отмена")
                    }
                }
            )
        }
    }


    if (showSortSheet) {
        SortBottomSheet(
            onDismissRequest = { showSortSheet = false },
            onSortSelected = { sortType ->
                when (sortType) {
                    com.example.fifth.ui.viewmodel.SortType.BY_TITLE -> viewModel.sortByTitle()
                    com.example.fifth.ui.viewmodel.SortType.BY_DATE_NEWEST -> viewModel.sortByDateNewest()
                    com.example.fifth.ui.viewmodel.SortType.BY_DATE_OLDEST -> viewModel.sortByDateOldest()
                    com.example.fifth.ui.viewmodel.SortType.BY_RATING_HIGH -> viewModel.sortByRatingHigh()
                    com.example.fifth.ui.viewmodel.SortType.BY_RATING_LOW -> viewModel.sortByRatingLow()
                }
            },
            currentSortType = uiState.currentSortType
        )
    }
}

@Composable
fun ContentItem(
    content: com.example.fifth.data.database.ContentEntity,
    onDeleteClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = content.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.width(48.dp)
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Удалить",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = content.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    )
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = String.format(Locale.getDefault(), "★ %.1f", content.rating),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = dateFormat.format(content.addedDate),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}