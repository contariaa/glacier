package me.contaria.glacier.optimization.memory.deduplicate_mappings;

import com.google.common.collect.ImmutableMap;
import me.contaria.glacier.Glacier;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.fabricmc.loader.impl.LazyMappingResolver;
import net.fabricmc.loader.impl.lib.mappingio.tree.MemoryMappingTree;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DeduplicateMappings {

    public static void tryDeduplicateMappings() {
        long start = System.currentTimeMillis();
        try {
            Glacier.LOGGER.info("Deduplicating mappings...");
            if (deduplicateMappings()) {
                Glacier.LOGGER.info("Took {}ms to deduplicate mappings!", System.currentTimeMillis() - start);
            } else {
                Glacier.LOGGER.warn("Could not deduplicate mappings, may not be initialized yet.");
            }
        } catch (Exception e) {
            Glacier.LOGGER.error("Failed to deduplicate mappings!", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean deduplicateMappings() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        Field delegate = LazyMappingResolver.class.getDeclaredField("delegate");

        Class<?> mappingResolverImpl = Class.forName("net.fabricmc.loader.impl.MappingResolverImpl");
        Field mappingResolverImpl_mappings = mappingResolverImpl.getDeclaredField("mappings");

        Field classesBySrcName = MemoryMappingTree.class.getDeclaredField("classesBySrcName");

        Class<?> classEntry = Class.forName("net.fabricmc.loader.impl.lib.mappingio.tree.MemoryMappingTree$ClassEntry");
        Field fields = classEntry.getDeclaredField("fields");
        Field methods = classEntry.getDeclaredField("methods");

        Class<?> entry = Class.forName("net.fabricmc.loader.impl.lib.mappingio.tree.MemoryMappingTree$Entry");
        Field entry_srcName = entry.getDeclaredField("srcName");
        Field entry_dstNames = entry.getDeclaredField("dstNames");
        Field entry_comment = entry.getDeclaredField("comment");

        Class<?> memberEntry = Class.forName("net.fabricmc.loader.impl.lib.mappingio.tree.MemoryMappingTree$MemberEntry");
        Field memberEntry_srcDesc = memberEntry.getDeclaredField("srcDesc");

        Class<?> memberKey = Class.forName("net.fabricmc.loader.impl.lib.mappingio.tree.MemoryMappingTree$MemberKey");
        Field memberKey_name = memberKey.getDeclaredField("name");
        Field memberKey_desc = memberKey.getDeclaredField("desc");

        Class<?> methodEntry = Class.forName("net.fabricmc.loader.impl.lib.mappingio.tree.MemoryMappingTree$MethodEntry");
        Field methodEntry_args = methodEntry.getDeclaredField("args");
        Field methodEntry_vars = methodEntry.getDeclaredField("vars");

        delegate.setAccessible(true);
        mappingResolverImpl_mappings.setAccessible(true);
        classesBySrcName.setAccessible(true);
        fields.setAccessible(true);
        methods.setAccessible(true);
        entry_srcName.setAccessible(true);
        entry_dstNames.setAccessible(true);
        entry_comment.setAccessible(true);
        memberEntry_srcDesc.setAccessible(true);
        memberKey_name.setAccessible(true);
        memberKey_desc.setAccessible(true);
        methodEntry_args.setAccessible(true);
        methodEntry_vars.setAccessible(true);
        try {
            Map<String, String> strings = new HashMap<>(8096);
            MappingResolver resolver = (MappingResolver) delegate.get(FabricLoader.getInstance().getMappingResolver());
            if (resolver == null) {
                return false;
            }
            MemoryMappingTree mappings = (MemoryMappingTree) mappingResolverImpl_mappings.get(resolver);

            deduplicateClasses(strings, fields, methods, memberKey_name, memberKey_desc, entry_srcName, entry_dstNames, entry_comment, memberEntry_srcDesc, methodEntry_args, methodEntry_vars, (Map<String, ?>) classesBySrcName.get(mappings));
        } finally {
            delegate.setAccessible(false);
            mappingResolverImpl_mappings.setAccessible(false);
            classesBySrcName.setAccessible(false);
            fields.setAccessible(false);
            methods.setAccessible(false);
            entry_srcName.setAccessible(false);
            entry_dstNames.setAccessible(false);
            entry_comment.setAccessible(false);
            memberEntry_srcDesc.setAccessible(false);
            memberKey_name.setAccessible(false);
            memberKey_desc.setAccessible(false);
            methodEntry_args.setAccessible(false);
            methodEntry_vars.setAccessible(false);
        }
        return true;
    }

    private static void deduplicateClasses(
            Map<String, String> strings,
            Field fields,
            Field methods,
            Field memberKey_name,
            Field memberKey_desc,
            Field entry_srcName,
            Field entry_dstNames,
            Field entry_comment,
            Field memberEntry_srcDesc,
            Field methodEntry_args,
            Field methodEntry_vars,
            Map<String, ?> classes
    ) throws IllegalAccessException {
        classes.keySet().forEach(string -> strings.put(string, string));
        for (Object classEntry : classes.values()) {
            deduplicateClassEntry(strings, fields, methods, memberKey_name, memberKey_desc, entry_srcName, entry_dstNames, entry_comment, memberEntry_srcDesc, methodEntry_args, methodEntry_vars, classEntry);
        }
    }

    private static void deduplicateClassEntry(
            Map<String, String> strings,
            Field fields,
            Field methods,
            Field memberKey_name,
            Field memberKey_desc,
            Field entry_srcName,
            Field entry_dstNames,
            Field entry_comment,
            Field memberEntry_srcDesc,
            Field methodEntry_args,
            Field methodEntry_vars,
            Object classEntry
    ) throws IllegalAccessException {
        deduplicateEntry(strings, entry_srcName, entry_dstNames, entry_comment, classEntry);

        fields.set(classEntry, deduplicateFields(strings, memberKey_name, memberKey_desc, entry_srcName, entry_dstNames, entry_comment, memberEntry_srcDesc, (Map<?, ?>) fields.get(classEntry)));
        methods.set(classEntry, deduplicateMethods(strings, memberKey_name, memberKey_desc, entry_srcName, entry_dstNames, entry_comment, memberEntry_srcDesc, methodEntry_args, methodEntry_vars, (Map<?, ?>) methods.get(classEntry)));
    }

    private static void deduplicateEntry(
            Map<String, String> strings,
            Field entry_srcName,
            Field entry_dstNames,
            Field entry_comment,
            Object memberEntry
    ) throws IllegalAccessException {
        deduplicate(strings, entry_srcName, memberEntry);
        deduplicateArray(strings, entry_dstNames, memberEntry);
        deduplicate(strings, entry_comment, memberEntry);
    }

    private static Map<?, ?> deduplicateMethods(
            Map<String, String> strings,
            Field memberKey_name,
            Field memberKey_desc,
            Field entry_srcName,
            Field entry_dstNames,
            Field entry_comment,
            Field memberEntry_srcDesc,
            Field methodEntry_args,
            Field methodEntry_vars,
            Map<?, ?> members
    ) throws IllegalAccessException {
        if (members == null) {
            return null;
        }
        if (members.isEmpty()) {
            return Collections.emptyMap();
        }
        for (Map.Entry<?, ?> entry : members.entrySet()) {
            Object methodEntry = entry.getValue();
            deduplicateMemberKey(strings, memberKey_name, memberKey_desc, entry.getKey());
            deduplicateMemberEntry(strings, entry_srcName, entry_dstNames, entry_comment, memberEntry_srcDesc, methodEntry);

            List<?> args = (List<?>) methodEntry_args.get(methodEntry);
            if (args != null) {
                for (Object argEntry : args) {
                    deduplicateEntry(strings, entry_srcName, entry_dstNames, entry_comment, argEntry);
                }
            }
            List<?> vars = (List<?>) methodEntry_vars.get(methodEntry);
            if (vars != null) {
                for (Object varEntry : vars) {
                    deduplicateEntry(strings, entry_srcName, entry_dstNames, entry_comment, varEntry);
                }
            }
        }
        return ImmutableMap.copyOf(members);
    }

    private static Map<?, ?> deduplicateFields(
            Map<String, String> strings,
            Field memberKey_name,
            Field memberKey_desc,
            Field entry_srcName,
            Field entry_dstNames,
            Field entry_comment,
            Field memberEntry_srcDesc,
            Map<?, ?> members
    ) throws IllegalAccessException {
        if (members == null) {
            return null;
        }
        if (members.isEmpty()) {
            return Collections.emptyMap();
        }
        for (Map.Entry<?, ?> entry : members.entrySet()) {
            deduplicateMemberKey(strings, memberKey_name, memberKey_desc, entry.getKey());
            deduplicateMemberEntry(strings, entry_srcName, entry_dstNames, entry_comment, memberEntry_srcDesc, entry.getValue());
        }
        return ImmutableMap.copyOf(members);
    }

    private static void deduplicateMemberKey(
            Map<String, String> strings,
            Field memberKey_name,
            Field memberKey_desc,
            Object memberKey
    ) throws IllegalAccessException {
        deduplicate(strings, memberKey_name, memberKey);
        deduplicate(strings, memberKey_desc, memberKey);
    }

    private static void deduplicateMemberEntry(
            Map<String, String> strings,
            Field entry_srcName,
            Field entry_dstNames,
            Field entry_comment,
            Field memberEntry_srcDesc,
            Object memberEntry
    ) throws IllegalAccessException {
        deduplicateEntry(strings, entry_srcName, entry_dstNames, entry_comment, memberEntry);
        deduplicate(strings, memberEntry_srcDesc, memberEntry);
    }

    private static void deduplicateArray(
            Map<String, String> strings,
            Field field,
            Object object
    ) throws IllegalAccessException {
        String[] array = (String[]) field.get(object);
        for (int i = 0; i < array.length; i++) {
            array[i] = deduplicate(strings, array[i]);
        }
    }

    private static void deduplicate(
            Map<String, String> strings,
            Field field,
            Object object
    ) throws IllegalAccessException {
        String string = (String) field.get(object);
        if (string != null) {
            field.set(object, deduplicate(strings, string));
        }
    }

    private static String deduplicate(
            Map<String, String> strings,
            String string
    ) {
        return string != null ? strings.computeIfAbsent(string, Function.identity()) : null;
    }
}
