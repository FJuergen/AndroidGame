#version 300 es

in vec2 passTextureCoords;

out vec4 out_colour;

uniform sampler2D textureSampler;

void main(void){
    out_colour = texture(textureSampler,passTextureCoords);
}