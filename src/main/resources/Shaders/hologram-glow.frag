
#import "Common/ShaderLib/GLSLCompat.glsllib"

uniform vec3 g_CameraPosition;

uniform vec4 m_Color;
uniform float m_Intensity;

varying vec3 wPosition;
varying vec3 wNormal;

#ifdef USE_VERTEX_COLOR
    varying vec4 vertexColor;
#endif

void main() {
    
    #ifndef USE_VERTEX_COLOR
        gl_FragColor = m_Color;
    #else
        gl_FragColor = vertexColor;
    #endif
    
    vec3 camDir = normalize(wPosition - g_CameraPosition);
    float fresnel = 1.0 - abs(dot(camDir, wNormal));
    gl_FragColor = mix(vec4(0.0), gl_FragColor * m_Intensity, fresnel);
    
}
