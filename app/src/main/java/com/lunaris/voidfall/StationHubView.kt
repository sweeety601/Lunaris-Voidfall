package com.lunaris.voidfall

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.min
import kotlin.random.Random

class StationHubView(context: Context) : View(context) {
    data class Terminal(
        val title: String, val sub: String,
        val x: Float, val y: Float, val w: Float, val h: Float
    )

    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val t = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Typeface.DEFAULT_BOLD
    }

    private val buttons = listOf(
        Terminal("УПРАВЛЕНИЕ СИНДИКАТОМ", "Ресурсы и добыча", .05f, .31f, .40f, .13f),
        Terminal("СЮЖЕТНАЯ КАМПАНИЯ", "Главы и задания", .55f, .31f, .40f, .13f),
        Terminal("НАЁМНИКИ", "Персонажи • навыки • экипировка", .05f, .48f, .40f, .13f),
        Terminal("СКЛАД", "Оборудование и материалы", .55f, .48f, .40f, .13f),
        Terminal("ГАРАЖ", "Боевые корабли и модули", .05f, .65f, .40f, .13f),
        Terminal("ИВЕНТЫ", "Временные операции", .55f, .65f, .40f, .13f),
        Terminal("ПОИСК СИГНАЛА", "Персонажи • корабли • модули", .18f, .82f, .64f, .12f)
    )

    private val stars = Array(90) {
        PointF(Random.nextFloat(), Random.nextFloat() * .72f)
    }
    private var active = -1

    override fun onDraw(c: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        c.drawColor(Color.rgb(4, 7, 16))

        stars.forEachIndexed { i, s ->
            p.color = if (i % 5 == 0) Color.rgb(120,180,210) else Color.rgb(55,80,105)
            c.drawCircle(s.x*w, s.y*h, if (i % 7 == 0) 2.2f else 1.1f, p)
        }

        val hull = Path().apply {
            moveTo(0f,h*.18f); lineTo(w*.1f,h*.1f); lineTo(w*.9f,h*.1f)
            lineTo(w,h*.18f); lineTo(w,h); lineTo(0f,h); close()
        }
        p.style = Paint.Style.FILL
        p.color = Color.rgb(11,18,31)
        c.drawPath(hull,p)
        p.style = Paint.Style.STROKE
        p.strokeWidth = 3f
        p.color = Color.rgb(46,78,104)
        c.drawPath(hull,p)

        p.strokeWidth = 1.5f
        p.color = Color.rgb(25,51,70)
        for (i in 1..5) {
            val y = h * (.2f + i*.12f)
            c.drawLine(0f,y,w,y,p)
        }
        c.drawLine(w*.5f,h*.18f,w*.5f,h,p)

        p.style = Paint.Style.FILL
        p.color = Color.rgb(32,68,88)
        c.drawCircle(w*.5f,h*.21f,min(w,h)*.12f,p)
        p.color = Color.argb(110,85,200,235)
        c.drawCircle(w*.5f,h*.21f,min(w,h)*.105f,p)
        p.color = Color.rgb(150,235,255)
        c.drawCircle(w*.5f,h*.21f,4f,p)

        t.textAlign = Paint.Align.CENTER
        t.color = Color.WHITE
        t.textSize = w*.055f
        c.drawText("КОСМИЧЕСКАЯ СТАНЦИЯ «ЛУНАРИС»",w/2,h*.075f,t)
        p.color = Color.rgb(100,210,235)
        c.drawRect(w*.28f,h*.092f,w*.72f,h*.096f,p)
        t.textSize = w*.027f
        t.color = Color.rgb(130,160,180)
        c.drawText("LUNARIS // COMMAND DECK",w/2,h*.115f,t)

        buttons.forEachIndexed { i,b ->
            val l=b.x*w; val top=b.y*h; val r=(b.x+b.w)*w; val bot=(b.y+b.h)*h
            p.style=Paint.Style.FILL
            p.color=if(i==active)Color.rgb(25,65,82)else Color.rgb(13,29,43)
            c.drawRoundRect(l,top,r,bot,12f,12f,p)
            p.style=Paint.Style.STROKE
            p.strokeWidth=if(i==active)3f else 1.5f
            p.color=if(i==active)Color.rgb(125,235,255)else Color.rgb(48,99,120)
            c.drawRoundRect(l,top,r,bot,12f,12f,p)
            p.style=Paint.Style.FILL
            p.color=Color.rgb(83,213,237)
            c.drawRect(l+10,top+12,l+14,bot-12,p)

            t.textAlign=Paint.Align.LEFT
            t.textSize=w*.027f
            t.color=Color.WHITE
            c.drawText(b.title,l+24,top+h*.045f,t)
            t.textSize=w*.019f
            t.color=Color.rgb(132,174,192)
            c.drawText(b.sub,l+24,top+h*.088f,t)

            p.color=Color.rgb(68,170,194)
            c.drawCircle(r-18,top+18,4f,p)
        }

        p.color=Color.rgb(20,43,57)
        c.drawRect(0f,h*.955f,w,h,p)
        t.textAlign=Paint.Align.LEFT
        t.textSize=w*.018f
        t.color=Color.rgb(110,155,170)
        c.drawText("LUNARIS // СИСТЕМЫ ОНЛАЙН",18f,h*.982f,t)
        t.textAlign=Paint.Align.RIGHT
        c.drawText("СИНДИКАТ: 0   •   ЭНЕРГИЯ: 100%",w-18,h*.982f,t)
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (e.action != MotionEvent.ACTION_UP) return true
        val x=e.x/width
        val y=e.y/height
        active=buttons.indexOfFirst {
            x>=it.x && x<=it.x+it.w && y>=it.y && y<=it.y+it.h
        }
        invalidate()
        return true
    }
}
