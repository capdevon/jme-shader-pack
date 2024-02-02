package com.github.mat.editor;

import java.util.Collection;

import com.jme3.asset.TextureKey;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.TextureCubeMap;

/**
 * The implementation of a material serializer.
 *
 * @author JavaSaBr
 */
public class MaterialSerializer {
    
    /**
     * Serialize to string string.
     *
     * @param material the material
     * @return the string
     */
    public static String serializeToString(Material material) {

        final MaterialDef materialDef = material.getMaterialDef();
        final Collection<MatParam> params = material.getParams();

        StringBuilder sb = new StringBuilder();
        sb.append("Material MyMaterial : ").append(materialDef.getAssetName()).append(" {\n");
        sb.append("    MaterialParameters {\n");
        params.forEach(matParam -> addMaterialParameter(sb, matParam));
        sb.append("    }\n");
        sb.append("    AdditionalRenderState {\n");

        RenderState renderState = material.getAdditionalRenderState();
        BlendMode blendMode = renderState.getBlendMode();
        FaceCullMode faceCullMode = renderState.getFaceCullMode();

        if (blendMode != RenderState.BlendMode.Off) {
            sb.append("      Blend ").append(blendMode.name()).append('\n');
        }
        if (faceCullMode != RenderState.FaceCullMode.Back) {
            sb.append("      FaceCull ").append(faceCullMode.name()).append('\n');
        }
        if (renderState.isWireframe()) {
            sb.append("      Wireframe On\n");
        }
        if (!renderState.isDepthTest()) {
            sb.append("      DepthTest Off\n");
        }
        if (!renderState.isDepthWrite()) {
            sb.append("      DepthWrite Off\n");
        }
        if (!renderState.isColorWrite()) {
            sb.append("      ColorWrite Off\n");
        }

        float polyOffsetFactor = renderState.getPolyOffsetFactor();
        float polyOffsetUnits = renderState.getPolyOffsetUnits();

        if (polyOffsetFactor != 0 || polyOffsetUnits != 0) {
            sb.append("      PolyOffset ")
                    .append(polyOffsetFactor)
                    .append(' ')
                    .append(polyOffsetUnits)
                    .append('\n');
        }

        sb.append("    }\n");
        sb.append("}");

        return sb.toString();
    }

    /**
     * Add the material parameter to the builder.
     *
     * @param builder the builder.
     * @param matParam the material parameter.
     */
    private static void addMaterialParameter(StringBuilder builder, MatParam matParam) {

        String value = toString(matParam.getVarType(), matParam.getValue());
        if (value.isEmpty()) {
            return;
        }

        builder.append("        ")
                .append(matParam.getName())
                .append(" : ")
                .append(value)
                .append('\n');
    }

    private static String toString(VarType varType, Object value) {

        switch (varType) {
            case Int:
            case Float:
            case Boolean:
                return String.valueOf(value);

            case Vector4: {
                if (value instanceof ColorRGBA) {
                    ColorRGBA color = (ColorRGBA) value;
                    return color.getRed() + " " + color.getGreen() + " " + color.getBlue() + " " + color.getAlpha();

                } else if (value instanceof Vector4f) {
                    Vector4f vec4 = (Vector4f) value;
                    return vec4.getX() + " " + vec4.getY() + " " + vec4.getZ() + " " + vec4.getW();
                }

                break;
            }
            
            case Vector2: {
                Vector2f vec2 = (Vector2f) value;
                return vec2.getX() + " " + vec2.getY();
            }
            
            case Vector3: {
                Vector3f vec3 = (Vector3f) value;
                return vec3.getX() + " " + vec3.getY() + " " + vec3.getZ();
            }
            
            case Texture2D: {
                Texture2D texture2D = (Texture2D) value;
                TextureKey textureKey = (TextureKey) texture2D.getKey();
                if (textureKey == null) {
                    return "";
                }

                StringBuilder sb = new StringBuilder();
                if (textureKey.isFlipY()) {
                    sb.append("Flip ");
                }
                sb.append("Wrap").append(texture2D.getWrap(Texture.WrapAxis.T)).append("_T").append(' ');
                sb.append("Wrap").append(texture2D.getWrap(Texture.WrapAxis.S)).append("_S").append(' ');
                sb.append("Mag").append(texture2D.getMagFilter()).append(' ');
                sb.append("Min").append(texture2D.getMinFilter()).append(' ');
                sb.append(textureKey.getName());

                return sb.toString();
            }
            
            case TextureCubeMap: {
                TextureCubeMap textureCubeMap = (TextureCubeMap) value;
                TextureKey textureKey = (TextureKey) textureCubeMap.getKey();
                if (textureKey == null) {
                    return "";
                }

                StringBuilder sb = new StringBuilder();
                if (textureKey.isFlipY()) {
                    sb.append("Flip ");
                }
                sb.append("Wrap").append(textureCubeMap.getWrap(Texture.WrapAxis.T)).append("_T").append(' ');
                sb.append("Wrap").append(textureCubeMap.getWrap(Texture.WrapAxis.S)).append("_S").append(' ');
                sb.append("Wrap").append(textureCubeMap.getWrap(Texture.WrapAxis.R)).append("_R").append(' ');
                sb.append("Mag").append(textureCubeMap.getMagFilter()).append(' ');
                sb.append("Min").append(textureCubeMap.getMinFilter()).append(' ');
                sb.append(textureKey.getName());

                return sb.toString();
            }
            
            default:
                break;
        }

        throw new RuntimeException("Can't support this type: " + varType);
    }
}
