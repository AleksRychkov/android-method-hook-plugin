package dev.aleksrychkov.methodhook.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class MethodHookUtilsTest {

    @ParameterizedTest
    @CsvSource(
        "(Landroid/content/Intent;)Landroid/os/IBinder;@(Intent)->IBinder",
        "()V@()->void",
        "()Ljava/lang/Class;@()->Class",
        "(Ljava/lang/String;I)Ljava/lang/String;@(String,int)->String",
        "([[[D)[Ljava/lang/String;@(double[][][])->String[]",
        "(IDB)Ljava/lang/String;@(int,double,byte)->String",
        delimiterString = "@",
    )
    fun `When convertDescriptor Then result correct`(input: String, expected: String) {
        assertEquals(expected, input.convertDescriptor())
    }
}
