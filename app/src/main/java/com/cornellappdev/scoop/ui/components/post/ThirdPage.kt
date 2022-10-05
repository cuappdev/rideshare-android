package com.cornellappdev.scoop.ui.components.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.cornellappdev.scoop.R
import com.cornellappdev.scoop.data.models.Ride

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ThirdPage(ride: Ride) {
    BottomSheetScaffold(
        sheetPeekHeight = 200.dp,
        sheetShape = RoundedCornerShape(25.dp),
        sheetContent = { BottomSheet(ride) }
    ) {
        Image(
            painter = painterResource(R.drawable.ic_fake_map),
            contentDescription = "Map showing departure to arrival locations"
        )
    }
}