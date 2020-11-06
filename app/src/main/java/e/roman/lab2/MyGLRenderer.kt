package e.roman.lab2

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MyGLRenderer : GLSurfaceView.Renderer {

    @Volatile
    var angleX: Float = 0f
    @Volatile
    var angleY: Float = 0f
    @Volatile
    var pinchSize: Float = 1f

    private lateinit var triangle: Triangle
    private lateinit var bottom: MutableList<Triangle>
    private lateinit var pyramid: MutableList<Triangle>

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f)
    }

    private val rotationMatrixX = FloatArray(16)
    private val rotationMatrixY = FloatArray(16)

    override fun onDrawFrame(unused: GL10) {

        GLES20.glEnable(GL10.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GL10.GL_LEQUAL)

        // Redraw background color
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)

        //triangle.draw()

        // Set the camera position (View matrix)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // Draw shape
        //triangle.draw(vPMatrix)

        // Create a rotation transformation for the triangle
        //val time = SystemClock.uptimeMillis() % 4000L
        //val angle = 0.090f * time.toInt()
        Matrix.setRotateM(rotationMatrixY, 0, angleY, -1.0f, 0.0f, 0.0f)
        Matrix.setRotateM(rotationMatrixX, 0, angleX, 0.0f, 1.0f, 0.0f)


        // Combine the rotation matrix with the projection and camera view
        // Note that the vPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        var scaleMatrix = floatArrayOf(
            pinchSize, 0f, 0f, 0f,
            0f, pinchSize, 0f, 0f,
            0f, 0f, pinchSize, 0f,
            0f, 0f, 0f, 1f
        )
        var scratch= FloatArray(16)
        /*Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMatrixX, 0)
        Matrix.multiplyMM(scratch, 0, scratch, 0, rotationMatrixY, 0)*/
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, cos(-angleY / 100), sin(-angleY / 100), 0f,
            0f, -sin(-angleY / 100), cos(-angleY / 100), 0f,
            0f, 0f, 0f, 1f
        ), 0)
        Matrix.multiplyMM(scratch, 0, scratch, 0, floatArrayOf(
            cos(angleX / 100), 0f, -sin(angleX  / 100), 0f,
            0f, 1f, 0f, 0f,
            sin(angleX  / 100), 0f, cos(angleX  / 100), 0f,
            0f, 0f, 0f, 1f
        ), 0)
        Matrix.multiplyMM(scratch, 0, scratch, 0, scaleMatrix, 0)

        // Draw triangle
        //triangle.draw(scratch)
        //triangle.draw(scratch)
        for (i in bottom)
            i.draw(scratch)
        for (i in pyramid)
            i.draw(scratch)
    }

    // vPMatrix is an abbreviation for "Model View Projection Matrix"
    private val vPMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        // initialize a triangle
        triangle = Triangle(
            floatArrayOf(
                0.0f, 0.5f, 0.0f,
                0.5f, 0.0f, 0.5f,
                0.5f, 0.0f, -0.5f
            ), floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f)
        )
        bottom = mutableListOf()
        pyramid = mutableListOf()
        var co1: Float
        var si1: Float
        var co2: Float
        var si2: Float
        var color: Float
        for (i in 0..15){
            if (i > 7)
                color = 1.5f - (i + 1).toFloat() / 16
            else
                color = 0.5f + (i + 1).toFloat() / 16
            co1 = cos(2 * PI.toFloat() * i / 16)
            co2 = cos(2 * PI.toFloat() * (i + 1 ) / 16)
            si1 = sin(2 * PI.toFloat() * i / 16)
            si2 = sin(2 * PI.toFloat() * (i + 1) / 16)
            bottom.add(Triangle(floatArrayOf(
                co1, 0f, si1,
                co2, 0f, si2,
                0f, 0f, 0f
            ), floatArrayOf(0f, 0f, 0f, 1f)))
            pyramid.add(Triangle(floatArrayOf(
                co1, 0f, si1,
                co2, 0f, si2,
                0f, 1f, 0f
            ), floatArrayOf(0f, 0f, color, 1f)))
        }
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 7f)
    }

    fun loadShader(type: Int, shaderCode: String): Int {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        return GLES20.glCreateShader(type).also { shader ->

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}