package space.webkombinat.feg2.View.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusPanel(
    str: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
        .height(100.dp)
        .fillMaxWidth(),
//             .background(Color.Black.copy(alpha = 0.3f)),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom
    ){
        Text(
            text = str,
            fontSize = 70.sp,
            fontStyle = FontStyle.Italic,
            color = Color.Gray
        )
        Spacer(modifier = modifier.width(10.dp))
    }
}