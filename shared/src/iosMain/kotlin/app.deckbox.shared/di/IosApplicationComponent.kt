// Copyright 2023, Google LLC, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0

package app.deckbox.shared.di

import app.deckbox.core.app.ApplicationInfo
import app.deckbox.core.app.Flavor
import app.deckbox.core.di.AppScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults

@Component
@AppScope
abstract class IosApplicationComponent() : SharedAppComponent {

    @AppScope
    @Provides
    fun provideApplicationId(): ApplicationInfo = ApplicationInfo(
        packageName = NSBundle.mainBundle.bundleIdentifier ?: "app.tivi.client",
        debugBuild = Platform.isDebugBinary,
        flavor = Flavor.Standard,
        versionName = NSBundle.mainBundle.infoDictionary
            ?.get("CFBundleShortVersionString") as? String
            ?: "",
        versionCode = (
            NSBundle.mainBundle.infoDictionary
                ?.get("CFBundleVersion") as? String
            )
            ?.toIntOrNull()
            ?: 0,
    )

    @Provides
    fun provideNsUserDefaults(): NSUserDefaults = NSUserDefaults.standardUserDefaults

    companion object
}
