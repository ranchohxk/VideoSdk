//
// Created by gutou on 2017/4/18.
//

#include "hyq_sdk_head.h"


#define STR(s) #s
static const char *vs = STR(
        attribute vec4 position;
        attribute vec2 texcoord;
        varying vec2 tx;
        void main() {
            tx = texcoord;
            gl_Position = position;
        }
);
static const char *head = "#extension GL_OES_EGL_image_external : require\n";
static const char *fs_video = STR(
        varying highp vec2 tx;
        uniform samplerExternalOES tex_v;
        uniform highp mat4 st_matrix;
        uniform highp float S;
        uniform highp float L;
        uniform highp float H;
        uniform highp float R;
        uniform highp float G;
        uniform highp float B;
        highp vec3 rgb2hsv(highp vec3 c){
            highp vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
            highp vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
            highp vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));
            highp float d = q.x - min(q.w, q.y);
            highp float e = 1.0e-10;
            return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
        }
        highp vec3 hsv2rgb(highp vec3 c){
            highp vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
            highp vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
            return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
        }
        void main() {
            highp vec2 tx_transformed = (st_matrix * vec4(tx, 0, 1.0)).xy;
            highp vec4 video = texture2D(tex_v, vec2(tx_transformed.x, 1.0 - tx_transformed.y));
            highp vec3 hsl = rgb2hsv(video.xyz);
            if(H != 0.0)hsl.x = H;
            if(hsl.x<0.0)hsl.x = hsl.x+1.0;
            else if(hsl.x>1.0)hsl.x = hsl.x-1.0;
            if(S != 1.0)hsl.y = hsl.y*S;
            if(hsl.y > 1.0)hsl.y = 1.0;
            if(hsl.y < 0.0)hsl.y = 0.0;
            highp vec3 rgb = hsv2rgb(hsl);
            if (L < 0.0) rgb = rgb + rgb * vec3(L);
            else if(L > 0.0)rgb = rgb + (1.0 - rgb) * vec3(L);
            if(R < 0.0)rgb.r = (1.0 + R)*rgb.r;
            else if(R > 0.0)rgb.r = (1.0-rgb.r)*R + rgb.r;
            if(G < 0.0)rgb.g = (1.0 + G)*rgb.g;
            else if(G > 0.0)rgb.g = (1.0-rgb.g)*G + rgb.g;
            if(B < 0.0)rgb.b = (1.0 + B)*rgb.b;
            else if(B > 0.0)rgb.b = (1.0-rgb.b)*B + rgb.b;
            gl_FragColor = vec4(rgb,video.w);
        }
);


static GLuint loadShader(GLenum shaderType, const char *shaderSrc) {
    GLuint shader;
    GLint compiled;
    shader = glCreateShader(shaderType);
    if (shader == 0) return 0;
    glShaderSource(shader, 1, &shaderSrc, NULL);
    glCompileShader(shader);
    glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
    if (!compiled) {
        GLint infoLen;
        glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
        if (infoLen > 0) {
            char *infoLog = (char *) malloc(sizeof(char) * infoLen);
            glGetShaderInfoLog(shader, infoLen, NULL, infoLog);
            LOGD("compile shader error ==>\n%s\n\n%s\n", shaderSrc, infoLog);

            free(infoLog);
        }
        glDeleteShader(shader);
        return 0;
    }
    return shader;
}


static GLuint loadProgram(const char *vsSrc, const char *fsSrc) {
    GLuint vertexShader = loadShader(GL_VERTEX_SHADER, vsSrc);
    GLuint fragmentShader = loadShader(GL_FRAGMENT_SHADER, fsSrc);
    if (vertexShader == 0 || fragmentShader == 0) return 0;
    GLint linked;
    GLuint pro = glCreateProgram();
    if (pro == 0) {
        LOGD("create program error!");
    }
    glAttachShader(pro, vertexShader);
    glAttachShader(pro, fragmentShader);
    glLinkProgram(pro);
    glGetProgramiv(pro, GL_LINK_STATUS, &linked);
    if (!linked) {
        GLint infoLen;
        glGetProgramiv(pro, GL_INFO_LOG_LENGTH, &infoLen);
        if (infoLen > 0) {
            char *infoLog = (char *) malloc(sizeof(char) * infoLen);
            glGetProgramInfoLog(pro, infoLen, NULL, infoLog);
            LOGD("link program error ==>\n%s\n", infoLog);
        }
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        glDeleteProgram(pro);
        return 0;
    }
    return pro;
}


static void init_video(hyq_sdk_glsl_program *program) {
    size_t head_size = strlen(head);
    size_t body_size = strlen(fs_video);
    char fs[head_size + body_size];
    strcpy(fs, head);
    strcat(fs, fs_video);
    GLuint pro = loadProgram(vs, fs);
    program->program = pro;
    program->positon_location = glGetAttribLocation(pro, "position");
    program->texcoord_location = glGetAttribLocation(pro, "texcoord");
    program->tex_v = glGetUniformLocation(pro, "tex_v");
    program->st_matrix = glGetUniformLocation(pro, "st_matrix");
    program->glsl_S = glGetUniformLocation(pro, "S");
    program->glsl_L = glGetUniformLocation(pro, "L");
    program->glsl_H = glGetUniformLocation(pro, "H");
    program->glsl_R = glGetUniformLocation(pro, "R");
    program->glsl_G = glGetUniformLocation(pro, "G");
    program->glsl_B = glGetUniformLocation(pro, "B");
}


void hyq_sdk_glsl_video_program_init(hyq_sdk_model *sdk_model) {
    init_video(sdk_model->video_program);
}

void hyq_sdk_glsl_video_program_delete(hyq_sdk_model *sdk_model) {
    glDeleteProgram(sdk_model->video_program->program);
}