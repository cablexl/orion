uniform mat4 uViewProjection;
attribute vec4 aVertex;

void main() {
    gl_Position =  uViewProjection * aVertex;
}