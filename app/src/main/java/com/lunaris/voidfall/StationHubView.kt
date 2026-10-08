package com.lunaris.voidfall
import android.content.Context
import android.graphics.*
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.*
import android.widget.FrameLayout
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.zip.GZIPInputStream
import kotlin.math.*

class StationHubView(context: Context):FrameLayout(context){
 private val surface=HubSurface(context)
 private val hud=Hud(context)
 init{addView(surface,LayoutParams(-1,-1));addView(hud,LayoutParams(-1,-1))}
 fun resume(){surface.onResume()}
 fun pause(){surface.onPause()}
 private class Hud(c:Context):View(c){
  private val p=Paint(1);private val t=Paint(1).apply{typeface=Typeface.DEFAULT_BOLD};private var active=-1
  private val names=arrayOf(
   "УПРАВЛЕНИЕ СИНДИКАТОМ" to "Ресурсы и добыча","СЮЖЕТНАЯ КАМПАНИЯ" to "Главы и задания",
   "НАЁМНИКИ" to "Персонажи • навыки • экипировка","СКЛАД" to "Оборудование и материалы",
   "ГАРАЖ" to "Боевые корабли и модули","ИВЕНТЫ" to "Временные операции",
   "ПОИСК СИГНАЛА" to "Персонажи • корабли • модули")
  private val rs=arrayOf(floatArrayOf(.04f,.25f,.43f,.12f),floatArrayOf(.53f,.25f,.43f,.12f),floatArrayOf(.04f,.42f,.43f,.12f),floatArrayOf(.53f,.42f,.43f,.12f),floatArrayOf(.04f,.59f,.43f,.12f),floatArrayOf(.53f,.59f,.43f,.12f),floatArrayOf(.18f,.77f,.64f,.12f))
  override fun onDraw(c:Canvas){val w=width.toFloat();val h=height.toFloat()
   p.color=Color.argb(175,3,8,16);c.drawRect(0f,0f,w,h*.17f,p)
   t.textAlign=Paint.Align.CENTER;t.color=Color.WHITE;t.textSize=w*.055f;c.drawText("КОСМИЧЕСКАЯ СТАНЦИЯ «ЛУНАРИС»",w/2,h*.075f,t)
   t.textSize=w*.026f;t.color=Color.rgb(120,205,230);c.drawText("LUNARIS // COMMAND DECK",w/2,h*.115f,t)
   names.forEachIndexed{i,n->val r=rs[i];val l=r[0]*w;val y=r[1]*h;val rr=(r[0]+r[2])*w;val b=(r[1]+r[3])*h
    p.color=if(i==active)Color.argb(225,18,70,88)else Color.argb(205,8,24,36);c.drawRoundRect(l,y,rr,b,14f,14f,p)
    p.style=Paint.Style.STROKE;p.strokeWidth=if(i==active)3f else 1.5f;p.color=if(i==active)Color.rgb(125,235,255)else Color.rgb(55,115,135);c.drawRoundRect(l,y,rr,b,14f,14f,p);p.style=Paint.Style.FILL
    p.color=Color.rgb(80,210,235);c.drawRect(l+10,y+12,l+14,b-12,p)
    t.textAlign=Paint.Align.LEFT;t.textSize=w*.026f;t.color=Color.WHITE;c.drawText(n.first,l+24,y+h*.045f,t)
    t.textSize=w*.018f;t.color=Color.rgb(140,180,195);c.drawText(n.second,l+24,y+h*.087f,t)
   }
   p.color=Color.argb(220,18,45,62);c.drawRect(0f,h*.955f,w,h,p);t.textAlign=Paint.Align.LEFT;t.textSize=w*.018f;t.color=Color.rgb(110,165,180);c.drawText("LUNARIS // СИСТЕМЫ ОНЛАЙН",18f,h*.982f,t);t.textAlign=Paint.Align.RIGHT;c.drawText("СИНДИКАТ: 0   •   ЭНЕРГИЯ: 100%",w-18f,h*.982f,t)
  }
  override fun onTouchEvent(e:MotionEvent):Boolean{if(e.action!=MotionEvent.ACTION_UP)return true;val x=e.x/width;val y=e.y/height;active=rs.indexOfFirst{x>=it[0]&&x<=it[0]+it[2]&&y>=it[1]&&y<=it[1]+it[3]};invalidate();return true}
 }
 private class HubSurface(c:Context):GLSurfaceView(c){init{setEGLContextClientVersion(2);setRenderer(Renderer(c));renderMode=RENDERMODE_CONTINUOUSLY}}
 private class Renderer(private val ctx:Context):GLSurfaceView.Renderer{
  private var prog=0;private var buf:java.nio.FloatBuffer?=null;private var count=0
  private val proj=FloatArray(16);private val view=FloatArray(16);private val mvp=FloatArray(16);private var sway=0f
  override fun onSurfaceCreated(a:javax.microedition.khronos.opengles.GL10?,b:javax.microedition.khronos.egl.EGLConfig?){GLES20.glClearColor(.006f,.012f,.028f,1f);GLES20.glEnable(GLES20.GL_DEPTH_TEST);prog=make(VS,FS);load()}
  override fun onSurfaceChanged(a:javax.microedition.khronos.opengles.GL10?,w:Int,h:Int){GLES20.glViewport(0,0,w,h);Matrix.perspectiveM(proj,0,58f,w.toFloat()/max(1f,h.toFloat()),.1f,80f)}
  override fun onDrawFrame(a:javax.microedition.khronos.opengles.GL10?){GLES20.glClear(16640);sway+=.01f;Matrix.setLookAtM(view,0,sin(sway)*.35f,-16f,5.2f,0f,1.0f,3.0f,0f,0f,1f);Matrix.multiplyMM(mvp,0,proj,0,view,0);GLES20.glUseProgram(prog);GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(prog,"uMvp"),1,false,mvp,0)
   val ab=GLES20.glGetAttribLocation(prog,"aPos");val an=GLES20.glGetAttribLocation(prog,"aN");val ac=GLES20.glGetAttribLocation(prog,"aC");buf!!.position(0);GLES20.glEnableVertexAttribArray(ab);GLES20.glVertexAttribPointer(ab,3,GLES20.GL_FLOAT,false,40,buf!!);buf!!.position(3);GLES20.glEnableVertexAttribArray(an);GLES20.glVertexAttribPointer(an,3,GLES20.GL_FLOAT,false,40,buf!!);buf!!.position(6);GLES20.glEnableVertexAttribArray(ac);GLES20.glVertexAttribPointer(ac,4,GLES20.GL_FLOAT,false,40,buf);GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,count)
  }
  private fun load(){val b=android.util.Base64.decode(ctx.assets.open("lunaris_hub_model.b64").bufferedReader().readText(),android.util.Base64.DEFAULT);val s=GZIPInputStream(ByteArrayInputStream(b)).bufferedReader().readText();val vs=ArrayList<FloatArray>();val out=ArrayList<Float>();var g="Floor"
   fun col():FloatArray=when{g.contains("Screen")||g.contains("Light")->floatArrayOf(.18f,.78f,.92f,1f);g.contains("Window")->floatArrayOf(.04f,.16f,.23f,1f);g.contains("Core")->floatArrayOf(.10f,.30f,.38f,1f);g.contains("Console")->floatArrayOf(.09f,.16f,.21f,1f);g.contains("Rib")->floatArrayOf(.07f,.13f,.17f,1f);g.contains("Platform")->floatArrayOf(.13f,.19f,.23f,1f);g.contains("Door")->floatArrayOf(.09f,.23f,.29f,1f);else->floatArrayOf(.035f,.065f,.09f,1f)}
   fun emit(a:Int,b:Int,d:Int){val x=vs[a-1];val y=vs[b-1];val z=vs[d-1];val ux=y[0]-x[0];val uy=y[1]-x[1];val uz=y[2]-x[2];val vx=z[0]-x[0];val vy=z[1]-x[1];val vz=z[2]-x[2];var nx=uy*vz-uz*vy;var ny=uz*vx-ux*vz;var nz=ux*vy-uy*vx;val q=sqrt(nx*nx+ny*ny+nz*nz).coerceAtLeast(.001f);nx/=q;ny/=q;nz/=q;val c=col();for(i in intArrayOf(a,b,d)){val p=vs[i-1];out.addAll(floatArrayOf(p[0],p[1],p[2],nx,ny,nz,c[0],c[1],c[2],1f).asList())}}
   s.lineSequence().forEach{l->val q=l.trim();when{q.startsWith("v ")->{val z=q.split(Regex("\\s+"));vs.add(floatArrayOf(z[1].toFloat(),z[2].toFloat(),z[3].toFloat()))};q.startsWith("g ")->g=q.substring(2);q.startsWith("f ")->{val z=q.split(Regex("\\s+")).drop(1).map{it.substringBefore('/').toInt()};for(i in 1 until z.size-1)emit(z[0],z[i],z[i+1])}}}
   count=out.size/10;buf=ByteBuffer.allocateDirect(out.size*4).order(ByteOrder.nativeOrder()).asFloatBuffer();buf!!.put(out.toFloatArray()).position(0)
  }
  private fun make(v:String,f:String):Int{fun sh(t:Int,s:String):Int{val x=GLES20.glCreateShader(t);GLES20.glShaderSource(x,s);GLES20.glCompileShader(x);return x};val p=GLES20.glCreateProgram();GLES20.glAttachShader(p,sh(35633,v));GLES20.glAttachShader(p,sh(35632,f));GLES20.glLinkProgram(p);return p}
  companion object{const val VS="attribute vec3 aPos;attribute vec3 aN;attribute vec4 aC;uniform mat4 uMvp;varying vec3 n;varying vec4 c;void main(){gl_Position=uMvp*vec4(aPos,1.0);n=aN;c=aC;}";const val FS="precision mediump float;varying vec3 n;varying vec4 c;void main(){float l=.25+.75*max(dot(normalize(n),normalize(vec3(-.4,-.6,1.0))),0.0);float scan=.96+.04*sin(gl_FragCoord.x*.12)*sin(gl_FragCoord.y*.08);gl_FragColor=vec4(c.rgb*l*scan,c.a);}"}
 }
}