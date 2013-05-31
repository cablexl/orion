uniform mat4 uViewProjection;   /* Complete MVP matrix */
uniform mat4 uView;             /* MV Matrix */
uniform mat4 uViewInvTrans;     /* Inverse Transpose of MV matrix */

attribute vec4 aVertex;
attribute vec2 aTexture;
attribute vec4 aNormal;
attribute vec4 aLightPosition;

varying vec2 vTexture;
varying vec4 vNormal;

varying vec4 vColor;


void main() {
    vTexture = aTexture;
    vNormal = aNormal;

    /* vertex => view space */
    vec4 vertexInView = uView * aVertex;

    /* light => view space */
    vec4 lightInView = aLightPosition;

    /* normal => view space */
    vec4 normalInView = normalize(uViewInvTrans * vNormal);

    /* distance of the light to the vertex in view space */
    float lightDistance = length(lightInView - vertexInView);

    /* direction of the light as a normalized vector */
    vec4 lightVector = normalize(lightInView - vertexInView);

    /* dot product between surface normal and light vector. */
    float diffuse = clamp(dot(normalInView, lightVector), 0.0, 1.0);

    /* distance based attenuation (pulled off of a website, dunno) */
    diffuse = diffuse * (1.0 / (0.25 * lightDistance * lightDistance));

    /*       Ambient                    Light Color             Diffuse weight */
    vColor = vec4(0.02, 0.02, 0.02, 1.0) + vec4(1.0,1.0,1.0,1.0) * diffuse;

    gl_Position =  uViewProjection * aVertex;
}