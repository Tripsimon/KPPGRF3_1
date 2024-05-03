#version 330

in vec3 vertexPosition;
in vec2 inPosition;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProj;
uniform float changeX;
uniform float changeY;
uniform int chosenColorMode;

out vec4 vertPositionColor;
out vec3 vertNormalVector;
out vec2 vertTextureCoord;

const float PI = 3.1415926;

void main() {
    // Oprava z 0 - 1 na -1 - 1
    vec2 position = inPosition;
    float x = position.x*2;
    float y = position.y*2;
    float z = 0.0f;

    float az = x * PI;
    float ze = y * PI ;
    float r = 1.0;

     x = r * cos(az) * cos(ze) * sin(changeY +1);
     y =  r * sin (az) * cos(ze) * sin(changeX +1);
     z =  r * sin(ze);
    vertTextureCoord = vec2(x,y);

    //Uprava pozice
    position = vec2(x, y);

    //Finalni slození pozice
    vec3 finalPosition = vec3(position, z);
    gl_Position = uProj * uView * uModel * vec4(finalPosition, 1.0);

    vertPositionColor = vec4(finalPosition,1);
}