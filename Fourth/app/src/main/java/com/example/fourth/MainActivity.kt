package com.example.fourth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.fourth.model.DispatcherType
import com.example.fourth.model.ExceptionType
import com.example.fourth.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val settings by viewModel.settings.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val context = LocalContext.current

    var isExpanded by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val dispatchers = DispatcherType.entries

    val toastMessage = stringResource(R.string.toast_exception)
    val snackbarMessage = stringResource(R.string.snackbar_exception)
    val resetMessage = stringResource(R.string.reset_exception)

    val exceptionHandler: (ExceptionType) -> Unit = { exceptionType ->
        when (exceptionType) {
            ExceptionType.TOAST -> {
                Toast.makeText(
                    context,
                    toastMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
            ExceptionType.SNACKBAR -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = snackbarMessage,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            ExceptionType.RESET -> {
                viewModel.updateCount(Constants.MIN_COROUTINES)
                viewModel.updateDispatcher(DispatcherType.DEFAULT)
                viewModel.toggleSequential()
                viewModel.toggleLazy(false)

                Toast.makeText(
                    context,
                    resetMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.coroutines_count, settings.count),
                style = MaterialTheme.typography.headlineSmall
            )
            Slider(
                value = settings.count.toFloat(),
                onValueChange = { viewModel.updateCount(it.toInt()) },
                valueRange = Constants.MIN_COROUTINES.toFloat()..Constants.MAX_COROUTINES.toFloat(),
                steps = (Constants.MAX_COROUTINES - Constants.MIN_COROUTINES) / Constants.STEP_COROUTINES - 1
            )

            Text(
                text = stringResource(R.string.select_dispatcher),
                style = MaterialTheme.typography.headlineSmall
            )

            ExposedDropdownMenuBox(
                expanded = isExpanded,
                onExpandedChange = { isExpanded = it }
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    readOnly = true,
                    value = when (settings.dispatcherType) {
                        DispatcherType.DEFAULT -> stringResource(R.string.dispatcher_default)
                        DispatcherType.IO -> stringResource(R.string.dispatcher_io)
                        DispatcherType.MAIN -> stringResource(R.string.dispatcher_main)
                        DispatcherType.UNCONFINED -> stringResource(R.string.dispatcher_unconfined)
                    },
                    onValueChange = {},
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = isExpanded,
                    onDismissRequest = { isExpanded = false }
                ) {
                    dispatchers.forEach { dispatcher ->
                        DropdownMenuItem(
                            text = {
                                Text(when (dispatcher) {
                                    DispatcherType.DEFAULT -> stringResource(R.string.dispatcher_default)
                                    DispatcherType.IO -> stringResource(R.string.dispatcher_io)
                                    DispatcherType.MAIN -> stringResource(R.string.dispatcher_main)
                                    DispatcherType.UNCONFINED -> stringResource(R.string.dispatcher_unconfined)
                                })
                            },
                            onClick = {
                                viewModel.updateDispatcher(dispatcher)
                                isExpanded = false
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.sequential))
                    Switch(
                        checked = settings.isSequential,
                        onCheckedChange = { if (it) viewModel.toggleSequential() }
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.parallel))
                    Switch(
                        checked = settings.isParallel,
                        onCheckedChange = { if (it) viewModel.toggleParallel() }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.lazy_launch))
                    Switch(
                        checked = settings.isLazy,
                        onCheckedChange = viewModel::toggleLazy
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.background_work))
                    Switch(
                        checked = settings.isBackgroundEnabled,
                        onCheckedChange = viewModel::toggleBackground
                    )
                }
            }

            if (isRunning) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val cancelled = viewModel.cancelAllCoroutines()
                            Toast.makeText(
                                context,
                                context.resources.getString(R.string.coroutines_cancelled, cancelled),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Text(stringResource(R.string.cancel_coroutines_count, settings.count))
                    }
                }
            } else {
                Button(
                    onClick = {
                        viewModel.launchCoroutines(
                            onException = exceptionHandler,
                            onComplete = {
                                Toast.makeText(
                                    context,
                                    context.resources.getString(R.string.all_coroutines_completed, settings.count),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                ) {
                    Text(stringResource(R.string.launch_coroutines))
                }
            }
        }
    }
}