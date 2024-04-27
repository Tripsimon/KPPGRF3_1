in vec2 inPosition;

uniform mat4 uView;
uniform mat4 uProj;

const float PI = 3.1415;

void main() {
    // Oprava z 0 - 1 na -1 - 1
    vec2 position = inPosition;
    float x = position.x;
    float y = position.y;
    float z = 0f;

    float az = x * PI;
    float ze = y * PI ;
    float r = 1.0;

     x = r * cos(az) * cos(ze);
     y =  r * sin (az) * cos(ze);
     z =  r * sin(ze);

    //Uprava pozice
    position = vec2(x, y);

    //Finalni slození pozice
    vec3 finalPosition = vec3(position, -z);
    gl_Position = uProj * uView * vec4(finalPosition, 1.0);
}