package app.deckbox.features.cards.impl.paging

import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState
import app.deckbox.core.di.AppScope
import app.deckbox.core.di.MergeAppScope
import app.deckbox.core.model.Card
import app.deckbox.features.cards.public.db.CardDao
import app.deckbox.features.cards.public.model.CardQuery
import app.deckbox.features.cards.public.paging.CardPagingSource
import app.deckbox.features.cards.public.paging.CardPagingSourceFactory
import app.deckbox.network.PokemonTcgApi
import com.r0adkll.kotlininject.merge.annotations.ContributesBinding
import me.tatarka.inject.annotations.Inject

@Inject
@AppScope
@ContributesBinding(MergeAppScope::class)
class NetworkCardPagingSourceFactory(
  private val api: PokemonTcgApi,
  private val db: CardDao,
) : CardPagingSourceFactory {

  override fun create(query: CardQuery): CardPagingSource {
    return NetworkCardPagingSource(api, db, query)
  }
}

class NetworkCardPagingSource(
  private val api: PokemonTcgApi,
  private val db: CardDao,
  private val query: CardQuery,
) : CardPagingSource() {

  override fun getRefreshKey(state: PagingState<Int, List<Card>>): Int? = null

  override suspend fun load(params: PagingSourceLoadParams<Int>): PagingSourceLoadResult<Int, Card> {
    val adjustedQuery = query.copy(pageSize = params.loadSize)
    val result = api.getCards(adjustedQuery.asQueryOptions())
    return if (result.isSuccess) {
      val response = result.getOrThrow()

      // Cache response
      db.insert(response.data)

      PagingSourceLoadResultPage(
        data = response.data,
        prevKey = (response.page - 1).takeIf { it >= 0 },
        nextKey = response.page + 1,
      ) as PagingSourceLoadResult<Int, Card>
    } else {
      PagingSourceLoadResultError<Int, Card>(result.exceptionOrNull() ?: Exception("Unable to load cards"))
        as PagingSourceLoadResult<Int, Card>
    }
  }
}
