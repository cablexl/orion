uniform mat4 uViewProjection;

attribute vec4 aVertex;
attribute vec2 aTex;
attribute vec4 aNormal;

varying vec2 vTex;
varying vec4 vNormal;

void main() {
    gl_Position =  uViewProjection * aVertex;
    vTex = aTex;
    vNormal = aNormal;
}