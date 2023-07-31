package app.deckbox.ui.expansions.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import app.deckbox.common.settings.DeckBoxSettings
import app.deckbox.expansions.ui.ExpansionCardStyle

@Composable
fun DeckBoxSettings.collectExpansionCardStyle(): State<ExpansionCardStyle> {
  return remember { observeExpansionCardStyle() }
    .collectAsState(ExpansionCardStyle.Large)
}
