

cmake_minimum_required(VERSION 3.4.1)

#aux_source_directory(src/main/cpp/filter filter)
add_library(
             video_filter
             SHARED
             src/main/cpp/video_filter.cpp
             src/main/cpp/filter/hyq_sdk_glsl_program.c
             src/main/cpp/filter/hyq_sdk_model.c
             src/main/cpp/filter/hyq_sdk_texture.c
             src/main/cpp/filter/hyq_sdk_mesh.c
             )


find_library(
              log-lib
              log )


target_link_libraries(
                       video_filter
                       ${log-lib}
                        GLESv2
                        jnigraphics
                        )