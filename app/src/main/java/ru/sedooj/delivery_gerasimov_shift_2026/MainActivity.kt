package ru.sedooj.delivery_gerasimov_shift_2026

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import ru.sedooj.delivery_gerasimov_shift_2026.presentation.calculator.CalculatorRoute
import ru.sedooj.delivery_gerasimov_shift_2026.ui.theme.Deliverygerasimovshift2026Theme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Deliverygerasimovshift2026Theme {
                CalculatorRoute()
            }
        }
    }
}
