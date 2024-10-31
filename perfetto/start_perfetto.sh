#!/bin/zsh

adb root
adb shell setprop persist.traced.enable 1

./record_android_trace -o trace_file.perfetto-trace -b 500mb \
-a io.github.aleksrychkov.example.android.kts sched freq idle am wm gfx view \
binder_driver hal dalvik camera input res