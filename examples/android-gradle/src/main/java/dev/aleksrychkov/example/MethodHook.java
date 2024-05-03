package dev.aleksrychkov.example;

public class MethodHook {

    /**
     * @noinspection unused
     */
    public static void start(String runtimeClazz, String clazz, String method) {
        System.out.println("MethodHook::start::" + runtimeClazz + "::" + clazz + "::" + method);
    }

    /**
     * @noinspection unused
     */
    public static void end(String runtimeClazz, String clazz, String method) {
        System.out.println("MethodHook::end::" + runtimeClazz + "::" + clazz + "::" + method);
    }
}
