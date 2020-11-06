package e.roman.lab2

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.ScaleGestureDetector

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {



    val renderer: MyGLRenderer
    //var isNotPinch = true
    var mPtrCount = 0

    init {

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2)

        renderer = MyGLRenderer()

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)

        val mScaleDetector = ScaleGestureDetector(context, MyPinchListener(this))
        this.setOnTouchListener{ v, event ->
            mScaleDetector.onTouchEvent(event)
            this.onTouchEvent(event)
            true
        }
    }

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        val action: Int = e.action and MotionEvent.ACTION_MASK
        when (action) {
            MotionEvent.ACTION_POINTER_DOWN -> mPtrCount++
            MotionEvent.ACTION_POINTER_UP -> mPtrCount--
            MotionEvent.ACTION_DOWN -> mPtrCount++
            MotionEvent.ACTION_UP -> mPtrCount--
            //MotionEvent.ACTION_MOVE -> Log.d("TAG", "Move $mPtrCount")
        }
        if (mPtrCount == 1) {
            val x: Float = e.x
            val y: Float = e.y

            when (e.action) {
                MotionEvent.ACTION_MOVE -> {

                    var dx: Float = x - previousX
                    var dy: Float = y - previousY

                    /*// reverse direction of rotation above the mid-line
                if (y > height / 2) {
                    dx *= -1
                }

                // reverse direction of rotation to left of the mid-line
                if (x < width / 2) {
                    dy *= -1
                }*/
                    renderer.angleX += dx * TOUCH_SCALE_FACTOR / 2
                    renderer.angleY += dy * TOUCH_SCALE_FACTOR / 2
                    requestRender()
                }
            }

            previousX = x
            previousY = y
        }
        return true
    }
}