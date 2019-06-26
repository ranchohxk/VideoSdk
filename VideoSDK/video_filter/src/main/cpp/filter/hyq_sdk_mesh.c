//
// Created by gutou on 2017/4/18.
//

#include "hyq_sdk_head.h"

void hyq_sdk_init_video_vertex_buffers(GLuint *video_bos,float *vertex_data,float *texture_vertex_data){
    glGenBuffers(2,video_bos);
    glBindBuffer(GL_ARRAY_BUFFER,video_bos[0]);
    glBufferData(GL_ARRAY_BUFFER, 12* sizeof(float),vertex_data,GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER,video_bos[1]);
    glBufferData(GL_ARRAY_BUFFER, 8*sizeof(float),texture_vertex_data,GL_STATIC_DRAW);
    glBindBuffer(GL_ARRAY_BUFFER,0);
}


void hyq_sdk_video_bind_vertex_buffers(hyq_sdk_glsl_program *video_program,GLuint *video_bos,float *st_matrix){
    glBindBuffer(GL_ARRAY_BUFFER,video_bos[0]);
    glEnableVertexAttribArray((GLuint) video_program->positon_location);
    glVertexAttribPointer((GLuint) video_program->positon_location, 3, GL_FLOAT, GL_FALSE,
                          0, 0);

    glBindBuffer(GL_ARRAY_BUFFER,video_bos[1]);
    glEnableVertexAttribArray((GLuint) video_program->texcoord_location);
    glVertexAttribPointer((GLuint) video_program->texcoord_location,2,GL_FLOAT,GL_FALSE,0,0);
    glBindBuffer(GL_ARRAY_BUFFER,0);

    glUniformMatrix4fv(video_program->st_matrix, 1, GL_FALSE, st_matrix);

}
void hyq_sdk_delete_video_vertex_buffers(GLuint *video_bos){
    glDeleteBuffers(2,video_bos);
}
