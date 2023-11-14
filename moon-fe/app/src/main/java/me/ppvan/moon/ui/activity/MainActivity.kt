package me.ppvan.moon.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.ppvan.moon.services.MoonMediaService
import me.ppvan.moon.services.PermissionsManager
import me.ppvan.moon.ui.Nav.graphs.AlbumGraph
import me.ppvan.moon.ui.Nav.graphs.ArtistGraph
import me.ppvan.moon.ui.theme.MoonTheme
import me.ppvan.moon.ui.view.AlbumView
import me.ppvan.moon.ui.view.ArtistView
import me.ppvan.moon.ui.view.DownloadView
import me.ppvan.moon.ui.view.HomeView
import me.ppvan.moon.ui.view.SettingView
import me.ppvan.moon.ui.view.nowplaying.NowPlayingView
import me.ppvan.moon.ui.viewmodel.AlbumViewModel


import me.ppvan.moon.ui.viewmodel.YTViewModel
import me.ppvan.moon.utils.DownloadUtils

import me.ppvan.moon.utils.FadeTransition
import me.ppvan.moon.utils.ScaleTransition
import me.ppvan.moon.utils.SlideTransition
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var permissionsManager: PermissionsManager

    @Inject
    lateinit var ytViewModel: YTViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        permissionsManager.handle(this)
//        startPlayerService()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            MoonTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                  MoonApp(activity = this)
                }

            }
        }

        startPlayerService()
        permissionsManager.handle(this)
        DownloadUtils.init(applicationContext)
    }

    private fun startPlayerService() {
        lifecycleScope.launch(Dispatchers.Main) {
            val serviceIntent = Intent(this@MainActivity, MoonMediaService::class.java)
            startService(serviceIntent)
        }
    }

}

data class ViewContext(
    val navigator: NavHostController,
    val activity: Activity,
)

@Composable
fun MoonApp(activity: Activity, navController: NavHostController = rememberNavController()) {

    val context = ViewContext(navigator = navController, activity = activity)

    NavHost(navController = navController, startDestination = Routes.Home.name) {
        AlbumGraph(context)
        composable(
            Routes.Home.name,
            enterTransition = { FadeTransition.enterTransition() }
        ) {
            HomeView(context = context)
        }
        composable(
            Routes.NowPlaying.name,
            enterTransition = { SlideTransition.slideUp.enterTransition() },
            exitTransition = { FadeTransition.exitTransition() },
            popEnterTransition = { FadeTransition.enterTransition() },
            popExitTransition = { SlideTransition.slideDown.exitTransition() },
        ) {
            NowPlayingView(context = context, )
        }
        ArtistGraph(context)


        composable(
            Routes.Settings.name,
            enterTransition = { ScaleTransition.scaleDown.enterTransition() },
            exitTransition = { ScaleTransition.scaleUp.exitTransition() },
        ) {
            SettingView(context)
        }

        composable(
            Routes.Download.name,
            enterTransition = { ScaleTransition.scaleDown.enterTransition() },
            exitTransition = { ScaleTransition.scaleUp.exitTransition() },
        ) {
            DownloadView(context)
        }
    }
}


enum class Routes() {
    Home, NowPlaying, Album, Artist, Playlist, Settings, Download
}
