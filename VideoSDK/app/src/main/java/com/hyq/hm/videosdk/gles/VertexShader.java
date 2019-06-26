package com.hyq.hm.videosdk.gles;
/**
 * @author hxk <br/>
 * 功能：
 * 创建日期   2019/6/17
 * 修改者：
 * 修改日期：
 * 修改内容:
 */
public class VertexShader {
    public static final String VERTEX_SHADER = "attribute vec4 vPosition;" +
            "attribute vec2 a_texCoord;" +
            "varying vec2 v_texCoord;" +
            "void main() {" +
            "  gl_Position = vPosition;" +
            "  v_texCoord = a_texCoord;" +
            "}";
}
