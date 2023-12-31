package me.ppvan.moon.ui.view

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import me.ppvan.moon.data.model.Track
import me.ppvan.moon.ui.activity.Routes
import me.ppvan.moon.ui.activity.ViewContext
import me.ppvan.moon.ui.component.CenterTopAppBar
import me.ppvan.moon.ui.component.CenterTopAppBarAction
import me.ppvan.moon.ui.view.home.BottomPlayer
import me.ppvan.moon.ui.view.home.LibraryPage
import me.ppvan.moon.ui.view.home.ProfilePage
import me.ppvan.moon.ui.view.home.SearchBar
import me.ppvan.moon.ui.view.home.SearchPage
import me.ppvan.moon.utils.ScaleTransition
import me.ppvan.moon.utils.SlideTransition


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    context: ViewContext,
) {
    var selectedTab = context.selectedTab
    val trackViewModel = context.trackViewModel
    val ytViewModel = context.ytViewModel
    val player = trackViewModel.player
    val playbackState by player.playbackState.collectAsState()
    val bottomPlayerVisible = playbackState.track != Track.default()
    val query by  ytViewModel.searchQuery.collectAsState()
    val recommendations by  ytViewModel.recommendations.collectAsState()
    val active by  ytViewModel.active.collectAsState()
    val resultItems by  ytViewModel.searchResult.collectAsState()


    Scaffold(
        topBar = {
            Crossfade(targetState = selectedTab, label = "top-bar-page") { page ->
                when (page.value) {
                    MoonPages.Search -> {
                        var isOpenSearchBar by remember { mutableStateOf(false) }
                        if (!isOpenSearchBar) {
                            CenterTopAppBarAction(
                                title = page.value.label,
                                navigationIcon = {
                                    IconButton(onClick = {isOpenSearchBar = true }) {
                                        Icon(imageVector = Icons.Default.Search, contentDescription = "OpenSearchBar")
                                    }
                                },
                                actions = {}
                            )
                        } else {
                            SearchBar(query = query, viewModel = ytViewModel, active = active, recommendations) {
                                isOpenSearchBar = false
                            }
                        }
                    }

                    else -> {
                        CenterTopAppBar(
                            title = page.value.label,
                            menuItems = {
                                DropdownMenuItem(
                                    text = { Text("ReScan") },
                                    onClick = { context.trackViewModel.reloadTracks() },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.Refresh,
                                            contentDescription = "ReScan"
                                        )
                                    }
                                )

                                DropdownMenuItem(
                                    text = { Text("Setting") },
                                    onClick = { context.navigator.navigate(route = Routes.Settings.name) },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Outlined.Settings,
                                            contentDescription = "Settings"
                                        )
                                    }
                                )
                            }
                        )
                    }
                }
            }
        },
        bottomBar = {
            Column {

                AnimatedContent(
                    targetState = bottomPlayerVisible,
                    label = "player",
                    transitionSpec = {
                        SlideTransition.slideUp.enterTransition()
                            .togetherWith(SlideTransition.slideDown.exitTransition())
                    }
                ) { visible ->
                    if (visible) {
                        BottomPlayer(
                            playbackState = playbackState,
                            onPausePlayClick = { player.playPause() },
                            onNextClick = { player.next() },
                            onClick = { context.navigator.navigate(Routes.NowPlaying.name) }
                        )
                    }
                }


                NavigationBar {
                    for (tab in MoonPages.values()) {
                        NavigationBarItem(
                            icon = { Icon(tab.icon, contentDescription = tab.label) },
                            label = { Text(tab.label) },
                            selected = selectedTab.value == tab,
                            onClick = { context.updateSelectedTab(tab) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = selectedTab,
            label = "home-page",
            modifier = Modifier.padding(innerPadding),
            transitionSpec = {
                SlideTransition.slideUp.enterTransition()
                    .togetherWith(ScaleTransition.scaleDown.exitTransition())
            }
        ) { page ->
            when (page.value) {
                MoonPages.Library -> LibraryPage(context)
                MoonPages.Search -> SearchPage(context)
                MoonPages.Profile -> ProfilePage(context)
                else -> {
                    Text(text = "Not implemented")
                }
            }
        }
    }
}



enum class MoonPages(val label: String, val icon: ImageVector) {
    Library("Library", Icons.Filled.LibraryMusic),
    Search("Search", Icons.Filled.Search),
    Profile("Profile", Icons.Filled.Person),
}


