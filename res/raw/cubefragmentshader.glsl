precision mediump float;
uniform sampler2D uTexture;

varying vec2 vTexture;
varying vec4 vColor;

void main() {
    gl_FragColor = texture2D(uTexture, vTexture) * vColor;
}