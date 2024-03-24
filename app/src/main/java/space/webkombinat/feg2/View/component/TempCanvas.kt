package space.webkombinat.feg2.View.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import space.webkombinat.feg2.Data.Constants.MAX_TEMP
import space.webkombinat.feg2.Data.Constants.MIN_TEMP
import space.webkombinat.feg2.Data.Constants.TEMP_RANGE
import space.webkombinat.feg2.Data.Constants.TEMP_STEP

@Composable
fun TempCanvas(modifier: Modifier = Modifier) {
    BoxWithConstraints {
        val screenHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() }
        val textMeasure = rememberTextMeasurer()
        Canvas(
            modifier = modifier
                .height(screenHeight)
                .width(60.dp),
//                .background(Color.Cyan.copy(alpha = 0.2f)),
            onDraw = {
                val height = size.height
                val one_step_width = height / TEMP_RANGE

                for (
                    temp in MIN_TEMP .. MAX_TEMP step TEMP_STEP
                ) {
                    val yPoint = height - ((temp - MIN_TEMP ) / 10 * one_step_width)
                    val startPoint = Offset(x = 0f, y = yPoint)
                    val endPoint = Offset(x = 20f, y = yPoint)

//                    線の描画
                    drawLine(
                        color = Color.Black,
                        start = startPoint,
                        end = endPoint,
                        strokeWidth = 1.dp.toPx()
                    )
//                    テキストの描画
                    if(temp % 50 == 0) {
                        drawText(
                            textMeasurer = textMeasure,
                            text = "$temp",
                            style = TextStyle(
                                fontSize = 10.sp,
                                textAlign = TextAlign.Start,
                                color = Color.Black
                            ),
                            topLeft = Offset(30f, y = yPoint - 22f)
                        )
                    }
                }
            }
        )
    }
}