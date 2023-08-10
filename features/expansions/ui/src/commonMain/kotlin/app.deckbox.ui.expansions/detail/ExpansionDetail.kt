package app.deckbox.ui.expansions.detail

import DeckBoxAppBar
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import app.deckbox.common.compose.overlays.showInFullScreen
import app.deckbox.common.compose.util.Palette
import app.deckbox.common.compose.util.generateColorPalette
import app.deckbox.common.compose.widgets.PokeballLoadingIndicator
import app.deckbox.common.compose.widgets.PokemonCardGrid
import app.deckbox.common.screens.CardDetailScreen
import app.deckbox.common.screens.ExpansionDetailScreen
import app.deckbox.core.di.MergeActivityScope
import app.deckbox.core.logging.bark
import com.r0adkll.kotlininject.merge.annotations.CircuitInject
import com.seiko.imageloader.asImageBitmap
import com.seiko.imageloader.model.ImageResult
import com.seiko.imageloader.rememberImageAction
import com.slack.circuit.overlay.LocalOverlayHost
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@CircuitInject(MergeActivityScope::class, ExpansionDetailScreen::class)
@Composable
internal fun ExpansionDetail(
  state: ExpansionDetailUiState,
  modifier: Modifier = Modifier,
) {
  val coroutineScope = rememberCoroutineScope()
  val overlayHost = LocalOverlayHost.current

  val scrollState = rememberLazyGridState()
  val isScrolled by remember {
    derivedStateOf {
      scrollState.firstVisibleItemIndex > 0 ||
        (scrollState.firstVisibleItemIndex == 0 && scrollState.firstVisibleItemScrollOffset > 0)
    }
  }

  val elevation by animateDpAsState(
    if (isScrolled) 8.dp else 0.dp,
  )

  Scaffold(
    topBar = {
      if (state is ExpansionDetailUiState.Loaded) {
        val logoAction by rememberImageAction(state.expansion.images.logo)
        var logoPalette by remember { mutableStateOf<Palette?>(null) }

        LaunchedEffect(logoAction) {
          bark { "Logo ImageAction: $logoAction" }
          if (logoAction is ImageResult.Bitmap) {
            logoPalette = generateColorPalette((logoAction as ImageResult.Bitmap).bitmap.asImageBitmap())
            bark { "Palette generated: $logoPalette" }
          }
        }

        val appBarColors = if (logoPalette != null) {
          TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(logoPalette!!.secondary))
        } else {
          TopAppBarDefaults.smallTopAppBarColors()
        }

        DeckBoxAppBar(
          title = state.expansion.name,
          navigationIcon = {
            IconButton(
              onClick = {
                state.eventSink(ExpansionDetailUiEvent.NavigateBack)
              },
            ) {
              Icon(Icons.Rounded.ArrowBack, contentDescription = null)
            }
          },
          colors = appBarColors,
          modifier = Modifier.graphicsLayer(shadowElevation = with(LocalDensity.current) { elevation.toPx() }),
        )
      }
    },

    modifier = modifier,
  ) {
    when (state) {
      ExpansionDetailUiState.Loading -> Loading(Modifier.padding(it))
      is ExpansionDetailUiState.Loaded -> PokemonCardGrid(
        cardPager = state.cardsPager,
        state = scrollState,
        onClick = { card ->
          coroutineScope.launch {
            overlayHost.showInFullScreen(CardDetailScreen(card.id))
          }
        },
        contentPadding = PaddingValues(
          start = 16.dp,
          end = 16.dp,
        ),
        modifier = Modifier.padding(it),
      )
    }
  }
}

@Composable
private fun Loading(
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
  ) {
    PokeballLoadingIndicator(
      size = 92.dp,
    )
  }
}
