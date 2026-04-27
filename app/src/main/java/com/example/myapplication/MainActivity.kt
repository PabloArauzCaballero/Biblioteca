package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.NavScreens
import com.example.myapplication.ui.ViewModels.BooksFormViewModel
import com.example.myapplication.ui.ViewModels.BooksViewModel
import com.example.myapplication.ui.ViewModels.GenreViewModel
import com.example.myapplication.ui.screens.BookDetailScreen
import com.example.myapplication.ui.screens.BookFormScreen
import com.example.myapplication.ui.screens.BookListScreen
import com.example.myapplication.ui.screens.GenreFormScreen
import com.example.myapplication.ui.screens.GenreListScreen
import com.example.myapplication.ui.screens.HomeScreen
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
    listVM: BooksViewModel = BooksViewModel(),
    formVM: BooksFormViewModel = BooksFormViewModel(),
    genreVM: GenreViewModel = GenreViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = NavScreens.HOME.name
    ){
        composable(NavScreens.HOME.name){
            HomeScreen(
                modifier = Modifier,
                navController = navController
            )
        }

        composable(NavScreens.BOOKS_LIST.name){
            BookListScreen(
                modifier = Modifier,
                vm = listVM,
                navController = navController
            )
        }

        composable("${NavScreens.BOOK_DETAIL.name}/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments
                ?.getString("bookId")
                ?.toIntOrNull()

            if (bookId != null) {
                BookDetailScreen(
                    navController = navController,
                    bookId = bookId,
                    formVM = formVM,
                    listVM = listVM
                )
            }
        }

        composable(NavScreens.BOOK_FORM.name) {
            BookFormScreen(
                navController = navController,
                bookId = null,
                formBooksVM = formVM,
                listVM = listVM
            )
        }

        composable("${NavScreens.BOOK_FORM.name}/{bookId}") { backStackEntry ->
            // Obtener el id el libro desde el path
            val bookId = backStackEntry.arguments
                ?.getString("bookId")
                ?.toIntOrNull()

            BookFormScreen(
                navController = navController,
                bookId = bookId,
                formBooksVM = formVM,
                listVM = listVM
            )
        }

        composable(NavScreens.GENRES_LIST.name){
            GenreListScreen(
                modifier = Modifier,
                navController = navController,
                vm = genreVM
            )
        }

        composable(NavScreens.GENRE_EDIT.name) {
            GenreFormScreen(
                navController = navController,
                vm = genreVM
            )
        }

    }
}