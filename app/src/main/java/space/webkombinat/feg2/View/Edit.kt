package space.webkombinat.feg2.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import space.webkombinat.feg2.ViewModel.EditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Edit(
    modifier: Modifier = Modifier,
    vm: EditViewModel,
    id: Long,
    click: () -> Unit
) {
    val uiState by vm.uiState.collectAsState()
    val textName = vm.textNameHolder.collectAsState("")
    val textDesc = vm.textDescHolder.collectAsState("")
    when(uiState) {
        EditViewModel.UiState.Initial -> {}
        EditViewModel.UiState.LoadSuccess -> {
            Column(
                modifier = modifier
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = textName.value,
                    onValueChange = { vm.setName(it)},
                    label = {Text(text = "名前")},
                    modifier = modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ).fillMaxWidth()
                )
                OutlinedTextField(
                    value = textDesc.value,
                    onValueChange = { vm.setDesc(it) },
                    label = {Text(text = "メモ")},
                    modifier = modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ).fillMaxWidth().height(150.dp)
                )

                Row(
                    modifier = modifier.height(60.dp).padding(
                        horizontal = 16.dp,
                        vertical = 8.dp)
                ) {
                    Spacer(modifier = modifier.weight(1f))

                    Button(
                        modifier = modifier.width(120.dp),
                        onClick = {
                        click()
                    }) {
                        Text("キャンセル")
                    }
                    Spacer(modifier = modifier.weight(1f))
                    Button(
                        modifier = modifier.width(120.dp),
                        onClick = {
                        vm.save()
                    }) {
                        Text("保存")
                    }
                    Spacer(modifier = modifier.weight(1f))

                }

                Spacer(modifier = modifier.height(60.dp))

            }
        }
        EditViewModel.UiState.Loading -> {}
    }

    LaunchedEffect(key1 = uiState) {
        when(uiState) {
            EditViewModel.UiState.Initial -> {
                vm.load(id = id)
            }
            EditViewModel.UiState.LoadSuccess -> {}
            EditViewModel.UiState.Loading -> {}
        }
    }

}