package com.cornellappdev.scoop.ui.components.post

import android.widget.EditText
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cornellappdev.scoop.R
import com.cornellappdev.scoop.models.Trip
import com.cornellappdev.scoop.ui.components.general.BuildMessage
import com.cornellappdev.scoop.ui.components.general.DenseTextField
import com.cornellappdev.scoop.ui.theme.Gray
import com.cornellappdev.scoop.ui.theme.PlaceholderGray
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SecondPage(onProceedClicked: () -> Unit, tripState: MutableState<Trip>) {
    val dateFormatter = SimpleDateFormat(stringResource(R.string.date_format), Locale.getDefault())
    val timeFormatter = SimpleDateFormat(stringResource(R.string.time_format), Locale.getDefault())
    val dateAndTimeFormatter =
        SimpleDateFormat(stringResource(R.string.date_time_format), Locale.getDefault())
    val detailsText = rememberSaveable { mutableStateOf(tripState.value.otherDetails.orEmpty()) }
    val lowerRangeNumTravelers =
        rememberSaveable { mutableStateOf((tripState.value.lowerRangeNumTravelers ?: 1)) }
    val higherRangeNumTravelers =
        rememberSaveable { mutableStateOf((tripState.value.higherRangeNumTravelers ?: 1)) }
    val dateText = rememberSaveable { mutableStateOf(tripState.value.dateOfTrip.orEmpty()) }
    val timeText = rememberSaveable { mutableStateOf(tripState.value.timeOfTrip.orEmpty()) }
    var showInvalidRangeMessage by rememberSaveable { mutableStateOf(false) }
    var showInvalidDateMessage by rememberSaveable { mutableStateOf(false) }
    var showInvalidTimeMessage by rememberSaveable { mutableStateOf(false) }
    var proceedEnabled by rememberSaveable { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // Stops displaying the given message to the user after a delay.
    suspend fun disableMessage() {
        delay(3000L)
        when {
            showInvalidRangeMessage -> {
                showInvalidRangeMessage = false
                proceedEnabled = true
            }
            showInvalidDateMessage -> {
                showInvalidDateMessage = false
                proceedEnabled = true
            }
            showInvalidTimeMessage -> {
                showInvalidTimeMessage = false
                proceedEnabled = true
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(horizontal = 37.dp)
        ) {
            NumberOfTravelersSection(lowerRangeNumTravelers, higherRangeNumTravelers, tripState)
            DateOfTripSection(dateText, dateFormatter, tripState)
            TimeOfTripSection(timeText, timeFormatter, tripState)
            OtherDetailsSection(detailsText, tripState)

            Column(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 38.dp)
                    .wrapContentSize()
            ) {
                Button(
                    modifier = Modifier
                        .size(56.dp),
                    shape = RoundedCornerShape(30.dp),
                    enabled = proceedEnabled,
                    onClick = {
                        when {
                            lowerRangeNumTravelers.value > higherRangeNumTravelers.value -> {
                                showInvalidRangeMessage = true
                                proceedEnabled = false
                                coroutineScope.launch {
                                    disableMessage()
                                }
                            }
                            dateText.value.isEmpty() -> {
                                showInvalidDateMessage = true
                                proceedEnabled = false
                                coroutineScope.launch {
                                    disableMessage()
                                }
                            }
                            timeText.value.isEmpty() || dateAndTimeFormatter.parse("$dateText $timeText")
                                ?.before(Date()) == true
                            -> {
                                showInvalidTimeMessage = true
                                proceedEnabled = false
                                coroutineScope.launch {
                                    disableMessage()
                                }
                            }
                            else -> {
                                onProceedClicked()
                            }
                        }
                    },
                    contentPadding = PaddingValues(10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Gray)
                ) {
                    Icon(
                        Icons.Outlined.ArrowForward,
                        contentDescription = stringResource(R.string.arrow_forward_description)
                    )
                }
            }
        }

        Box {
            BuildMessage(
                showInvalidRangeMessage,
                stringResource(R.string.invalid_range)
            )
            BuildMessage(showInvalidDateMessage, stringResource(R.string.invalid_date))
            BuildMessage(showInvalidTimeMessage, stringResource(R.string.invalid_time))
        }
    }
}

@Composable
fun NumberOfTravelersSection(
    lowerRangeNumTravelers: MutableState<Int>,
    higherRangeNumTravelers: MutableState<Int>,
    tripState: MutableState<Trip>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.num_of_travelers),
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row {
            Icon(
                Icons.Outlined.Group,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterVertically),
                contentDescription = stringResource(R.string.travelers_icon_description)
            )
            NumberPicker(
                state = lowerRangeNumTravelers,
                modifier = Modifier.padding(start = 13.dp),
                range = 1..10,
            ) {
                tripState.value = tripState.value.copy(
                    lowerRangeNumTravelers = it
                )
            }
            Text(
                text = stringResource(R.string.to),
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .align(Alignment.Bottom),
                fontSize = 22.sp
            )
            NumberPicker(
                state = higherRangeNumTravelers,
                range = 1..10,
            ) {
                tripState.value = tripState.value.copy(
                    higherRangeNumTravelers = it
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DateOfTripSection(
    dateText: MutableState<String>,
    dateFormatter: SimpleDateFormat,
    tripState: MutableState<Trip>
) {
    val datePickerDialog = createDatePickerDialog(LocalContext.current, dateFormatter) {
        dateText.value = it
        tripState.value = tripState.value.copy(
            dateOfTrip = it
        )
    }
    Column(
        modifier = Modifier
            .width(200.dp)
            .padding(top = 30.dp)
    ) {
        Text(
            text = stringResource(R.string.date_of_trip),
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row {
            Icon(
                Icons.Outlined.CalendarToday,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(32.dp)
                    .align(Alignment.CenterVertically),
                contentDescription = stringResource(R.string.calendar_icon_description)
            )
            TextButton(
                modifier = Modifier.align(Alignment.Bottom),
                contentPadding = PaddingValues(
                    all = 0.dp
                ),
                onClick = { datePickerDialog.show() }) {
                Column {
                    if (dateText.value.isBlank()) {
                        Text(
                            stringResource(R.string.date_template),
                            style = TextStyle(color = PlaceholderGray, fontSize = 22.sp),
                        )
                    } else {
                        Text(
                            dateText.value, style = TextStyle(color = Color.Black, fontSize = 22.sp),
                        )
                    }

                    Divider(
                        modifier = Modifier.padding(top = 4.dp),
                        color = Color.Black,
                        thickness = 2.dp
                    )
                }
            }
        }
    }
}

@Composable
fun TimeOfTripSection(
    timeText: MutableState<String>,
    timeFormatter: SimpleDateFormat,
    tripState: MutableState<Trip>
) {
    val timePickerDialog = createTimePickerDialog(LocalContext.current, timeFormatter) {
        timeText.value = it
        tripState.value = tripState.value.copy(
            timeOfTrip = it
        )
    }

    Column(
        modifier = Modifier
            .width(200.dp)
            .padding(top = 30.dp)
    ) {
        Text(
            text = stringResource(R.string.time_of_trip),
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row {
            Icon(
                Icons.Outlined.Schedule,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(32.dp)
                    .align(Alignment.CenterVertically),
                contentDescription = stringResource(R.string.clock_icon_description)
            )
            TextButton(
                modifier = Modifier.align(Alignment.Bottom),
                contentPadding = PaddingValues(
                    all = 0.dp
                ),
                onClick = { timePickerDialog.show() }) {
                Column {
                    if (timeText.value.isBlank()) {
                        Text(
                            stringResource(R.string.time_template),
                            style = TextStyle(color = PlaceholderGray, fontSize = 22.sp),
                        )
                    } else {
                        Text(
                            timeText.value, style = TextStyle(color = Color.Black, fontSize = 22.sp),
                        )
                    }

                    Divider(
                        modifier = Modifier.padding(top = 4.dp),
                        color = Color.Black,
                        thickness = 2.dp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OtherDetailsSection(
    detailsText: MutableState<String>,
    tripState: MutableState<Trip>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
    ) {
        Text(
            text = stringResource(R.string.other_details),
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row {
            Icon(
                painterResource(R.drawable.ic_details_icon),
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(32.dp)
                    .align(Alignment.CenterVertically),
                contentDescription = stringResource(R.string.details_icon_description)
            )
            DenseTextField(
                text = detailsText,
                placeholderText = stringResource(R.string.enter_details),
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .fillMaxWidth()
            ) {
                tripState.value = tripState.value.copy(
                    otherDetails = it
                )
            }
        }
    }
}