#version 330

in vec4 vertPositionColor;
in vec2 vertTextureCoord;
in vec3 vertNormalVector;

uniform sampler2D textureBricks;
uniform int chosenColorMode;

out vec4 outColor;

void main() {


    switch (chosenColorMode) {
        // Pozice xyz v souřadnicích pozorovatele
        case 1:
            outColor = vec4(vertPositionColor);
            break;

        // Depth buffer
        case 2:
            float depth = gl_FragCoord.z / (gl_FragCoord.w);
            depth = clamp(depth, 0.0, 8);
            depth = 1.0 - (depth/8);
            outColor = vec4(vec3(depth), 1.0);
            break;
        case 3:
            // Normála
            outColor = vec4(vertNormalVector, 1);
            break;
        case 4:
            // Textura RGB
            outColor = texture(textureBricks,vertTextureCoord);
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



    //outColor = vec4(vertPositionColor);
}
