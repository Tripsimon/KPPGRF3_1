#version 330

in vec4 vertPositionColor;
in vec2 vertTextureCoord;
in vec3 vertNormalVector;

uniform sampler2D textureBricks;
uniform int chosenColorMode;

out vec4 outColor;

void main() {


    switch (chosenColorMode) {
        case 1:
    // Pozice xyz v souřadnicích pozorovatele
            outColor = vec4(vertPositionColor);
            break;
        case 2:
            break;
        case 3:
    // Normála
            outColor = vec4(vertNormalVector, 1);
            break;
        case 4:
            outColor = texture(textureBricks,vertTextureCoord);
            break;
        case 5:
            break;
        case 6:
            break;
        default:
            outColor = vec4(vertPositionColor);
            break;
    }



    //outColor = vec4(vertPositionColor);
}
