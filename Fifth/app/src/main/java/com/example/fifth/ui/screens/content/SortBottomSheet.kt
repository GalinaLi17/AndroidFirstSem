package com.example.fifth.ui.screens.content

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.fifth.R
import com.example.fifth.ui.viewmodel.SortType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    onDismissRequest: () -> Unit,
    onSortSelected: (SortType) -> Unit,
    currentSortType: SortType
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.sort_dialog_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SortOption.entries.forEach { sortOption ->
                SortItem(
                    sortOption = sortOption,
                    isSelected = currentSortType == sortOption.type,
                    onClick = {
                        onSortSelected(sortOption.type)
                        onDismissRequest()
                    }
                )
            }
        }
    }
}

@Composable
fun SortItem(
    sortOption: SortOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = when (sortOption.type) {
                SortType.BY_TITLE -> stringResource(R.string.sort_by_title)
                SortType.BY_DATE_NEWEST -> stringResource(R.string.sort_by_date_newest)
                SortType.BY_DATE_OLDEST -> stringResource(R.string.sort_by_date_oldest)
                SortType.BY_RATING_HIGH -> stringResource(R.string.sort_by_rating_high)
                SortType.BY_RATING_LOW -> stringResource(R.string.sort_by_rating_low)
            },
            style = MaterialTheme.typography.bodyLarge
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Выбрано",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

enum class SortOption(val type: SortType) {
    BY_TITLE(SortType.BY_TITLE),
    BY_DATE_NEWEST(SortType.BY_DATE_NEWEST),
    BY_DATE_OLDEST(SortType.BY_DATE_OLDEST),
    BY_RATING_HIGH(SortType.BY_RATING_HIGH),
    BY_RATING_LOW(SortType.BY_RATING_LOW)
}