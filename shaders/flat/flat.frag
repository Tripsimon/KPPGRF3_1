#version 330

in vec4 vertPositionColor;
in vec2 vertTextureCoord;
in vec3 vertNormalVector;
in vec3 vertLightDirection;
in float vertLightSourceDistance;

uniform sampler2D textureBricks;
uniform int chosenColorMode;

out vec4 outColor;

void main() {

    //Phongovo osvětlení
    vec4 ambientColor = vec4(0.3, 0.3, 0.3, 1.0);
    vec4 diffuseColor = vec4(1, 1.0, 1.0, 1.0);

    vec3 ld = normalize(vertLightDirection);
    vec3 nd = normalize(vertNormalVector);

    float nDotL = max(dot(nd, ld), 0.0);
    vec4 totalDiffuse = nDotL * diffuseColor;


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
            // Osvětlení bez textury
            outColor = (ambientColor + totalDiffuse) * vec4(1, 1., 1, 1);
            break;

        case 7:
            // osvětlení s texturou
            outColor = texture(textureBricks,vertTextureCoord) * (ambientColor + totalDiffuse) * vec4(1, 1., 1, 1);
            break;

        case 8:
            // Vzdalenost od zdroje světla

            outColor = vec4(length(vertLightSourceDistance),length(vertLightSourceDistance),length(vertLightSourceDistance),1);
            break;

        default:
            outColor = vec4(vertPositionColor);
            break;
    }



    //outColor = vec4(vertPositionColor);
}
