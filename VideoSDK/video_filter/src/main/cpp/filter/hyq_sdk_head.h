//
// Created by 海米 on 2018/12/5.
//

#ifndef VIDEOSDK_HYQ_H
#define VIDEOSDK_HYQ_H
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <malloc.h>
#include <string.h>
#include <android/log.h>
#include <sys/types.h>
#define LOG_TAG "YHQ"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

typedef struct struct_hyq_sdk_glsl_program {
    GLuint program;
    GLint positon_location;

    GLint st_matrix;
    GLint texcoord_location;
    GLint tex_v;

    GLint glsl_S;
    GLint glsl_L;
    GLint glsl_H;
    GLint glsl_R;
    GLint glsl_G;
    GLint glsl_B;

} hyq_sdk_glsl_program;


typedef struct struct_hyq_sdk_model {

    float S,L,H,R,G,B;

    float *vertex_data;
    float *texture_vertex_data;

    hyq_sdk_glsl_program *video_program;

    float *st_matrix;


    GLuint *video_bos;
    GLuint *textures;
    GLuint *frame_buffers;


    int left,top,right,bottom,video_width,video_height;

} hyq_sdk_model;
hyq_sdk_model * hyq_sdk_model_create();
void hyq_sdk_model_free(hyq_sdk_model *sdk_model);
void hyq_sdk_gles_init(hyq_sdk_model *sdk_model);
void hyq_sdk_gles_delete(hyq_sdk_model *sdk_model);

void hyq_sdk_glsl_video_program_init(hyq_sdk_model *sdk_model);
void hyq_sdk_glsl_video_program_delete(hyq_sdk_model *sdk_model);

void hyq_sdk_init_texture(GLuint *textures);
void hyq_sdk_delete_texture(GLuint *textures);
void hyq_sdk_init_frame_buffer(GLuint *frame_buffer);
void hyq_sdk_delete_frame_buffer(GLuint *frame_buffer);
int hyq_sdk_draw_texture(hyq_sdk_model *sdk_model,int video_width, int video_height) ;

void hyq_sdk_init_video_vertex_buffers(GLuint *video_bos,float *vertex_data,float *texture_vertex_data);
void hyq_sdk_delete_video_vertex_buffers(GLuint *video_bos);
void hyq_sdk_video_bind_vertex_buffers(hyq_sdk_glsl_program *video_program,GLuint *video_bos,float *st_matrix);


#endif //VIDEOSDK_HYQ_H
