LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := CallNative
LOCAL_SRC_FILE := com_codextropy_mycryptoapp_MainActivity

include $(BUILD_SHARED_LIBRARY)
