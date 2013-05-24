uniform mat4 uViewProjection;

attribute vec4 aVertex;
attribute vec2 aTex;

varying vec2 vTex;

void main() {
    gl_Position =  uViewProjection * aVertex;
    vTex = aTex;
}