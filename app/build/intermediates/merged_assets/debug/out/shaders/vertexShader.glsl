#version 300 es
precision highp float;

precision highp float;


in vec3 position;
in vec2 textureCoords;
in vec3 normal;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 lightPosition[4];

out vec2 passTextureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[4];
out vec3 toCameraVector;

void main(void){

    vec4 worldPosition = transformationMatrix * vec4(position,1.0);

    gl_Position =  projectionMatrix * viewMatrix * worldPosition;
    passTextureCoords = textureCoords;

    surfaceNormal = (transformationMatrix * vec4(normal,0.0)).xyz;
    for(int i = 0; i<4;i++){
        toLightVector[i] = lightPosition[i] - worldPosition.xyz;
    }
    toCameraVector = (inverse(viewMatrix) * vec4(0,0,0,1.0)).xyz - worldPosition.xyz;

}
