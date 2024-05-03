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

/*
        //Vypocet normaly
        float a = inPosition.x * PI * 2.;
        float normz = inPosition.y * PI - PI / 2.;
        vec3 dx = vec3(-3 * sin(a) * cos(normz) * PI * 2.,
                       2. * cos(a) * cos(normz) * 2. * PI,
                       0.
        );
        vec3 dy = vec3(-3. * cos(a) * sin(normz) * PI,
                       -2. * sin(a) * sin(normz) * PI,
                       cos(normz) * PI
        );
        normal = normalize(cross(dx, dy));
        */


    vertNormalVector = transpose(inverse(mat3(uView * uModel))) * vec3(0, 0, 1);


    //Uprava pozice

    //Hotova pozice bodů
    vec4 finalPosition = uProj * uView * uModel * vec4(vec3(positionFixed, z), 1.0);




    //Color Mode 1 - Pozice xyz – v souřadnicích pozorovatele
    vertPositionColor = (finalPosition);

    //Color Mode 2 - Hloubka – informace v depth bufferu

    //Color Mode 3 - Normála xyz – v soustavě pozorovatele
    //vertPositionColor = vec4(normal, 1);

    //Color Mode 4 - Mapovaná textura rgba

    // Color Mode 5 - Souřadnice do textury uv

    //vertPositionColor = vec4(vec3(gl_FragCoord.z), 1.0);
    //vertPositionColor = vec4( normal,1);

}

