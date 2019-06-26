//
// Created by gutou on 2017/4/18.
//

#include "hyq_sdk_head.h"

void hyq_sdk_gles_init(hyq_sdk_model *sdk_model) {
    hyq_sdk_glsl_video_program_init(sdk_model);
    hyq_sdk_init_texture(sdk_model->textures);
    hyq_sdk_init_frame_buffer(sdk_model->frame_buffers);
    hyq_sdk_init_video_vertex_buffers(sdk_model->video_bos, sdk_model->vertex_data,sdk_model->texture_vertex_data);
//    for (int i = 0; i < sdk_model->max_textures_count; ++i) {
//        hyq_sdk_init_ad_vertex_buffers(sdk_model->sdk_data[i]->ad_bos);
//    }
//    hyq_sdk_init_frame_texture(sdk_model);

}

void hyq_sdk_gles_delete(hyq_sdk_model *sdk_model) {
    hyq_sdk_delete_texture(sdk_model->textures);
    hyq_sdk_delete_frame_buffer(sdk_model->frame_buffers);
    hyq_sdk_delete_video_vertex_buffers(sdk_model->video_bos);
//    for (int i = 0; i < sdk_model->max_textures_count; ++i) {
//        hyq_sdk_delete_ad_vertex_buffers(sdk_model->sdk_data[i]->ad_bos);
//    }
//    hyq_sdk_delete_frame_texture(sdk_model);
    hyq_sdk_glsl_video_program_delete(sdk_model);
    sdk_model->video_width = 0;
    sdk_model->video_height = 0;
}

hyq_sdk_model *hyq_sdk_model_create() {
    hyq_sdk_model *sdk_model = (hyq_sdk_model *) malloc(sizeof(hyq_sdk_model));

    sdk_model->S = 1.0f;
    sdk_model->L = 0.0f;
    sdk_model->H = 0.0f;
    sdk_model->R = 0.0f;
    sdk_model->G = 0.0f;
    sdk_model->B = 0.0f;

    sdk_model->video_program = malloc(sizeof(hyq_sdk_glsl_program));
    sdk_model->left = 0;
    sdk_model->top = 0;
    sdk_model->right = 0;
    sdk_model->bottom = 0;
    sdk_model->video_width = 0;
    sdk_model->video_height = 0;

    sdk_model->st_matrix = malloc(sizeof(float) * 16);
    sdk_model->video_bos = malloc(sizeof(GLuint)*2);
    sdk_model->textures = malloc(sizeof(GLuint)*2);
    sdk_model->frame_buffers = malloc(sizeof(GLuint));


    sdk_model->vertex_data = malloc(sizeof(float) * 12);
    sdk_model->texture_vertex_data = malloc(sizeof(float) * 8);



//    sdk_model->draw_texture = hyq_sdk_draw_texture;

    sdk_model->vertex_data[0] = 1.0f;
    sdk_model->vertex_data[1] = -1.0f;
    sdk_model->vertex_data[2] = 0.0f;
    sdk_model->vertex_data[3] = -1.0f;
    sdk_model->vertex_data[4] = -1.0f;
    sdk_model->vertex_data[5] = 0.0f;
    sdk_model->vertex_data[6] = 1.0f;
    sdk_model->vertex_data[7] = 1.0f;
    sdk_model->vertex_data[8] = 0.0f;
    sdk_model->vertex_data[9] = -1.0f;
    sdk_model->vertex_data[10] = 1.0f;
    sdk_model->vertex_data[11] = 0.0f;


    sdk_model->texture_vertex_data[0] = 1.0f;
    sdk_model->texture_vertex_data[1] = 0.0f;
    sdk_model->texture_vertex_data[2] = 0.0f;
    sdk_model->texture_vertex_data[3] = 0.0f;
    sdk_model->texture_vertex_data[4] = 1.0f;
    sdk_model->texture_vertex_data[5] = 1.0f;
    sdk_model->texture_vertex_data[6] = 0.0f;
    sdk_model->texture_vertex_data[7] = 1.0f;


    return sdk_model;
}

void hyq_sdk_model_free(hyq_sdk_model *sdk_model) {
    if (sdk_model != NULL) {
        free(sdk_model->st_matrix);
        free(sdk_model->vertex_data);
        free(sdk_model->texture_vertex_data);
        free(sdk_model->video_bos);
        free(sdk_model->textures);
        free(sdk_model->frame_buffers);
        free(sdk_model->video_program);
        free(sdk_model);
    }
}