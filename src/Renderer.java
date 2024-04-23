import lwjglutils.OGLBuffers;
import lwjglutils.ShaderUtils;
import lwjglutils.ToFloatArray;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import solids.Grid;
import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import java.awt.*;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author PGRF FIM UHK
 * @version 2.0
 * @since 2019-09-02
 */
public class Renderer extends AbstractRenderer {
    private int shaderProgramTriangle, shaderProgramGrid;
    private OGLBuffers buffers;
    private Grid grid;
    private Camera camera;

    //cviceni
    private int shaderProgram, locMat;

    //OVladani myší
    private boolean mouseButton1 = false;
    double ox, oy;

    private Mat4 proj;

    @Override
    public void init() {

        camera = new Camera()
                .withPosition(new Vec3D(0f, -2f, 2.f))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-45));

        proj = new Mat4PerspRH(Math.PI / 4, 1, 0.01f, 1000.f);

        createBuffers();

        shaderProgram = ShaderUtils.loadProgram("/cube/simple");

        glUseProgram(this.shaderProgram);

        locMat = glGetUniformLocation(shaderProgram, "mat");

        camera = camera.withPosition(new Vec3D(5, 5, 2.5))
                .withAzimuth(Math.PI * 1.25)
                .withZenith(Math.PI * -0.125);

        glDisable(GL_CULL_FACE);

        //initTriangleProgram();
        //initGridProgram();
    }

    @Override
    public void display() {
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // set the current shader to be used
        glUseProgram(shaderProgram);

        glUniformMatrix4fv(locMat, false,
                ToFloatArray.convert(camera.getViewMatrix().mul(proj)));

        buffers.draw(GL_TRIANGLES, shaderProgram);
        glfwPollEvents();

        //buffers.draw(GL_TRIANGLES, shaderProgramTriangle);
        //grid.getBuffers().draw(GL_TRIANGLES, shaderProgramGrid);
    }

    private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key == GLFW_KEY_W && action == GLFW_RELEASE) {
                camera.forward(1);
                System.out.println("FORWARD");
            }
            if (key == GLFW_KEY_S && action == GLFW_RELEASE) {
                camera.backward(1);
            }
            if (key == GLFW_KEY_A && action == GLFW_RELEASE) {
                camera.left(1);
            }
            if (key == GLFW_KEY_D && action == GLFW_RELEASE) {
                camera.right(1);
            }

            if (key == GLFW_KEY_UP && action == GLFW_RELEASE) {
                camera.addZenith(0.2);
            }
            if (key == GLFW_KEY_DOWN && action == GLFW_RELEASE) {
                camera.addZenith(-0.2);
            }
            if (key == GLFW_KEY_LEFT && action == GLFW_RELEASE) {
                camera.addAzimuth(0.2);
            }
            if (key == GLFW_KEY_RIGHT && action == GLFW_RELEASE) {
                camera.addAzimuth(-0.2);
            }
        }
    };

    private GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int w, int h) {
        }
    };

    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;
            mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

            if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
                mouseButton1 = true;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                ox = xBuffer.get(0);
                oy = yBuffer.get(0);
            }

            if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE){
                mouseButton1 = false;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);
                camera = camera.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }

    };

    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mouseButton1) {
                camera = camera.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };

    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double dx, double dy) {
        }
    };


    @Override
    public GLFWKeyCallback getKeyCallback() {

        return keyCallback;
    }

    @Override
    public GLFWWindowSizeCallback getWsCallback() {
        return wsCallback;
    }

    @Override
    public GLFWMouseButtonCallback getMouseCallback() {
        return mbCallback;
    }

    @Override
    public GLFWCursorPosCallback getCursorCallback() {
        return cpCallbacknew;
    }

    @Override
    public GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }

    void createBuffers(){
        float[] cube ={
                // bottom (z-) face
                1, 0, 0,	0, 0, -1,
                0, 0, 0,	0, 0, -1,
                1, 1, 0,	0, 0, -1,
                0, 1, 0,	0, 0, -1,
                // top (z+) face
                1, 0, 1,	0, 0, 1,
                0, 0, 1,	0, 0, 1,
                1, 1, 1,	0, 0, 1,
                0, 1, 1,	0, 0, 1,
                // x+ face
                1, 1, 0,	1, 0, 0,
                1, 0, 0,	1, 0, 0,
                1, 1, 1,	1, 0, 0,
                1, 0, 1,	1, 0, 0,
                // x- face
                0, 1, 0,	-1, 0, 0,
                0, 0, 0,	-1, 0, 0,
                0, 1, 1,	-1, 0, 0,
                0, 0, 1,	-1, 0, 0,
                // y+ face
                1, 1, 0,	0, 1, 0,
                0, 1, 0,	0, 1, 0,
                1, 1, 1,	0, 1, 0,
                0, 1, 1,	0, 1, 0,
                // y- face
                1, 0, 0,	0, -1, 0,
                0, 0, 0,	0, -1, 0,
                1, 0, 1,	0, -1, 0,
                0, 0, 1,	0, -1, 0
        };
        int[] indexBufferData = new int[36];
        for (int i = 0; i<6; i++){
            indexBufferData[i*6] = i*4;
            indexBufferData[i*6 + 1] = i*4 + 1;
            indexBufferData[i*6 + 2] = i*4 + 2;
            indexBufferData[i*6 + 3] = i*4 + 1;
            indexBufferData[i*6 + 4] = i*4 + 2;
            indexBufferData[i*6 + 5] = i*4 + 3;
        }
        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 3),
                new OGLBuffers.Attrib("inNormal", 3)
        };

        buffers = new OGLBuffers(cube, attributes, indexBufferData);
        System.out.println(buffers.toString());
    }


    public void initTriangleProgram(){
        // vb
        float[] vb = {
                0.0f,  1.0f,   1.0f, 0.0f, 0.0f,
                -1.0f,  0.0f,   0.0f, 1.0f, 0.0f,
                1.0f, -1.0f,   0.0f, 0.0f, 1.0f
        };

        // ib
        int[] ib = {
                0, 1, 2
        };

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2),
                new OGLBuffers.Attrib("inColor", 3),
        };

        buffers = new OGLBuffers(vb, attributes, ib);

        shaderProgramTriangle = ShaderUtils.loadProgram("/triangle");
        glUseProgram(shaderProgramTriangle);

        int uColor = glGetUniformLocation(shaderProgramTriangle, "uColor");
        glUniform3fv(uColor, new float[] {0.0f, 1.0f, 0.0f});
    }

    public void initGridProgram(){

        // vb
        float[] vb = {
                0.0f,  1.0f,   1.0f, 0.0f, 0.0f,
                -1.0f,  0.0f,   0.0f, 1.0f, 0.0f,
                1.0f, -1.0f,   0.0f, 0.0f, 1.0f
        };

        // ib
        int[] ib = {
                0, 1, 2
        };

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2),
                new OGLBuffers.Attrib("inColor", 3),
        };

        grid = new Grid(15, 15);
        shaderProgramGrid = ShaderUtils.loadProgram("/grid");
        glUseProgram(shaderProgramGrid);

        int uView = glGetUniformLocation(shaderProgramGrid, "uView");
        glUniformMatrix4fv(uView, false, camera.getViewMatrix().floatArray());

        int uProj = glGetUniformLocation(shaderProgramGrid, "uProj");
        glUniformMatrix4fv(uProj, false, proj.floatArray());

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

}