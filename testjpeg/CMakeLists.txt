# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)


# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK. Gradle


set(jpeg_dir ${CMAKE_CURRENT_SOURCE_DIR}/libs/jpeg/${ANDROID_ABI})

add_library(libjpeg SHARED IMPORTED)
set_target_properties(libjpeg PROPERTIES IMPORTED_LOCATION ${jpeg_dir}/libjpeg.so)


add_library( # Sets the name of the library.  设置库的名称。
             native_libs

             # Sets the library as a shared library. 将library设置为一个共享库
             SHARED

             # Provides a relative path to your source file(s).为源文件提供一个相对路径

             src/main/jni/native_lib.cpp
             )


find_library( # Sets the name of the path variable. 设置路径变量的名称
              log-lib

              # Specifies the name of the NDK library that 指定NDK库的名称
              # you want CMake to locate.
              log
              )


target_link_libraries( # Specifies the target library.指定连接的目标库
                       native_libs
                        android
                        jnigraphics
                        libjpeg
                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib})

