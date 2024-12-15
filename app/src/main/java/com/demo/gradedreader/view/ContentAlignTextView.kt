package com.demo.gradedreader.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.demo.gradedreader.wordroom.Word

class ContentAlignTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var text: String = ""
    private val textPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF000000.toInt() // 默认黑色
        textSize = 50f             // 默认字体大小
    }

    private var highlightedWords: List<Word> = emptyList() // 高亮单词列表

    private val colorPalette =
        listOf(Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.CYAN, Color.RED)

    private val defaultColor = Color.BLACK // 默认字体颜色

    fun setText(newText: String) {
        text = newText
        requestLayout()
        invalidate()
    }

    override fun getText():String{

        return text
    }

    fun setHighlightedWords(words: List<Word>) {
        highlightedWords = words
        invalidate() // 数据变化后重新绘制
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val contentWidth = width - paddingLeft - paddingRight
        val lines = calculateLines(text, contentWidth)
        val height =
            ((lines.size * textPaint.fontSpacing) + paddingTop + paddingBottom).toInt()
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        val contentWidth = width - paddingLeft - paddingRight
        var y = paddingTop + textPaint.fontSpacing // 起始 y 值为字体高度

        val lines = calculateLines(text, contentWidth)
        for (line in lines) {
            drawJustifiedLine(canvas, line, contentWidth.toFloat(), y)
            y += textPaint.fontSpacing // 使用 `fontSpacing` 保证行高一致
        }
        super.onDraw(canvas) // 确保调用父类的 onDraw
    }

    private fun drawJustifiedLine(canvas: Canvas, lineText: String, contentWidth: Float, y: Float) {
        val words = lineText.split(" ")
        if (words.size <= 1) {
            // 单词少于或等于一个，直接绘制
            canvas.drawText(lineText, paddingLeft.toFloat(), y, textPaint)
            return
        }

        // 计算空格的额外宽度
        val textWidth = words.sumOf { textPaint.measureText(it).toDouble() }.toFloat()
        val totalSpaces = words.size - 1

        // 防止除以零
        val extraSpace = if (totalSpaces > 0) {
            (contentWidth - textWidth) / totalSpaces
        } else {
            0f
        }

        // 逐单词绘制
        var xPosition = paddingLeft.toFloat()
        for (word in words) {
            drawWord(canvas,word,xPosition,y)
            canvas.drawText(word, xPosition, y, textPaint)
            xPosition += textPaint.measureText(word) + extraSpace
        }
    }

    private fun calculateLines(text: String, contentWidth: Int): List<String> {
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()

        for (word in text.split(" ")) {
            val potentialLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val potentialWidth = textPaint.measureText(potentialLine)

            if (potentialWidth > contentWidth) {
                // 当前行已满，保存当前行并开启新行
                lines.add(currentLine.toString())
                currentLine = StringBuilder(word)
            } else {
                // 添加单词到当前行
                if (currentLine.isNotEmpty()) {
                    currentLine.append(" ")
                }
                currentLine.append(word)
            }
        }

        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString())
        }

        return lines
    }

    //    可以针对传入的等级和上次匹配的结果进行优化避免多次查找，避免频繁绘制
    private fun drawWord(canvas: Canvas, word: String, x: Float, y: Float) {
        // 检查单词是否需要高亮
        val highlightedWord = highlightedWords.find { it.word.equals(word, ignoreCase = true) }


        if (highlightedWord != null) {
            val level = highlightedWord.level
            textPaint.color = if (level == 0) {
                Color.RED // 特殊处理 0 级时的颜色
            } else {
                // 根据等级循环映射颜色
                val colorIndex = (level - 1) % colorPalette.size
                colorPalette[colorIndex]
            }
        } else {
            // 默认颜色
            textPaint.color = defaultColor
        }
        canvas.drawText(word, x, y, textPaint)
    }


}
