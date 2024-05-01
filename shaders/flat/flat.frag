#version 330

in vec4 vertPositionColor;

out vec4 outColor;

void main() {
    outColor = vec4(vertPositionColor);
}
