import lwjglutils.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import solids.Grid;
import transforms.*;

import java.io.IOException;
import java.nio.DoubleBuffer;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
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
    private int shaderProgram, shaderProgramSecondthing;

    private float rotation = 0;
    private float changeX, changeY = 0;

    private int loadedShaderProgramFlat, loadedShaderProgramFunction ,loadedShaderProgramSphere, loadedShaderProgramHourglass, loadedShaderProgramCylinder, loadedShaderProgramAbomination;

    //Ovladani programu
    private boolean mouseButton1 = false;
    private boolean mouseButton2 = false;
    double ox, oy;
    double mouseRotateX, mouseRotateY, mouseRotateXOffset, mouseRotateYOffset;
    double mouseResize = 1;
    double translateX = 0;
    double translateY = 0;
    float rotSecondthing = 0;

    //Proměné pro vykreslení
    private Mat4 perspProj,orthProj;
    private OGLBuffers buffers;
    private Grid grid;

    //Uživatelské nastavení
    private int chosenSolid = 1;
    private int chosenShaderProgram = 1;
    private int chosenForm = GL_LINE;
    private int chosenRenderForm = GL_TRIANGLES;
    private int chosenProjection = 1;
    private int chosenAnimation = 1;
    private int chosenColorMode = 1;

    //Textury
    private OGLTexture2D textureBricks, textureGlobe, textureTypewriter, textureBaratheon;

    @Override
    public void init() {
        //Inicializace kamery
        camera = new Camera()
                .withPosition(new Vec3D(0f, -2f, 2.f))
                .withAzimuth(Math.toRadians(90))
                .withZenith(Math.toRadians(-45));

        //Projekční matice
        perspProj = new Mat4PerspRH(Math.PI / 4, 1, 0.01f, 1000.f);
        orthProj = new Mat4OrthoRH(Math.PI , Math.PI , 0.1f, 1000.f);


        try {
            loadedShaderProgramFlat = ShaderUtils.loadProgram("/flat/flat");
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            loadedShaderProgramFunction = ShaderUtils.loadProgram("/function/function");
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            loadedShaderProgramSphere = ShaderUtils.loadProgram("/sphere/sphere");
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            loadedShaderProgramHourglass = ShaderUtils.loadProgram("/hourglass/hourglass");
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            loadedShaderProgramCylinder = ShaderUtils.loadProgram("/cylinder/cylinder");
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            loadedShaderProgramAbomination = ShaderUtils.loadProgram("/abomination/abomination");
        }catch (Exception e){
            e.printStackTrace();
        }

        // Načtení textur
        try {
            textureBricks = new OGLTexture2D("./textures/bricks.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            textureGlobe = new OGLTexture2D("./textures/globe.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            textureTypewriter = new OGLTexture2D("./textures/typewriter.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            textureBaratheon = new OGLTexture2D("./textures/baratheon.png");
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Shader programy
        shaderProgram = loadedShaderProgramFlat;
        shaderProgramSecondthing = loadedShaderProgramSphere;


        //Inicializace mřížky pro vykreslování
        createBuffersGrid();

        //Vybrání základního nastavení shaderu
        glUseProgram(this.shaderProgram);

        //Inicializace kamery
        camera = camera.withPosition(new Vec3D(5, 5, 2.5))
                .withAzimuth(Math.PI * 1.25)
                .withZenith(Math.PI * -0.125);


        //Shader proměnmé


        //Misc nastavení
        glPointSize(3);
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
        rotSecondthing += 0.02f;

        //Výběr shaderu
        switch (chosenShaderProgram) {
            case 1:
                shaderProgram = loadedShaderProgramFlat;
                break;

            case 2:
                shaderProgram = loadedShaderProgramFunction;
                break;

            case 3:
                shaderProgram = loadedShaderProgramSphere;
                break;

            case 4:
                shaderProgram = loadedShaderProgramHourglass;
                break;

            case 5:
                shaderProgram = loadedShaderProgramCylinder;
                break;

            case 6:
                shaderProgram = loadedShaderProgramAbomination;
                break;

            default:
                break;
        }


        //Nastavení používaného shaderu
        glUseProgram(shaderProgram);

        // Bindování textury
        textureBricks.bind(shaderProgram, "textureBricks", 0);
        textureGlobe.bind(shaderProgram, "textureGlobe", 1);

        //Uniform view matice
        int uView = glGetUniformLocation(shaderProgram, "uView");
        glUniformMatrix4fv(uView, false, camera.getViewMatrix().floatArray());

        //Uniform projekční matice
        int uProj = glGetUniformLocation(shaderProgram, "uProj");
        if (chosenProjection == 1) {
            glUniformMatrix4fv(uProj, false, perspProj.floatArray());
        }else {
            glUniformMatrix4fv(uProj, false, orthProj.floatArray());
        }

        //Přepínání možností animací
        switch (chosenAnimation){
            case 2:
                changeX += 0.02f;
                break;

            case 3:
                changeY += 0.02f;
                break;

            case 4:
                changeX += 0.02f;
                changeY += 0.02f;
                break;

            case 5:
                changeX -= 0.02f;
                changeY -= 0.02f;
                break;

            default:
                break;
        }

        //Proměné pro animace
        int changeXLoc = glGetUniformLocation(shaderProgram,"changeX");
        glUniform1f(changeXLoc,changeX);
        int changeYLoc = glGetUniformLocation(shaderProgram,"changeY");
        glUniform1f(changeYLoc,changeY);

        //Proměná shaderu pro změnu modu barev
        int changeColorModeLoc = glGetUniformLocation(shaderProgram,"chosenColorMode");
        glUniform1i(changeColorModeLoc,chosenColorMode);


        //Proměné pro transformace
        Mat4 rotationX = new Mat4RotX(mouseRotateX);
        Mat4 rotationY = new Mat4RotY(mouseRotateY);
        Mat4 resize = new Mat4Scale(mouseResize);
        Mat4 translate = new Mat4Transl(translateX, translateY,0f);
        Mat4 model = rotationX.mul(rotationY);
        model = model.mul(resize);
        model =  model.mul(translate);
        int uModel = glGetUniformLocation(shaderProgram, "uModel");
        glUniformMatrix4fv(uModel, false, model.floatArray());


        // Vlastní vykreslení objektu
        buffers.draw(chosenRenderForm, shaderProgram);

        //Druhý solid
        glUseProgram(shaderProgramSecondthing);
        textureBaratheon.bind(shaderProgramSecondthing, "chosenTexture", 0);
         changeColorModeLoc = glGetUniformLocation(shaderProgramSecondthing,"chosenColorMode");
        glUniform1i(changeColorModeLoc,4);

         uView = glGetUniformLocation(shaderProgramSecondthing, "uView");
        glUniformMatrix4fv(uView, false, camera.getViewMatrix().floatArray());

         uProj = glGetUniformLocation(shaderProgramSecondthing, "uProj");
        if (chosenProjection == 1) {
            glUniformMatrix4fv(uProj, false, perspProj.floatArray());
        }else {
            glUniformMatrix4fv(uProj, false, orthProj.floatArray());
        }

        int uModelSecond = glGetUniformLocation(shaderProgramSecondthing, "uModel");
        Mat4 modelSecondthing = new Mat4Transl(0,0,0);
        modelSecondthing = modelSecondthing.mul(new Mat4RotX(rotSecondthing));
        modelSecondthing = modelSecondthing.mul(new Mat4Transl(sin(rotSecondthing)*7,cos(rotSecondthing)*7,0));
        glUniformMatrix4fv(uModelSecond, false, modelSecondthing.floatArray());

        buffers.draw(chosenRenderForm, shaderProgramSecondthing);

        // Zpracování volaných eventů
        glfwPollEvents();
    }


    // POuze příprava gridu
    void createBuffersGrid(){
        grid = new Grid(25, 25);
        buffers = grid.getBuffers();
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

            //Změna shaderu
            if (key == GLFW_KEY_F && action == GLFW_RELEASE) {
                chosenShaderProgram++;
                if(chosenShaderProgram ==7){
                    chosenShaderProgram = 1;
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

            //Reset transofmraci
            if (key == GLFW_KEY_T && action == GLFW_RELEASE) {
                mouseRotateX = 0;
                mouseRotateXOffset = 0;
                mouseRotateY = 0;
                mouseRotateYOffset = 0;
                mouseResize = 1;
            }

            if (key == GLFW_KEY_UP && action == GLFW_RELEASE && mouseButton2) {
                translateX +=1;
            }

            if (key == GLFW_KEY_DOWN && action == GLFW_RELEASE && mouseButton2) {
                translateX -=1;
            }

            if (key == GLFW_KEY_LEFT && action == GLFW_RELEASE && mouseButton2) {
                translateY +=1;
            }

            if (key == GLFW_KEY_RIGHT && action == GLFW_RELEASE && mouseButton2) {
                translateY -=1;
            }

            //Změna projekce
            if (key == GLFW_KEY_P && action == GLFW_RELEASE) {
                chosenProjection = (chosenProjection == 1) ? 2 : 1;
            }

            //Zapnutí animace
            if (key == GLFW_KEY_G && action == GLFW_RELEASE) {
                changeX = 0;
                changeY = 0;
                chosenAnimation++;
                if(chosenAnimation == 6){
                    chosenAnimation = 1;
                }
            }

            //Změna modu obarvení
            if(key == GLFW_KEY_L && action == GLFW_RELEASE){
                chosenColorMode++;
                if (chosenColorMode == 9){chosenColorMode = 1;}
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

                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                mouseRotateXOffset = xBuffer.get(0);
                mouseRotateYOffset = yBuffer.get(0);

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
                        .addZenith((double) Math.PI * (oy - y) / height);
                ox = x;
                oy = y;
            }

            if (mouseButton2) {
                mouseRotateX += (double) Math.PI *  (mouseRotateXOffset - x) / width;
                mouseRotateY += (double) Math.PI *  (mouseRotateYOffset - y) / height;
                mouseRotateXOffset = x;
                mouseRotateYOffset = y;
            }
        }
    };

    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double dx, double dy) {
            if (mouseButton2){
                mouseResize += dy/10;
            }
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