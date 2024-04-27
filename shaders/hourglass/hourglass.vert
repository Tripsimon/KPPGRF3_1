in vec2 inPosition;

uniform mat4 uView;
uniform mat4 uProj;
uniform mat4 uModel;
uniform float changeX;
uniform float changeY;

const float PI = 3.1415;

void main() {
    // Oprava z 0 - 1 na -1 - 1
    vec2 position = inPosition;
    float x = position.x*2;
    float y = position.y*2;
    float z = 0.0f;

    float az = x * PI;
    float ze = y * PI ;
    float r = 1.0;

     x = r * cos(az) * cos(ze +changeX);
     y =  r * sin (az + changeY) * cos(ze);
     z =  r*2 / (ze*0.3f);

    //Uprava pozice
    position = vec2(x, y);

    //Finalni slození pozice
    vec3 finalPosition = vec3(position, z);
    gl_Position = uProj * uView * uModel * vec4(finalPosition, 1.0);
}