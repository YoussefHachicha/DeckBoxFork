package app.deckbox.features.cards.public.paging

import app.cash.paging.PagingSource
import app.deckbox.core.model.Card
import app.deckbox.features.cards.public.model.CardQuery

abstract class CardPagingSource : PagingSource<Int, Card>()

interface CardPagingSourceFactory {
  fun create(query: CardQuery): CardPagingSource
}
