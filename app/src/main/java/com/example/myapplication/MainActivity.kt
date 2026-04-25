package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.NavScreens
import com.example.myapplication.ui.ViewModels.BooksViewModel
import com.example.myapplication.ui.ViewModels.GenreViewModel
import com.example.myapplication.ui.screens.BookListScreen
import com.example.myapplication.ui.screens.GenreListScreen
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NavigationApp()
            }
        }
    }
}

@Composable
fun NavigationApp(
    navController:NavHostController = rememberNavController(),
    vm: BooksViewModel = BooksViewModel(),
) {
    NavHost(
        navController = navController,
        startDestination = NavScreens.GENRES_LIST.name
    ){
        composable(NavScreens.BOOKS_LIST.name){
            BookListScreen(
                modifier = Modifier,
                vm = vm,
                navController = navController
            )
        }

        composable(NavScreens.GENRES_LIST.name){
            GenreListScreen(
                modifier = Modifier,
                navController = navController
            )
        }

    }
}