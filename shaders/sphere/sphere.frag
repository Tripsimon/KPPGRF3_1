#version 330

in vec4 vertPositionColor;
in vec2 vertTextureCoord;
in vec3 vertNormalVector;

uniform sampler2D chosenTexture;
uniform int chosenColorMode;

out vec4 outColor;

void main() {


    switch (chosenColorMode) {
    // Pozice xyz v souřadnicích pozorovatele
        case 1:
            outColor = vec4(vertPositionColor);
            break;
        case 2:
            outColor = vec4(0,0,0,gl_FragCoord.z*1000);
            break;
        case 3:
    // Normála
            outColor = vec4(vertNormalVector, 1);
            break;
        case 4:
    // Textura RGB
            outColor = texture(chosenTexture,vertTextureCoord);
            break;
        case 5:
    // Pozice UV v textuře
            outColor = vec4(vertTextureCoord, 1.f, 1.f);
            break;
        case 6:
            break;
        default:
            outColor = vec4(vertPositionColor);
            break;
    }



}
