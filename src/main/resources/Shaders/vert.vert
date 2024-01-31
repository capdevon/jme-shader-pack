
#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/Instancing.glsllib"
#import "Common/ShaderLib/Skinning.glsllib"
#import "Common/ShaderLib/MorphAnim.glsllib"

attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;

varying vec3 wPosition;
varying vec3 wNormal;
varying vec2 texCoord;

#ifdef USE_VERTEX_COLOR
    attribute vec4 inColor;
    varying vec4 vertexColor;
#endif

void main() {
    
    vec4 modelSpacePos = vec4(inPosition, 1.0);
    vec3 modelSpaceNorm = inNormal;

    #ifdef NUM_MORPH_TARGETS
        Morph_Compute(modelSpacePos, modelSpaceNorm);
    #endif
    #ifdef NUM_BONES
        Skinning_Compute(modelSpacePos, modelSpaceNorm);
    #endif

    gl_Position = TransformWorldViewProjection(modelSpacePos);
    
    texCoord = inTexCoord;
    wPosition = TransformWorld(modelSpacePos).xyz;
    wNormal = TransformWorldNormal(modelSpaceNorm);
    
    #ifdef USE_VERTEX_COLOR
        vertexColor = inColor;
    #endif
    
}
