package test.org.holo.forceClosable;

import com.google.errorprone.CompilationTestHelper;
import org.holo.forceClosable.runtime.ForcingClosable;
import org.holo.forceClosable.compiletime.plugin.ForcingPlugin;
import org.junit.jupiter.api.Test;
import test.org.holo.forceClosable.testdata.Test1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ForcingTest {
    @Test
    public void forceClosableMustBeDeclaredWithAVariable()
            throws IOException {
        var source0 = "test/org/holo/forceClosable/testdata/Test.java";
        var source1 = "test/org/holo/forceClosable/testdata/Test1.java";

        var compilationHelper = CompilationTestHelper.newInstance(ForcingPlugin.class, getClass());
        // TODO: add actual tests instead of this show-case example
        compilationHelper.addSourceLines(
                                 source0,
                                 Files.readAllLines(Path.of("src/test/java/" + source0))
                                      .toArray(String[]::new)
                         )
                         .addSourceLines(
                                 source1,
                                 Files.readAllLines(Path.of("src/test/java/" + source1))
                                      .toArray(String[]::new)
                         )
                         .setArgs("-XepOpt:ForcingTry=" + Test1.class.getCanonicalName())
                         .withClasspath(ForcingClosable.class)
                         .doTest();
    }
}
