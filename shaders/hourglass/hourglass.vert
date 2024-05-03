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
out vec3 vertLightDirection;
out float vertLightSourceDistance;
const float PI = 3.1415926;

void main() {
    // Oprava z 0 - 1 na -1 - 1
    vec2 positionFixed = inPosition;
    float x = positionFixed.x * 2;
    float y = positionFixed.y * 2;
    float z = 0.0f;

    float az = x * PI;
    float ze = y * PI;
    float r = 1.0;

    x = r * cos(az) * cos(ze + changeX);
    y = r * sin(az + changeY) * cos(ze);
    z = r * 2 / (ze * 0.3f);
    vec4 viewSpace = uView * uModel * vec4(x, y, z, 1);
    vertTextureCoord = inPosition;
    gl_Position = uProj * viewSpace;

    //Normála
    vertNormalVector = transpose(inverse(mat3(uView * uModel))) * vec3(0, 0, 1);

    //Světlo
    vec3 lightSource = vec3(-1, 0, 2);
    vec4 lightSourceInViewSpace = uView * vec4(lightSource, 1);
    vertLightDirection = lightSourceInViewSpace.xyz - viewSpace.xyz;

    vertLightSourceDistance = sqrt(pow((positionFixed.x - lightSource.x), 2) + pow((positionFixed.y - lightSource.y), 2) + pow((z - lightSource.z), 2));

    //Hotova pozice bodů
    vec4 finalPosition = uProj * uView * uModel * vec4(vec3(positionFixed, z), 1.0);


    vertPositionColor = finalPosition;
}