package pages



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app1.DatabaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@Composable
fun RateDialog(
    showRateDialog: MutableState<Boolean>,
    rate: MutableState<Int>,
    rateEvent: () -> Unit,
    isLoading: MutableState<Boolean>,
    onRateConfirmed: (Int) -> Unit

) {

    val interactionSource = remember { MutableInteractionSource() }

    val firestore = FirebaseFirestore.getInstance()
    val databaseService = remember { DatabaseService(firestore) }
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        modifier = Modifier
            .clip(
                RoundedCornerShape(20.dp)
            ),
        onDismissRequest = {},

        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
                    .background(Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ){
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Rate this event?",
                            style = TextStyle(
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround

                        ) {
                            for (i in 1..5){
                                Icon(
                                    imageVector =
                                    if(rate.value >= i) Icons.Filled.Star
                                    else Icons.Filled.AddCircle,
                                    contentDescription = "",
                                    tint =
                                    if(rate.value >= i) Color(0xFFFFC107)
                                    else Color(0xFF757575),
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = null
                                        ) {
                                            rate.value = i
                                        }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                        Button(

                            onClick = {
                                isLoading.value = true
                                rateEvent()
                                onRateConfirmed(rate.value)
                                val points = 10
                                coroutineScope.launch {
                                    val result = databaseService.addPoints(uid!!, points)

                                    isLoading.value = false
                                }
                                showRateDialog.value = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6200EA),
                                contentColor = Color.Black,
                                disabledContainerColor = Color(0xFFD3D3D3),
                                disabledContentColor = Color.White,
                            ),
                        ) {
                            if (isLoading.value) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Potvrdi",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = "Zatvori",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    showRateDialog.value = false
                                },
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color(0xFF757575),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
        },
        dismissButton = {
        }
    )
}

