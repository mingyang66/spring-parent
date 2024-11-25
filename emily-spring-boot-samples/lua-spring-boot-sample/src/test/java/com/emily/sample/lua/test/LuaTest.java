package com.emily.sample.lua.test;

import org.junit.Test;
import party.iroiro.luajava.Lua;
import party.iroiro.luajava.lua54.Lua54;
import party.iroiro.luajava.value.LuaValue;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

/**
 * @author :  Emily
 * @since :  2024/11/1 下午3:54
 */
public class LuaTest {
    @Test
    public void test() {
        try (Lua L = new Lua54()) {
            LuaValue[] returnValues = L.eval("return { a = 1 }, 1024, 'my string value'");
            assertEquals(3, returnValues.length);
            assertEquals(1, returnValues[0].get("a").toInteger());
            assertEquals(1024, returnValues[1].toInteger());
            assertEquals("my string value", returnValues[2].toString());
        }
    }

    @Test
    public void getGlobalVar() {
        try (Lua lua = new Lua54()) {
            assertEquals("Lua 5.4", lua.get("_VERSION").toString());
            LuaValue value = lua.from(1);
            lua.set("a", value); // LuaValue
            lua.set("b", 2); // Java Integer
            lua.set("c", new BigDecimal(3)); // Any Java object
            assertEquals(
                    6,
                    lua.eval("return a + b + c:longValue()")[0].toInteger()
            );
        }
    }

    @Test
    public void getLuaValues() {
        try (Lua L = new Lua54()) {
            LuaValue value = L.from(1);
            L.set("a", value); // LuaValue
            L.set("b", 2); // Java Integer
            L.set("c", new BigDecimal(3)); // Any Java object
            assertEquals(
                    6,
                    L.eval("return a + b + c:longValue()")[0].toInteger()
            );
        }
    }

    @Test
    public void getEval() {
        try (Lua L = new Lua54()) {
            L.openLibraries();
            LuaValue[] values1 = L.eval("string.sub('abcdefg', 0, 3)");
            assertEquals(0, values1.length);
            LuaValue[] values2 = L.eval("return string.sub('abcdefg', 0, 3)");
            assertEquals("abc", values2[0].toString());
        }
    }

    @Test
    public void getTable() {
        try (Lua L = new Lua54()) {
            L.run("t = { text = 'abc', children = { 'a', 'b', 'c' } }");
            LuaValue table = L.eval("return t")[0];
            // Get-calls return LuaValues.
            assertEquals("abc", table.get("text").toString());
            LuaValue children = table.get("children");
            // Indices are 1-based.
            assertEquals("a", children.get(1).toString());
            assertEquals(3, children.size());
            // Set-calls accept LuaValues or any Java object.
            children.set(4, "d");
            // Changes are done in the Lua side.
            L.run("assert(t.children[4] == 'd')");
        }
    }

    @Test
    public void getLuaValue() {
        try (Lua L = new Lua54()) {
            L.openLibrary("string");
            LuaValue gsub = L.eval("return string.gsub")[0];
            LuaValue luaJava = gsub.call("Lua", "a", "aJava")[0];
            assertEquals("LuaJava", luaJava.toString());
        }
    }

    @Test
    public void getRunnable() throws InterruptedException {
        try (Lua L = new Lua54()) {
            LuaValue runnable = L.eval("return { run = function() print('running...') end }")[0];
            Runnable r = runnable.toProxy(Runnable.class);
            Thread t = new Thread(r);
            t.start();
            t.join();
        }
    }
}
