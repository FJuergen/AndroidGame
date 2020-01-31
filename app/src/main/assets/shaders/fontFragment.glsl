#version 300 es
precision highp float;

in vec2 pass_textureCoords;

out vec4 out_colour;

uniform vec3 colour;
uniform sampler2D fontAtlas;

void main(void){

    out_colour = vec4(colour, texture(fontAtlas,pass_textureCoords).a);

}