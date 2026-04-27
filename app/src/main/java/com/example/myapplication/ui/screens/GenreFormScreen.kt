package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.ui.ViewModels.GenreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenreFormScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    vm: GenreViewModel
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) {
        vm.resetCreateState()
    }

    LaunchedEffect(state.isCreateSuccess) {
        if (state.isCreateSuccess) {
            vm.consumeCreateSuccess()
            navController.popBackStack()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Crear género",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.newGenreName,
                onValueChange = vm::onNewGenreNameChange,
                label = { Text("Nombre") },
                singleLine = true,
                isError = state.newGenreNameError != null,
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    val fieldError = state.newGenreNameError
                    if (fieldError != null) {
                        Text(fieldError)
                    }
                }
            )

            val submitError = state.submitErrorMessage
            if (submitError != null) {
                Text(submitError)
            }

            Button(
                enabled = !state.isSubmitting,
                onClick = {
                    vm.createGenre()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (state.isSubmitting) "Guardando..." else "Guardar género")
            }
        }
    }
}

