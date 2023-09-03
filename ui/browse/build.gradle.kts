plugins {
  id("app.deckbox.ui")
}

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.features.cards.public)
        implementation(projects.features.decks.public)
        implementation(projects.features.boosterpacks.public)
        implementation(projects.ui.filter)
      }
    }
  }
}
