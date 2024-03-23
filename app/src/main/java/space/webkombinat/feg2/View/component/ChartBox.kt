package space.webkombinat.feg2.View.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import space.webkombinat.feg2.Data.Constants.CANVAS_WIDTH
import space.webkombinat.feg2.Data.Constants.CHART_MINUTE
import space.webkombinat.feg2.Data.Constants.ONE_MINUTE_WIDTH

@Composable
fun ChartBox(modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier =
        modifier.horizontalScroll(rememberScrollState())
    ) {
        val screenHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() }
        val screenWidth = with(LocalDensity.current) { CANVAS_WIDTH.toDp()}
        val textMeasure = rememberTextMeasurer()
        Canvas(
            modifier = modifier
                .height(screenHeight)
                .width(screenWidth),
            onDraw = {
                TimeMemoryAndText(textMeasure = textMeasure)
            }
        )
    }
}

fun DrawScope.TimeMemoryAndText(textMeasure: TextMeasurer){
    val height = size.height

    for(i in 1..CHART_MINUTE){
        val xPosition = ONE_MINUTE_WIDTH * i
        val startPoint = Offset(x = xPosition, y = height)
        val endPoint = Offset(x = xPosition, y = height - 20f)
        val minutesToString = (i).toString().padStart(2, '0')
        //          時間メモリ線描画
        drawLine(
            color = Color.Black,
            start = startPoint,
            end = endPoint,
            strokeWidth = 2.5f
        )
//          時間テキスト描画
        drawText(
            textMeasurer = textMeasure,
            text = minutesToString,
            style = TextStyle(
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                color = Color.Black
            ),
            topLeft = Offset(x = xPosition - 17f, y = height - 60f)
        )
    }
}