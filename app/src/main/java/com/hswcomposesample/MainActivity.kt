package com.hswcomposesample

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FloatAnimationSpec
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.hswcomposesample.loopview.LoopView
import com.hswcomposesample.ui.theme.ComposeSampleTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@SuppressLint("MutableCollectionMutableState", "UnrememberedMutableState")
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    var listData = mutableStateListOf<Int>(1, 2, 3, 4, 5, 7, 8, 9, 10, 11)
    val state = rememberLazyListState()
    var firstVisibleItemIndex by remember { mutableIntStateOf(state.firstVisibleItemIndex) }
    var isScrollInProgress by remember { mutableStateOf(state.isScrollInProgress) }


    var currentIndex by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val currentUnselectedScale by rememberUpdatedState(newValue = .6f)
    val currentSelectedScale by rememberUpdatedState(newValue = 1f)

    var progress by remember { mutableFloatStateOf(0f) }

    val scrollingOutScale = remember { mutableFloatStateOf(1f) }
    val scrollingInScale = remember { mutableFloatStateOf(0.6f) }

    val scope = rememberCoroutineScope()

    val refresh by remember { mutableStateOf(true) }


    LaunchedEffect(state) {
        snapshotFlow { state.firstVisibleItemScrollOffset }
            .onEach { firstVisibleItemScrollOffset ->
                // 滑动时，根据滑动距离计算缩放比例
                progress =
                    firstVisibleItemScrollOffset.toFloat() / (60.dp.value * density.density)
                val disparity = (currentSelectedScale - currentUnselectedScale) * progress
                scrollingOutScale.floatValue = (currentSelectedScale - disparity).toFloat()
                scrollingInScale.floatValue = (currentUnselectedScale + disparity).toFloat()
            }.launchIn(this)

        snapshotFlow { state.isScrollInProgress }
            .filter { it != isScrollInProgress }
            .onEach { isScrollInProgress = it }
            .launchIn(this)

        snapshotFlow { state.firstVisibleItemIndex }
            .filter { it != firstVisibleItemIndex }
            .onEach {
                firstVisibleItemIndex = it

            }
            .launchIn(this)
    }

    var scd by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
//        withContext(Dispatchers.IO) {
//            while (true) {
//                delay(10)
//                withContext(Dispatchers.Main) {
//                    scd = 20f
//                }
//            }
//        }

    }

//
//    AndroidView(
//        factory =
//    )

    var isScroll: Boolean by remember { mutableStateOf(true) }

    var showProgress: Boolean by remember { mutableStateOf(true) }

    if (showProgress) {
        AndroidView(
            modifier = Modifier.fillMaxWidth().height(180.dp).offset(0.dp, 200.dp),
            factory = { context->
                val list = ArrayList<String>()
                for (i in 0..59) {
                    list.add("item gp$i")
                }
                val view = LoopView(context).apply {
                    layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 180.dp.value.toInt())
                    setItemsVisibleCount(7)
                    setTextSize(18f)
                    setNotLoop()
                    setItems(list)
                    setInitPosition(0)
                }
//                view.startLoop()
                view
            },
            update = { view ->
                if (isScroll) {
                    view.startLoop()
                } else {
                    view.stopLoop()
                }
            },
        )
    } else {
        Button(onClick = {}) {
            Text("ddsdsdsdfasf")
        }
    }


    Column(modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        Button(onClick = {
            isScroll = !isScroll
        }) {
            Text("Add one")
        }
        Image(
            modifier = Modifier
                .size(120.dp)
                .clickable {
                    showProgress = false
                },
            painter = painterResource(id = R.mipmap.ic_server),
            contentDescription = "server"
        )
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(240.dp),
//            userScrollEnabled = true,
//            contentPadding = PaddingValues(20.dp),
//            state = state
//        )
//        {
//            items(listData.size + 4) { index ->
//                if (index < 2) {
//                    Box(
//                        modifier = Modifier
//                            .height(60.dp)
//                            .fillMaxWidth()
//                    ) { }
//                } else {
//                    val textList = arrayOf<String>(
//                        "name -> $index",
//                        "type -> $index",
//                        "Number -> $index"
//                    )
//                    val alpha = when (index) {
//                        firstVisibleItemIndex -> currentUnselectedScale
//                        firstVisibleItemIndex + 1 -> if (progress > 0.2) currentUnselectedScale else scrollingInScale.floatValue
//                        firstVisibleItemIndex + 2 -> scrollingOutScale.floatValue
//                        firstVisibleItemIndex + 3 -> scrollingInScale.floatValue
//                        else -> currentUnselectedScale
//                    }
//                    val color1 = Color(0xFF192265)
//                    val color2 = Color(0xFF192265)
//                    val color3 = Color(0xFF1077FF)
//                    val color =
//                        when (index) {
////                            firstVisibleItemIndex + 2 -> color3
//                            firstVisibleItemIndex + 2 -> if (progress >= 0.1) color2 else color3
//                            firstVisibleItemIndex + 3 -> if (progress < 0.1) color2 else color3
//                            else -> color1
//
//                        }
//
//                    RowItemView(
//                        textList, color, alpha
//                    )
//                }
//
//            }
//        }
    }
}

@Composable
fun RowItemView(
    textList: Array<String>,
    color: Color,
    alpha: Float
) {
    Row(
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        ItemText(
            textList[0],
            alpha, color
        )
        ItemText(
            textList[1],
            alpha, color
        )
        ItemText(
            textList[2],
            alpha, color
        )


    }
}


@Composable
fun ItemText(
    text: String,
    alpha: Float,
    color: Color,
) {
    Text(
        text,
        modifier = Modifier
            .alpha(
                alpha
            ).scale(alpha),
        color = color,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold

    )
}

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeSampleTheme {
        Greeting("Android")
    }
}