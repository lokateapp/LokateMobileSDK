package com.lokate.demo.utils

/*
* Taken from the https://github.com/oleksandrbalan/textflow
* */
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import kotlin.math.max

/**
 * The composable to draw a text which flows around an "obstacle". Obstacle can be placed to the start top corner or to
 * the end top corner based on [obstacleAlignment]. Use [obstacleContent] lambda to provide a composable for an
 * "obstacle".
 *
 * @param text The text to be displayed.
 * @param modifier The modifier for root composable.
 * @param obstacleAlignment The alignment for an "obstacle" inside the text.
 * @param color [Color] to apply to the text. If [Color.Unspecified], and [style] has no color set, this will be
 * [LocalContentColor].
 * @param fontSize The size of glyphs to use when painting the text. See [TextStyle.fontSize].
 * @param fontStyle The typeface variant to use when drawing the letters (e.g., italic). See [TextStyle.fontStyle].
 * @param fontWeight The typeface thickness to use when painting the text (e.g., [FontWeight.Bold]).
 * @param fontFamily The font family to be used when rendering the text. See [TextStyle.fontFamily].
 * @param letterSpacing The amount of space to add between each letter. See [TextStyle.letterSpacing].
 * @param textDecoration The decorations to paint on the text (e.g., an underline). See [TextStyle.textDecoration].
 * @param textAlign The alignment of the text within the lines of the paragraph. See [TextStyle.textAlign].
 * @param lineHeight Line height for the [Paragraph] in [TextUnit] unit, e.g. SP or EM. See [TextStyle.lineHeight].
 * @param overflow How visual overflow should be handled.
 * @param softWrap Whether the text should break at soft line breaks. If false, the glyphs in the text will be
 * positioned as if there was unlimited horizontal space. If [softWrap] is false, [overflow] and TextAlign may have
 * unexpected effects.
 * @param maxLines An optional maximum number of lines for the text to span, wrapping if necessary. If the text
 * exceeds the given number of lines, it will be truncated according to [overflow] and [softWrap]. If it is not null,
 * then it must be greater than zero.
 * @param onTextLayout Callback that is executed when a new text layout is calculated. A [TextLayoutResult] object
 * that callback provides contains paragraph information, size of the text, baselines and other details. The callback
 * can be used to add additional decoration or functionality to the text. For example, to draw selection around
 * the text.
 * @param style Style configuration for the text such as color, font, line height etc.
 * @param obstacleContent The slot for an "obstacle".
 */
@Composable
fun TextFlow(
    text: String,
    modifier: Modifier = Modifier,
    obstacleAlignment: TextFlowObstacleAlignment = TextFlowObstacleAlignment.TopStart,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult?, TextLayoutResult?) -> Unit = { _, _ -> },
    style: TextStyle = LocalTextStyle.current,
    obstacleContent: @Composable () -> Unit = {},
) {
    TextFlow(
        text = AnnotatedString(text),
        modifier = modifier,
        obstacleAlignment = obstacleAlignment,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        onTextLayout = onTextLayout,
        style = style,
        obstacleContent = obstacleContent,
    )
}

/**
 * The composable to draw a text which flows around an "obstacle". Obstacle can be placed to the start top corner or to
 * the end top corner based on [obstacleAlignment]. Use [obstacleContent] lambda to provide a composable for an
 * "obstacle".
 *
 * @param text The text to be displayed.
 * @param modifier The modifier for root composable.
 * @param obstacleAlignment The alignment for an "obstacle" inside the text.
 * @param color [Color] to apply to the text. If [Color.Unspecified], and [style] has no color set, this will be
 * [LocalContentColor].
 * @param fontSize The size of glyphs to use when painting the text. See [TextStyle.fontSize].
 * @param fontStyle The typeface variant to use when drawing the letters (e.g., italic). See [TextStyle.fontStyle].
 * @param fontWeight The typeface thickness to use when painting the text (e.g., [FontWeight.Bold]).
 * @param fontFamily The font family to be used when rendering the text. See [TextStyle.fontFamily].
 * @param letterSpacing The amount of space to add between each letter. See [TextStyle.letterSpacing].
 * @param textDecoration The decorations to paint on the text (e.g., an underline). See [TextStyle.textDecoration].
 * @param textAlign The alignment of the text within the lines of the paragraph. See [TextStyle.textAlign].
 * @param lineHeight Line height for the [Paragraph] in [TextUnit] unit, e.g. SP or EM. See [TextStyle.lineHeight].
 * @param overflow How visual overflow should be handled.
 * @param softWrap Whether the text should break at soft line breaks. If false, the glyphs in the text will be
 * positioned as if there was unlimited horizontal space. If [softWrap] is false, [overflow] and TextAlign may have
 * unexpected effects.
 * @param maxLines An optional maximum number of lines for the text to span, wrapping if necessary. If the text
 * exceeds the given number of lines, it will be truncated according to [overflow] and [softWrap]. If it is not null,
 * then it must be greater than zero.
 * @param onTextLayout Callback that is executed when a new text layout is calculated. A [TextLayoutResult] object
 * that callback provides contains paragraph information, size of the text, baselines and other details. The callback
 * can be used to add additional decoration or functionality to the text. For example, to draw selection around
 * the text.
 * @param style Style configuration for the text such as color, font, line height etc.
 * @param obstacleContent The slot for an "obstacle".
 */
@Composable
fun TextFlow(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    obstacleAlignment: TextFlowObstacleAlignment = TextFlowObstacleAlignment.TopStart,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult?, TextLayoutResult?) -> Unit = { _, _ -> },
    style: TextStyle = LocalTextStyle.current,
    obstacleContent: @Composable () -> Unit = {},
) {
    SubcomposeLayout(modifier) { constraints ->
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        // Measure obstacle(s) first to check how much space they occupy
        val obstaclePlaceables =
            subcompose(TextFlowContent.Obstacle, obstacleContent).map {
                it.measure(looseConstraints)
            }

        // Take the largest width and height from obstacles
        val maxObstacleWidth = obstaclePlaceables.maxOfOrNull { it.width } ?: 0
        val maxObstacleHeight = obstaclePlaceables.maxOfOrNull { it.height } ?: 0

        // And calculate an offset for obstacle(s)
        val obstacleOffset =
            when (obstacleAlignment) {
                TextFlowObstacleAlignment.TopStart -> IntOffset.Zero
                TextFlowObstacleAlignment.TopEnd -> IntOffset(constraints.maxWidth - maxObstacleWidth, 0)
            }

        // Then measure the text canvas with the given obstacle
        val textPlaceable =
            subcompose(TextFlowContent.Text) {
                TextFlowCanvas(
                    text = text,
                    obstacleSize = IntSize(maxObstacleWidth, maxObstacleHeight),
                    obstacleAlignment = obstacleAlignment,
                    constraints = constraints,
                    color = color,
                    fontSize = fontSize,
                    fontStyle = fontStyle,
                    fontWeight = fontWeight,
                    fontFamily = fontFamily,
                    letterSpacing = letterSpacing,
                    textDecoration = textDecoration,
                    textAlign = textAlign,
                    lineHeight = lineHeight,
                    overflow = overflow,
                    softWrap = softWrap,
                    maxLines = maxLines,
                    onTextLayout = onTextLayout,
                    style = style,
                )
            }.first().measure(looseConstraints)

        layout(
            width = textPlaceable.width,
            height = max(maxObstacleHeight, textPlaceable.height),
        ) {
            obstaclePlaceables.forEach {
                it.place(obstacleOffset)
            }

            textPlaceable.place(0, 0)
        }
    }
}

/**
 * The allowed alignment for an "obstacle" inside the [TextFlow] composable.
 */
enum class TextFlowObstacleAlignment {
    /**
     * Obstacle is aligned in the top start corner.
     */
    TopStart,

    /**
     * Obstacle is aligned in the top end corner.
     */
    TopEnd,
}

private enum class TextFlowContent { Obstacle, Text }

@Composable
internal fun TextFlowCanvas(
    text: AnnotatedString,
    obstacleSize: IntSize,
    obstacleAlignment: TextFlowObstacleAlignment,
    constraints: Constraints,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult?, TextLayoutResult?) -> Unit = { _, _ -> },
    style: TextStyle = LocalTextStyle.current,
) {
    // Basically copy-pasta from Text composable
    val textColor =
        color.takeOrElse {
            style.color.takeOrElse {
                LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
            }
        }

    // Basically copy-pasta from Text composable
    val mergedStyle =
        style.merge(
            textAlign?.let {
                TextStyle(
                    color = textColor,
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    textAlign = it,
                    lineHeight = lineHeight,
                    fontFamily = fontFamily,
                    textDecoration = textDecoration,
                    fontStyle = fontStyle,
                    letterSpacing = letterSpacing,
                    // However we need to disable font padding to align both text paragraphs perfectly
                    // platformStyle = PlatformTextStyle(null),
                )
            },
        )

    // Prepare text measurer instance to measure text based on constraints
    val textMeasurer = rememberTextMeasurer()

    // Measure 2 blocks of texts
    // The "top" one which is affected by the obstacle, thus has smaller width and second "bottom" one
    val result =
        textMeasurer.measureTextFlow(
            text = text,
            obstacleSize = obstacleSize,
            layoutWidth = constraints.maxWidth,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            mergedStyle = mergedStyle,
        )

    // Report text results to the caller
    onTextLayout(result.topTextResult, result.bottomTextResult)

    // Calculate final canvas size to use in the modifier
    val canvasSize =
        calculateCanvasSize(
            density = LocalDensity.current,
            result = result,
            obstacleSize = obstacleSize,
            constraints = constraints,
        )

    Canvas(modifier = Modifier.size(canvasSize)) {
        // Paint the top text with a horizontal offset
        translate(left = calculateTopBlockOffset(obstacleSize, obstacleAlignment)) {
            result.topTextResult?.multiParagraph?.paint(
                canvas = drawContext.canvas,
                color = textColor,
                decoration = textDecoration,
            )
        }

        // Paint the bottom text moved below by the top text's height
        translate(top = result.topTextHeight.toFloat()) {
            result.bottomTextResult?.multiParagraph?.paint(
                canvas = drawContext.canvas,
                color = textColor,
                decoration = textDecoration,
            )
        }
    }
}

private fun TextMeasurer.measureTextFlow(
    text: AnnotatedString,
    obstacleSize: IntSize,
    layoutWidth: Int,
    overflow: TextOverflow,
    softWrap: Boolean,
    maxLines: Int,
    mergedStyle: TextStyle,
): TextFlowCanvasLayoutResult {
    var topBlock: TextLayoutResult? = null
    var topBlockVisibleLineCount = 0
    var topBlockLastCharIndex = -1
    var hasBottomBlock = true

    // Measure first block only if obstacle is present
    // Otherwise there is noting to wrap and only bottom block will be painted
    if (obstacleSize.height > 0) {
        topBlock =
            measure(
                text = text,
                style = mergedStyle,
                constraints =
                    Constraints(
                        maxWidth = layoutWidth - obstacleSize.width,
                        maxHeight = Int.MAX_VALUE,
                    ),
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines,
            )

        // Calculate real text height and lines count based on visible lines
        val lastVisibleLineIndex = topBlock.lastVisibleLineIndex(obstacleSize.height)
        val topBlockHeight = topBlock.getLineBottom(lastVisibleLineIndex)
        topBlockVisibleLineCount = lastVisibleLineIndex + 1

        // Also get index of the last character to know which part of the original text will belong to the bottom block
        topBlockLastCharIndex =
            topBlock.getOffsetForPosition(
                Offset(
                    topBlock.getLineRight(lastVisibleLineIndex),
                    topBlock.getLineTop(lastVisibleLineIndex),
                ),
            )

        // Check if text spans to the bottom block
        hasBottomBlock = topBlockVisibleLineCount < maxLines && topBlockLastCharIndex < text.length

        // Remeasure the top block with it's real height and displayed lines so that we do not have to clip it in canvas
        // and can report the correct text result to the caller
        topBlock =
            measure(
                text = text,
                style = mergedStyle,
                constraints =
                    Constraints(
                        maxWidth = layoutWidth - obstacleSize.width,
                        maxHeight = topBlockHeight.toInt(),
                    ),
                overflow = if (hasBottomBlock) TextOverflow.Clip else overflow,
                softWrap = softWrap,
                maxLines = topBlockVisibleLineCount,
            )
    }

    var bottomBlock: TextLayoutResult? = null
    if (hasBottomBlock) {
        bottomBlock =
            measure(
                text = text.subSequence(topBlockLastCharIndex + 1, text.length),
                style = mergedStyle,
                constraints =
                    Constraints(
                        maxWidth = layoutWidth,
                        maxHeight = Int.MAX_VALUE,
                    ),
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines - topBlockVisibleLineCount,
            )
    }

    return TextFlowCanvasLayoutResult(topBlock, bottomBlock)
}

private fun TextLayoutResult.lastVisibleLineIndex(height: Int): Int {
    repeat(lineCount) {
        if (getLineBottom(it) > height) {
            return it
        }
    }
    return lineCount - 1
}

private fun calculateCanvasSize(
    density: Density,
    result: TextFlowCanvasLayoutResult,
    obstacleSize: IntSize,
    constraints: Constraints,
): DpSize {
    val width =
        if (result.topTextResult != null) {
            obstacleSize.width + result.topTextResult.size.width
        } else if (result.bottomTextResult != null) {
            result.bottomTextResult.size.width
        } else {
            0
        }

    val height = result.topTextHeight + result.bottomTextHeight

    return with(density) {
        DpSize(
            width = constraints.constrainWidth(width).toDp(),
            height = constraints.constrainHeight(height).toDp(),
        )
    }
}

private fun calculateTopBlockOffset(
    obstacleSize: IntSize,
    obstacleAlignment: TextFlowObstacleAlignment,
): Float =
    if (obstacleAlignment == TextFlowObstacleAlignment.TopStart) {
        obstacleSize.width.toFloat()
    } else {
        0f
    }

private class TextFlowCanvasLayoutResult(
    val topTextResult: TextLayoutResult?,
    val bottomTextResult: TextLayoutResult?,
) {
    val topTextHeight: Int get() = topTextResult?.size?.height ?: 0
    val bottomTextHeight: Int get() = bottomTextResult?.size?.height ?: 0
}
