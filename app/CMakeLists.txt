# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# 设置生成的so动态库最后输出的路径
#set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})
#指定到so文件生成的具体位置另一种写法
#  ${CMAKE_CURRENT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}

####################### 注意 ######################
# 在指定so文件生成的路劲时，需要注意${ANDROID_ABI} 上一层的文件名最好自己取一个独特的；
# 避免与其它存放so的文件名相同造成build失败。

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/src/main/nativeLibs/${ANDROID_ABI})


# Creates and names a library, sets it as either STATIC  
# or SHARED, and provides the relative paths to its source code. 
# You can define multiple libraries, and CMake builds them for you. 
# Gradle automatically packages shared libraries with your APK. Gradle

add_library( # Sets the name of the library.  设置库的名称。
             native_libs

             # Sets the library as a shared library. 将library设置为一个共享库
             SHARED

             # Provides a relative path to your source file(s).为源文件提供一个相对路径

             src/main/jni/reverse_words.h
             src/main/jni/reverse_words.cpp
             src/main/jni/Decode.cpp
             )


find_library( # Sets the name of the path variable. 设置路径变量的名称
              log-lib

              # Specifies the name of the NDK library that 指定NDK库的名称
              # you want CMake to locate.
              log
              )


target_link_libraries( # Specifies the target library.指定连接的目标库
                       native_libs
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib})

