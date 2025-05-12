package com.agriconnect.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp), // Default for Material 3 is 12.dp, adjust as needed
    large = RoundedCornerShape(16.dp) // Default for Material 3 is 0.dp for top, 16.dp for bottom, adjust as needed
)