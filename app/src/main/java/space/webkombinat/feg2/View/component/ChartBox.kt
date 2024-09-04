package space.webkombinat.feg2.View.component

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import space.webkombinat.feg2.Data.Constants.CANVAS_WIDTH
import space.webkombinat.feg2.Data.Constants.CHART_MINUTE
import space.webkombinat.feg2.Data.Constants.ONE_MINUTE_WIDTH
import space.webkombinat.feg2.R
import space.webkombinat.feg2.Service.Line
import space.webkombinat.feg2.ViewModel.ChartViewModel
import kotlin.math.roundToInt

@Composable
fun ChartBox(
    modifier: Modifier = Modifier,
    clack: MutableState<Pair<Int?, Int?>>,
    tempList: SnapshotStateList<Line>,
    tempList_BT: SnapshotStateList<Line>,
    bottomShow: MutableState<Boolean>,
    font_size: Int = 10,
    vm: ChartViewModel?,
) {
    BoxWithConstraints(
        modifier =
        modifier.horizontalScroll(rememberScrollState())
    ) {
        val screenHeight = with(LocalDensity.current) { constraints.maxHeight.toDp() }
        val screenWidth = with(LocalDensity.current) { CANVAS_WIDTH.toDp()}
        val textMeasure = rememberTextMeasurer()
        val clack_f = clack.value.first
        val clack_s = clack.value.second

        val context = LocalContext.current
        val image = vectorToImageBitmap(
            context = context,
            id = R.drawable.bg2
        )

        val color = MaterialTheme.colorScheme.primary
        val color_ET = Color(0xFFDC5785)
        val color_BT = Color(0xFF548DB1)

        Canvas(
            modifier = modifier
                .height(screenHeight)
                .width(screenWidth),
            onDraw = {
                drawImage(
                    image = image!!,
                    topLeft = Offset(x = 0f, y = (screenHeight - 512f.dp + 10f.dp).toPx())
                )
                if (vm != null) {
                    LineChart(
                        tempList = vm.chartList,
                        color = color_ET.copy(0.3f)
                    )
                    LineChart(
                        tempList = vm.chartList_BT,
                        color = color_BT.copy(0.3f)
                    )
                    if (vm.clack.value.first != null){
                        tag(
                            color = Color(0xff7f7fff).copy(alpha = 0.3f),
                            position = Pair(vm.clack.value.first!! * 5f, (screenHeight - 60.dp).toPx()),
                            clack = false
                        )
                    }
                    if (vm.clack.value.second != null){
                        tag(
                            color = Color(0xffff7f7f).copy(alpha = 0.3f),
                            position = Pair(vm.clack.value.second!! * 5f, (screenHeight - 60.dp).toPx()),
                            clack = true
                        )
                    }
                }
                LineChart(
                    tempList = tempList,
                    color = color_ET
                )
                LineChart(
                    tempList = tempList_BT,
                    color = color_BT
                )
                if (clack_f != null){
                    tag(
                        color = Color(0xff7f7fff),
                        position = Pair(clack_f * 5f, (screenHeight - 60.dp).toPx()),
                        clack = false
                    )
                }
                if (clack_s != null){
                    tag(
                        color = Color(0xffff7f7f),
                        position = Pair(clack_s * 5f, (screenHeight - 60.dp).toPx()),
                        clack = true
                    )
                }
            }
        )

        Column(
            modifier = modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Bottom
        ) {
            MoveAnim(
                show = bottomShow.value
            ) {
                Column {
                    Canvas(
                        modifier = modifier
                            .height(30.dp)
                            .width(screenWidth),
                        onDraw = {
                            TimeMemoryAndText(
                                textMeasure = textMeasure,
                                color = color,
                                font_size = font_size
                            )
                        }
                    )
                    Box(modifier = modifier.height(60.dp))
                }
            }
        }
    }
}

fun DrawScope.LineChart(
    tempList: SnapshotStateList<Line>,
    color: Color
){
    if (tempList.isNotEmpty()){
        tempList.forEach{ line ->
            drawLine(
                color = color,
                start = line.start,
                end = line.end,
                strokeWidth = line.strokeWidth.toPx()
            )
        }
    }
}

fun DrawScope.TimeMemoryAndText(
    textMeasure: TextMeasurer,
    color: Color,
    font_size: Int,
){
    val height = size.height

    for(i in 1..CHART_MINUTE){
        val xPosition = ONE_MINUTE_WIDTH * i
        val startPoint = Offset(x = xPosition, y = height)
        val endPoint = Offset(x = xPosition, y = height - 20f)
        val minutesToString = (i).toString().padStart(2, '0')
        //          時間メモリ線描画
        drawLine(
            color = color,
            start = startPoint,
            end = endPoint,
            strokeWidth = 2.5f
        )

        //          時間テキスト描画
        drawText(
            textMeasurer = textMeasure,
            text = minutesToString,
            style = TextStyle(
                fontSize = font_size.sp,
                textAlign = TextAlign.Center,
                color = color
            ),
            topLeft = Offset(x = xPosition - 17f, y = height - 60f)
        )
    }
}

fun DrawScope.tag(color:Color, position: Pair<Float, Float>, clack: Boolean){
    drawLine(
        color = color,
        start = Offset(x = position.first, y = 0f),
        end = Offset(x = position.first, y = position.second - 70),
        strokeWidth = 2.5f
    )
    drawLine(
        color = color,
        start = Offset(x = position.first, y = position.second + 20),
        end = Offset(x = position.first, y = position.second + 60.dp.toPx()),
        strokeWidth = 2.5f
    )

    val tag = Path()
    tag.moveTo(x = position.first - 20f, y =  position.second)
    tag.lineTo(x = position.first - 20f, y =  position.second - 50f)
    tag.lineTo(x = position.first, y = position.second - 70)
    tag.lineTo(x = position.first + 20f, y = position.second - 50f)
    tag.lineTo(x = position.first + 20f, y = position.second)
    tag.lineTo(x = position.first, y = position.second + 20)
    tag.moveTo(x = position.first - 20f, y = position.second)

    drawPath(
        path = tag,
        color = color
    )

    if (clack){
        drawLine(
            color = Color.Black,
            start = Offset(x = position.first - 8f, y = position.second - 6f -30f),
            end = Offset(x = position.first - 6f, y = position.second -30f),
            strokeWidth = 3f
        )

        drawLine(
            color = Color.Black,
            start = Offset(x = position.first - 6f, y = position.second -30f),
            end = Offset(x = position.first + 8f, y = position.second - 6f -30f),
            strokeWidth = 3f
        )

        drawLine(
            color = Color.Black,
            start = Offset(x = position.first - 8f, y = position.second - 6f -10f),
            end = Offset(x = position.first - 6f, y = position.second -10f),
            strokeWidth = 3f
        )
        drawLine(
            color = Color.Black,
            start = Offset(x = position.first - 6f, y = position.second -10f),
            end = Offset(x = position.first + 8f, y = position.second - 6f -10f),
            strokeWidth = 3f
        )
    } else {
        drawLine(
            color = Color.Black,
            start = Offset(x = position.first - 8f, y = position.second - 6f -20f),
            end = Offset(x = position.first - 6f, y = position.second -20f),
            strokeWidth = 3f
        )

        drawLine(
            color = Color.Black,
            start = Offset(x = position.first - 6f, y = position.second -20f),
            end = Offset(x = position.first + 8f, y = position.second - 6f -20f),
            strokeWidth = 3f
        )
    }
}

fun vectorToImageBitmap(
    context: Context,
    @DrawableRes id: Int
): ImageBitmap? {
    val drawable = ContextCompat.getDrawable(context, id) ?: return null
    return drawable.toBitmap().asImageBitmap()
}