package me.ppvan.moon.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import me.ppvan.moon.R
import me.ppvan.moon.data.model.Album
import me.ppvan.moon.ui.activity.ViewContext


@Composable
fun AlbumTile(album: Album , context: ViewContext, onClick: () -> Unit) {
    SquareGrooveTile(
        image = ImageRequest.Builder(LocalContext.current)
            .data(data = album.thumbnailUri)
            .error(R.drawable.thumbnail)
            .crossfade(true)
            .build(),
        content = {
            Text(
                album.name,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                album.artist,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        onPlay = {
//            context.symphony.radio.shorty.playQueue(
//                context.symphony.groove.album.getSongIds(album.id)
//            )
        },
        onClick = {
            onClick();

        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SquareGrooveTile(
    image: ImageRequest,
    content: @Composable ColumnScope.() -> Unit,
    onPlay: () -> Unit,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        onClick = onClick
    ) {
        Box(modifier = Modifier.padding(12.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box {
                    AsyncImage(
                        image,
                        null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp)),
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    ) {
                        IconButton(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(12.dp)
                                )
                                .then(Modifier.size(36.dp)),
                            onClick = onPlay
                        ) {
                            Icon(Icons.Filled.PlayArrow, null)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                content()
            }
        }
    }
}
