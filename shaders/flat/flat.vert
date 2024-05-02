#version 330

in vec2 inPosition;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProj;
uniform float changeX;
uniform float changeY;
uniform int ChangeColorMode;

out vec4  vertPositionColor;

const float PI=3.1415926;
void main() {
    // Oprava z 0 - 1 na -1 - 1
    vec2 position = inPosition * 2 - 1;
    float x = position.x + changeX;
    float y = position.y + changeY;
    float z = 0.0f;



    //Výpočet normály
    float a = x*PI*2;
    float zNorm = y*PI - PI/2;
    vec3 dx = vec3(-3*sin(a)*cos(zNorm)*PI*2, 2*cos(a)*cos(zNorm)*2*PI,0);

    vec3 dy = vec3(-3*cos(a)*sin(zNorm)*PI,
                   -2*sin(a)*sin(zNorm)*PI,
                   cos(zNorm)*PI
    );
    vec3 normal =  cross(dx,dy);

    //Uprava pozice
    position = vec2(x, y);

    //Hotova pozice bodů
    vec4 finalPosition = uProj * uView * uModel * vec4(vec3(position, z), 1.0);

    //vertPositionColor = (finalPosition);
    //vertPositionColor = vec4( normal,1);
    vertPositionColor = finalPosition.
    gl_Position = finalPosition;
}
