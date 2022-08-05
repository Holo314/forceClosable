package org.holo.forceClosable.compiletime.plugin;

import com.sun.tools.javac.code.Symbol;

import java.util.Arrays;
import java.util.HashSet;

public class ForcingUtils {
    public static void allSuper(
            Symbol.ClassSymbol symbol, HashSet<Symbol.ClassSymbol> ifr, HashSet<Symbol.ClassSymbol> spr
    ) {
        var i = new HashSet<>(symbol.getInterfaces().map(face -> face.tsym)
                                    .map(Symbol.ClassSymbol.class::cast));
        ifr.addAll(i);
        i.forEach(it -> allSuper(it, ifr, spr));

        var next = (Symbol.ClassSymbol)symbol.getSuperclass().tsym;
        if (next == null) {
            return;
        }
        spr.add(next);
        allSuper(next, ifr, spr);
    }

    public static boolean isInstanceOf(Symbol.ClassSymbol symbol, Class<?>... clazzez) {
        return Arrays.stream(clazzez).allMatch(clazz -> {
            var itr = new HashSet<Symbol.ClassSymbol>();
            var spr = new HashSet<Symbol.ClassSymbol>();
            allSuper(symbol, itr, spr);

            return symbol.fullname.toString().equals(clazz.getCanonicalName())
                    || itr.stream().anyMatch(face -> face.fullname.toString()
                                                              .equals(clazz.getCanonicalName()))
                    || spr.stream().anyMatch(zuper -> zuper.fullname.toString()
                                                                  .equals(clazz.getCanonicalName()));
        });
    }
}
