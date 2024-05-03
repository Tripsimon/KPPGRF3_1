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
    vec2 positionFixed = inPosition * 2 - 1;
    vec4 viewSpace = uView * uModel * vec4(positionFixed, 0 , 1);
    gl_Position = uProj * viewSpace;


    // Proměné pro výpočty
    float x = positionFixed.x + changeX;
    float y = positionFixed.y + changeY;
    float z = 0.0f;
    vertTextureCoord = inPosition;


    vertNormalVector = transpose(inverse(mat3(uView * uModel))) * vec3(0, 0, 1);


    //Hotova pozice bodů
    vec4 finalPosition = uProj * uView * uModel * vec4(vec3(positionFixed, z), 1.0);
    vertPositionColor = finalPosition;


}

