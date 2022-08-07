package org.holo.forceClosable.compiletime.plugin;

import com.google.errorprone.BugPattern;
import com.google.errorprone.ErrorProneFlags;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.tree.JCTree;
import org.holo.forceClosable.runtime.ForcingClosable;

import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@BugPattern(
        name = "ForcingClosable",
        summary = "Any instance of ForcingClosable or of a class passed by the \"-XepOpt:ForcingTry\" parameter must be inside of a Try-with-Resources clause.",
        severity = BugPattern.SeverityLevel.ERROR,
        linkType = BugPattern.LinkType.CUSTOM,
        link = "https://github.com/Holo314/forceClosable"
)
public class ForcingPlugin
        extends BugChecker
        implements BugChecker.NewClassTreeMatcher {
    private static final BiPredicate<List<? extends Class<?>>, Type.ClassType> IS_FORCING_CLOSABLE =
            (forcingOn, type) -> {
                var symbol = (Symbol.ClassSymbol)type.tsym;
                return ForcingUtils.isInstanceOf(symbol, ForcingClosable.class)
                        || forcingOn.stream().anyMatch(clazz -> ForcingUtils.isInstanceOf(symbol, clazz));
            };
    private final List<? extends Class<?>> forcingTryOn;

    public ForcingPlugin(ErrorProneFlags flags) {
        super();
        this.forcingTryOn = flags.getList("ForcingTry")
                                 .stream()
                                 .flatMap(Collection::stream)
                                 .flatMap(canonName -> {
                                     try {
                                         return Stream.of(Class.forName(canonName));
                                     } catch (ClassNotFoundException e) {
                                         return Stream.empty();
                                     }
                                 })
                                 .collect(Collectors.toList());
        System.out.println();
    }

    @Override
    public Description matchNewClass(NewClassTree newClassTree, VisitorState visitorState) {
        var parentPath = visitorState.getPath().getParentPath();
        // most cases isDecl and isInTry will be true and no need to continue, so splitting the if is more efficient
        var isDecl = parentPath.getLeaf() instanceof VariableTree;
        var isInTry = parentPath.getParentPath().getLeaf() instanceof TryTree;
        if (isDecl && isInTry) {
            return Description.NO_MATCH;
        }
        var type = (Type.ClassType)((JCTree.JCNewClass)newClassTree).type;

        if (IS_FORCING_CLOSABLE.test(forcingTryOn, type)) {
            return describeMatch(newClassTree);
        }

        return Description.NO_MATCH;
    }
}
