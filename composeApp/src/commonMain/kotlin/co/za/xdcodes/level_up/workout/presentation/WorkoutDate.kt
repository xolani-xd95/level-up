//package co.za.xdcodes.level_up.workout.presentation
//
//import androidx.compose.material3.DatePicker
//import androidx.compose.material3.DatePickerDialog
//import androidx.compose.material3.DisplayMode
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.material3.rememberDatePickerState
//import androidx.compose.runtime.Composable
//
//@Composable
//fun WorkoutDate(
//    onDateSelected: (Long?) -> Unit,
//    onDismiss: () -> Unit
//) {
//    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)
////    DatePickerDialog(
////        onDismissRequest = {},
////        confirmButton = {
////            TextButton(onClick = {
////                onDateSelected(datePickerState.selectedDateMillis)
////                onDismiss()
////            }) {
////                Text("OK")
////            }
////        },
////        dismissButton = {
////            TextButton(onClick = onDismiss) {
////                Text("Cancel")
////            }
////        }
////    ) {
////        DatePicker(state = datePickerState)
////    }
//}
