package com.droidknights.app2023.feature.tvmain

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.droidknights.app2023.core.designsystem.theme.KnightsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TvMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KnightsTheme(darkTheme = true) {
                TvMainScreen()
            }
        }
    }
}
