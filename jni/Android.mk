LOCAL_PATH := (call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := sqlite3
LOCAL_SRC_FILES := libsqlite3.c

include $(BUILD_SHARED_LIBRARY)