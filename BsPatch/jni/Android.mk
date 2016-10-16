LOCAL_PATH := $(call my-dir)
 
include $(CLEAR_VARS)
 
LOCAL_MODULE    := BsPatch
LOCAL_SRC_FILES := blocksort.c\
					bzip2.c\
					bzlib.c\
					compress.c\
					crctable.c\
					decompress.c\
					huffman.c\
					randtable.c\
					bspatch.c
 
include $(BUILD_SHARED_LIBRARY)