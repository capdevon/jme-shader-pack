
#import "Common/ShaderLib/GLSLCompat.glsllib"

uniform vec3 g_CameraPosition;
uniform float g_Time;

uniform vec4 m_Color;
uniform vec3 m_Direction;
uniform float m_ScanOffset;
uniform float m_LineFrequency;
uniform float m_ScanFrequency;
uniform float m_LineSpeed;
uniform float m_ScanSpeed;
uniform float m_ScanMinimum;
uniform float m_Intensity;

varying vec3 wPosition;
varying vec3 wNormal;

#ifdef USE_VERTEX_COLOR
    varying vec4 vertexColor;
#endif

float signum(float a) {
    if (a > 0.0) return 1.0;
    else return -1.0;
}
float sawWave(float a) {
    return sin(a) * signum(cos(a));
}

void main() {
    
    vec3 camDir = normalize(wPosition - g_CameraPosition);
    float scanDir = signum(m_ScanSpeed);    
    float a = dot(m_Direction, wPosition) + m_ScanOffset;
    float line = (sin((a + g_Time * m_LineSpeed) * m_LineFrequency) + 1.0) / 2.0;    
    float fresnel = 1.0 - abs(dot(camDir, wNormal));
    float scan = (sawWave((a + g_Time * -m_ScanSpeed) * scanDir * m_ScanFrequency) + 1.0) / 2.0;
    
    #ifndef USE_VERTEX_COLOR
        gl_FragColor = m_Color;
    #else
        gl_FragColor = vertexColor;
    #endif
    
    gl_FragColor.a = line * fresnel * m_Intensity + fresnel/2.0;
    gl_FragColor = mix(gl_FragColor, gl_FragColor * m_ScanMinimum, scan) * gl_FragColor.a;
    
}
