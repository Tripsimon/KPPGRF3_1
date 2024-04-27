in vec2 inPosition;

uniform mat4 uView;
uniform mat4 uProj;

void main() {
    vec2 newPos = inPosition * 2 - 1;
    gl_Position = uProj * uView * vec4(newPos, 0.0, 1.0);
}