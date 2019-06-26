//
// Created by gutou on 2017/4/18.
//

#include "hyq_sdk_head.h"

void hyq_sdk_init_texture(GLuint *textures) {
    glGenTextures(2, textures);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, textures[0]);
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glBindTexture(GL_TEXTURE_2D, textures[1]);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    glBindTexture(GL_TEXTURE_2D, 0);
}

void hyq_sdk_delete_texture(GLuint *textures) {
    glDeleteTextures(2, textures);
}
void hyq_sdk_init_frame_buffer(GLuint *frame_buffer){
    glGenFramebuffers(1,frame_buffer);
}
void hyq_sdk_delete_frame_buffer(GLuint *frame_buffer) {
    glDeleteFramebuffers(1, frame_buffer);
}
void hyq_sdk_texture_update(GLuint texture, int width, int height, void *pixels) {
    glBindTexture(GL_TEXTURE_2D, texture);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
}

static void hyq_sdk_bind_video_texture(hyq_sdk_glsl_program *video_program, GLuint video_texture) {
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES, video_texture);
    glUniform1i(video_program->tex_v, 0);
}
static void hyq_sdk_bind_video_SHL(hyq_sdk_glsl_program *video_program, float S,float H,float L){
    glUniform1f(video_program->glsl_S, S);
    glUniform1f(video_program->glsl_H, H);
    glUniform1f(video_program->glsl_L, L);
}
static void hyq_sdk_bind_video_RGB(hyq_sdk_glsl_program *video_program, float R,float G,float B){
    glUniform1f(video_program->glsl_R, R);
    glUniform1f(video_program->glsl_G, G);
    glUniform1f(video_program->glsl_B, B);
}


int hyq_sdk_draw_texture(hyq_sdk_model *sdk_model,int video_width, int video_height) {
    if(sdk_model->video_width != video_width || sdk_model->video_height != video_height ){
        hyq_sdk_texture_update(sdk_model->textures[1],video_width,video_height,NULL);
        glBindTexture(GL_TEXTURE_2D, 0);
        sdk_model->video_width = video_width;
        sdk_model->video_height = video_height;
        glBindFramebuffer(GL_FRAMEBUFFER,sdk_model->frame_buffers[0]);
        glFramebufferTexture2D(GL_FRAMEBUFFER,GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D,sdk_model->textures[1],0);
    }
    glBindFramebuffer(GL_FRAMEBUFFER,sdk_model->frame_buffers[0]);
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    glViewport(0, 0, video_width, video_height);
    glUseProgram( sdk_model->video_program->program);
    hyq_sdk_bind_video_texture(sdk_model->video_program,sdk_model->textures[0]);
    hyq_sdk_video_bind_vertex_buffers(sdk_model->video_program, sdk_model->video_bos,sdk_model->st_matrix);
    hyq_sdk_bind_video_SHL(sdk_model->video_program,sdk_model->S,sdk_model->H,sdk_model->L);
    hyq_sdk_bind_video_RGB(sdk_model->video_program,sdk_model->R,sdk_model->G,sdk_model->B);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    glBindFramebuffer(GL_FRAMEBUFFER,0);
    return sdk_model->textures[1];
}

