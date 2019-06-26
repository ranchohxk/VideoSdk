#include <jni.h>
#include <string>


extern "C"{
#include "filter/hyq_sdk_head.h"
}
extern "C"
JNIEXPORT jstring

JNICALL
Java_com_hyq_hm_videosdk_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT hyq_sdk_model * JNICALL
Java_com_hyq_videoFilter_VideoFilter_createModel(JNIEnv *env, jobject instance) {

    // TODO
    return hyq_sdk_model_create();
}extern "C"
JNIEXPORT void JNICALL
Java_com_hyq_videoFilter_VideoFilter_release(JNIEnv *env, jobject instance, jlong model) {

    // TODO
    hyq_sdk_model *sdk_model = (hyq_sdk_model *) model;
    hyq_sdk_model_free(sdk_model);
}extern "C"
JNIEXPORT void JNICALL
Java_com_hyq_videoFilter_VideoFilter_initGLES(JNIEnv *env, jobject instance, jlong model) {

    // TODO
    hyq_sdk_model *sdk_model = (hyq_sdk_model *) model;
    hyq_sdk_gles_init(sdk_model);
}extern "C"
JNIEXPORT void JNICALL
Java_com_hyq_videoFilter_VideoFilter_deleteGLES(JNIEnv *env, jobject instance, jlong model) {

    // TODO
    hyq_sdk_model *sdk_model = (hyq_sdk_model *) model;
    hyq_sdk_gles_delete(sdk_model);
}extern "C"
JNIEXPORT jint JNICALL
Java_com_hyq_videoFilter_VideoFilter_drawGLES(JNIEnv *env, jobject instance,jfloatArray stMatrix_, jint videoWidth,
                                              jint videoHeight, jlong model) {
    jfloat *stMatrix = env->GetFloatArrayElements(stMatrix_, NULL);
    // TODO
    hyq_sdk_model *sdk_model = (hyq_sdk_model *) model;
    env->GetFloatArrayRegion(stMatrix_, 0, 16, sdk_model->st_matrix);
    env->ReleaseFloatArrayElements(stMatrix_, stMatrix, 0);
    env->DeleteLocalRef(stMatrix_);
    return hyq_sdk_draw_texture(sdk_model,videoWidth,videoHeight);
}extern "C"
JNIEXPORT jint JNICALL
Java_com_hyq_videoFilter_VideoFilter_getTextureId(JNIEnv *env, jobject instance, jlong model) {

    // TODO
    hyq_sdk_model *sdk_model = (hyq_sdk_model *) model;
    return sdk_model->textures[0];
}extern "C"
JNIEXPORT void JNICALL
Java_com_hyq_videoFilter_VideoFilter_setS(JNIEnv *env, jobject instance, jfloat S,
                                              jlong model) {

    // TODO
    hyq_sdk_model *sdk_model = (hyq_sdk_model *) model;
    sdk_model->S = S;
}extern "C"
JNIEXPORT void JNICALL
Java_com_hyq_videoFilter_VideoFilter_setH(JNIEnv *env, jobject instance, jfloat H,
                                              jlong model) {

    // TODO
    hyq_sdk_model *sdk_model = (hyq_sdk_model *) model;
    sdk_model->H = H;
}extern "C"
JNIEXPORT void JNICALL
Java_com_hyq_videoFilter_VideoFilter_setL(JNIEnv *env, jobject instance, jfloat L,
                                              jlong model) {

    // TODO
    hyq_sdk_model *sdk_model = (hyq_sdk_model *) model;
    sdk_model->L = L;
}extern "C"
JNIEXPORT void JNICALL
Java_com_hyq_videoFilter_VideoFilter_setR(JNIEnv *env, jobject instance, jfloat R,
                                              jlong model) {

    // TODO
    hyq_sdk_model *sdk_model = (hyq_sdk_model *) model;
    sdk_model->R = R;
}extern "C"
JNIEXPORT void JNICALL
Java_com_hyq_videoFilter_VideoFilter_setG(JNIEnv *env, jobject instance, jfloat G,
                                              jlong model) {

    // TODO
    hyq_sdk_model *sdk_model = (hyq_sdk_model *) model;
    sdk_model->G = G;
}extern "C"
JNIEXPORT void JNICALL
Java_com_hyq_videoFilter_VideoFilter_setB(JNIEnv *env, jobject instance, jfloat B,
                                              jlong model) {

    // TODO
    hyq_sdk_model *sdk_model = (hyq_sdk_model *) model;
    sdk_model->B = B;
}