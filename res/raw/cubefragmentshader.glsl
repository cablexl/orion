precision mediump float;
uniform vec4 uColor;
uniform sampler2D uTexture;

varying vec2 vTex;
varying vec4 vNormal;

void main() {
    /* Use to print normal color to quad: */
    /*gl_FragColor = vec4(vNormal.xyz, 1.0);*/
    gl_FragColor = texture2D(uTexture, vTex);
}