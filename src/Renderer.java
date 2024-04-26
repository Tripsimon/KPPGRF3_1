import lwjglutils.OGLBuffers;
import lwjglutils.OGLModelOBJ;
import lwjglutils.ShaderUtils;
import lwjglutils.ToFloatArray;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import solids.Grid;
import transforms.*;

import java.nio.DoubleBuffer;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author PGRF FIM UHK
 * @version 2.0
 * @since 2019-09-02
 */
public class Renderer extends AbstractRenderer {

    //Shader
    private int shaderProgramTriangle, shaderProgramGrid;

    private Camera camera;

    //cviceni
    private int shaderProgram, locMat;

    //OVladani programu
    private boolean mouseButton1 = false;
    private boolean mouseButton2 = false;
    double ox, oy;

    //Proměné pro vykreslení
    private Mat4 proj,orthProj;
    private OGLBuffers buffers;
    private Grid grid;
    private OGLModelOBJ sphereModel,teapotModel,swordModel;

    //Uživatelské nastavení
    private int chosenSolid = 1;
    private int chosenForm = GL_FILL;
    private int chosenRenderForm = GL_TRIANGLES;

    @Override
    public void init() {

        camera = new Camera()
                .withPosition(new Vec3D(0f, -2f, 2.f))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-45));

        proj = new Mat4PerspRH(Math.PI / 4, 1, 0.01f, 1000.f);
        orthProj = new Mat4OrthoRH(Math.PI , Math.PI , 0.1f, 1000.f);


        //Objekty
        sphereModel = new OGLModelOBJ("/obj/sphere.obj");
        teapotModel = new OGLModelOBJ("/obj/teapot.obj");
        swordModel = new OGLModelOBJ("/obj/sword.obj");


        shaderProgram = ShaderUtils.loadProgram("/cube/simple");
        shaderProgramGrid = ShaderUtils.loadProgram("/grid");

        //createBuffersFlatness();

        glUseProgram(this.shaderProgram);

        locMat = glGetUniformLocation(shaderProgram, "mat");

        camera = camera.withPosition(new Vec3D(5, 5, 2.5))
                .withAzimuth(Math.PI * 1.25)
                .withZenith(Math.PI * -0.125);

        glDisable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    public void display() {
        //Nastavení velikosti obrazovky
        glViewport(0, 0, width, height);

        //Vyčištění obrazovky
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        //Měnitelné nastavení uživatelem
        glPolygonMode(GL_FRONT_AND_BACK, chosenForm);

        //Výběr tělesa


        switch (chosenSolid) {
            case 1:
                createBuffersGrid();
                break;

            case 2:
                createBuffersCube();
                break;

            case 3:
                createBuffersPike();
                break;

            case 4:
                createBuffersSphere();
                break;

            case 5:
                createBuffersTeapot();
                break;

            case 6:
                createBuffersSword();
                break;

            default:
                break;
        }



        //Nastavení používaného shaderu
        glUseProgram(shaderProgram);

        //Projekce
        glUniformMatrix4fv(locMat, false, ToFloatArray.convert(camera.getViewMatrix().mul(proj)));

        // Vlastní vykreslení objektu
        buffers.draw(chosenRenderForm, shaderProgram);

        // Zpracování volaných eventů
        glfwPollEvents();
    }


    void createBuffersGrid(){
        grid = new Grid(15, 15);
        buffers = grid.getBuffers();
    }

    void createBuffersFlatness(){

        float[] vertexBuffer ={

                0, 0, 0,	1, 0, 0,
                1, 0, 0,	0, 1, 0,
                0, 1, 0,	0, 0, 1,
                1, 1, 0,	1, 0, 0,

                1, 0, 0,	1, 0, 0,
                2, 0, 0,	0, 1, 0,
                1, 1, 0,	0, 0, 1,
                2, 1, 0,	0, 0, 0,

                0, 1, 0,	1, 0, 0,
                1, 1, 0,	0, 1, 0,
                0, 2, 0,	0, 0, 1,
                1, 2, 0,	0, 0, 0,

                1, 1, 0,	1, 0, 0,
                2, 1, 0,	0, 1, 0,
                1, 2, 0,	0, 0, 1,
                2, 2, 0,	0, 0, 0,


                0, 2, 0,	1, 0, 0,
                1, 2, 0,	0, 1, 0,
                0, 3, 0,	0, 0, 1,
                1, 3, 0,	0, 0, 0,

                1, 2, 0,	1, 0, 0,
                2, 2, 0,	0, 1, 0,
                1, 3, 0,	0, 0, 1,
                2, 3, 0,	0, 0, 0,

                //Vrchní strana

        };
        int[] indexBuffer = {
                0, 1, 2, 1, 2, 3,

                4, 5, 6, 5, 6, 7,

                8, 9, 10, 9, 10, 11,

                12, 13, 11, 13, 14, 15,

                16, 17, 18, 17, 18, 19,

                20, 21, 22, 21, 22, 23

        };

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 3),
                new OGLBuffers.Attrib("inNormal", 3)
        };

        buffers = new OGLBuffers(vertexBuffer, attributes, indexBuffer);
    }

    void createBuffersCube(){

        float[] vertexBuffer ={

                //Spodní strana
                1, 0, 0,	0, 0, -1,
                0, 0, 0,	0, 0, -1,
                1, 1, 0,	0, 0, -1,
                0, 1, 0,	0, 0, -1,

                //Vrchní strana
                1, 0, 1,	0, 0, 1,
                0, 0, 1,	0, 0, 1,
                1, 1, 1,	0, 0, 1,
                0, 1, 1,	0, 0, 1,

                //Pravá strana
                1, 1, 0,	1, 0, 0,
                1, 0, 0,	1, 0, 0,
                1, 1, 1,	1, 0, 0,
                1, 0, 1,	1, 0, 0,

                //Levá strana
                0, 1, 0,	-1, 0, 0,
                0, 0, 0,	-1, 0, 0,
                0, 1, 1,	-1, 0, 0,
                0, 0, 1,	-1, 0, 0,

                //Přední strana
                1, 1, 0,	0, 1, 0,
                0, 1, 0,	0, 1, 0,
                1, 1, 1,	0, 1, 0,
                0, 1, 1,	0, 1, 0,

                //Zadní strana
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

        buffers = new OGLBuffers(vertexBuffer, attributes, indexBufferData);
    }

    // !! POZOR !! NENí JEHLAN SCHVALNE. Tenhle obrazec je trochu vic interesting. Aby to byl jehlan, musel bych trochu proházet index vertex tak aby se nekřížily trojuhelniky
    void createBuffersPike(){

        float[] vertexBuffer ={
                0, 0, 0,	1, 0, 0,
                1, 0, 0,	0, 1, 0,
                0, 1, 0,	0, 0, 1,
                1, 1, 0,	1, 0, 0,

                0.5f,0.5f,1,   1, 1, 1
        };
        int[] indexBuffer = {
                0, 1, 2, 1, 2, 3,

                0, 1, 4, 1, 2, 4, 2, 3, 4, 3, 0, 4
        };

        OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 3),
                new OGLBuffers.Attrib("inNormal", 3)
        };

        buffers = new OGLBuffers(vertexBuffer, attributes, indexBuffer);
    }

    void createBuffersSphere(){
        buffers = sphereModel.getBuffers();
    }

    void createBuffersTeapot(){
        buffers = teapotModel.getBuffers();
    }

    void createBuffersSword(){
        buffers = swordModel.getBuffers();
    }


    private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {

            //Kamera
            if (key == GLFW_KEY_W && action == GLFW_RELEASE) {
                camera = camera.forward(1);
            }
            if (key == GLFW_KEY_S && action == GLFW_RELEASE) {
                camera = camera.backward(1);
            }
            if (key == GLFW_KEY_A && action == GLFW_RELEASE) {
                camera = camera.left(1);
            }
            if (key == GLFW_KEY_D && action == GLFW_RELEASE) {
                camera = camera.right(1);
            }

            //Změna tělesa
            if (key == GLFW_KEY_Q && action == GLFW_RELEASE) {
                chosenSolid++;
                if(chosenSolid ==7){
                    chosenSolid = 1;
                }
            }

            //Změna zobrazovací formy
            if (key == GLFW_KEY_E && action == GLFW_RELEASE) {
                switch (chosenForm){
                    case GL_FILL:
                        chosenForm = GL_LINE;
                        break;
                    case GL_LINE:
                        chosenForm = GL_POINT;
                        break;
                    case GL_POINT:
                        chosenForm = GL_FILL;
                        break;

                    default:
                        break;
                }
            }

            //Změna formy renderovani
            if (key == GLFW_KEY_R && action == GLFW_RELEASE) {
                chosenRenderForm = (chosenRenderForm == GL_TRIANGLES) ? GL_TRIANGLE_STRIP : GL_TRIANGLES;
            }

        }
    };

    private GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
        @Override
        public void invoke(long window, int w, int h) {
            if (w > 0 && h > 0 && (w != width || h != height)) {
                width = w;
                height = h;
            }
        }
    };

    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;
            mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

            /**
             * Pohyb kamery pomoci myši
             */
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

            if (button==GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS){
                mouseButton2 = true;
            }
            if (button==GLFW_MOUSE_BUTTON_2 && action == GLFW_RELEASE){
                mouseButton2 = false;
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



}