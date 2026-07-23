package com.artemonre.hireme.portfolio

import androidx.compose.runtime.Composable

@Composable
expect fun PortfolioModal(onDismissRequest: () -> Unit, content: @Composable () -> Unit)
