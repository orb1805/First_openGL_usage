package e.roman.lab2

import android.view.ScaleGestureDetector

class MyPinchListener(view: MyGLSurfaceView) : ScaleGestureDetector.SimpleOnScaleGestureListener() {

    var span: Float = 10000f
    val view = view

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        //view.isNotPinch = false
        if (detector.currentSpan > detector.previousSpan) {
            view.renderer.pinchSize = ((detector.currentSpan + span) / 20000)
            span += detector.currentSpan
        } else {
            view.renderer.pinchSize = ((span - detector.currentSpan) / 20000)
            span -= detector.currentSpan
        }
        //view.isNotPinch = true
        return true
    }
}