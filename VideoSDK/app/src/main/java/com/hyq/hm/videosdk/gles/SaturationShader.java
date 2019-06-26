package com.hyq.hm.videosdk.gles;

/**
 * @author hxk <br/>
 * 功能：
 * 创建日期   2019/6/17
 * 修改者：
 * 修改日期：
 * 修改内容:
 */
public class SaturationShader {
    public static final String frame_shader_saturation_two_parameters = "varying highp vec2 v_texCoord;\n" +
            "uniform highp sampler2D s_texture;\n" +
            "uniform highp float saturation;\n" +
            "uniform highp float mVar1;\n" +
            "uniform highp float mVar2;\n" +
            "uniform highp float mVar3;\n" +
            "uniform highp float mVar4;\n" +
            "uniform highp float mVar5;\n" +
            "uniform highp float mVar6;\n" +
            "uniform highp float mVar7;\n" +
            "uniform highp float mVar8;\n" +
            "uniform highp float mVar9;\n" +
            "uniform highp float mVar10;\n" +
            "uniform highp float mVarExtra;\n" +

            "const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);\n" +
            "void main() {\n" +
            "   lowp vec4 textureColor = texture2D(s_texture,v_texCoord);\n" +
            "   lowp float luminance = dot(textureColor.rgb, luminanceWeighting);\n" +
            "   lowp vec3 greyScaleColor = vec3(luminance);\n" +
            "   gl_FragColor = vec4(mix(greyScaleColor, textureColor.rgb, 1.0), textureColor.w);\n" +
            "   gl_FragColor.r = mVar1 * texture2D(s_texture,v_texCoord).r;\n" +// slider 调节"   gl_FragColor.r = mVar1 * texture2D(sTexture, vTextureCoord).r;\n"
            "   gl_FragColor.g = mVar2 * texture2D(s_texture,v_texCoord).g;\n" +//slider调节
            "   gl_FragColor.b = mVar3 * texture2D(s_texture,v_texCoord).b;\n" +//人眼调节

            "}\n";
}
