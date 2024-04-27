in vec2 inPosition;

uniform mat4 uModel;
uniform mat4 uView;
uniform mat4 uProj;

void main() {
    // Oprava z 0 - 1 na -1 - 1
    vec2 position = inPosition * 2 - 1;
    float x = position.x;
    float y = position.y;
    float z = sin(x*3) * cos(y*3);

    //Uprava pozice
    position = vec2(x, y);

    //Hotova pozice bodů
    vec3 finalPosition = vec3(position, z);
    gl_Position = uProj * uView * uModel * vec4(finalPosition, 1.0);
}