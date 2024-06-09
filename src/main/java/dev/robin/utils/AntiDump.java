package dev.robin.utils;

import dev.robin.utils.NotifyUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import sun.misc.Unsafe;

public class AntiDump {
    private static Unsafe unsafe;
    private static  String[] naughtyFlags;
    private static Method findNative;
    private static ClassLoader classLoader;
    private static boolean ENABLE;

    public static native void check();

    private static boolean isClassLoaded(String clazz) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
        m.setAccessible(true);
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        ClassLoader scl = ClassLoader.getSystemClassLoader();
        return m.invoke(cl, clazz) != null || m.invoke(scl, clazz) != null;
    }

    private static byte[] createDummyClass(String name) {
        ClassNode classNode = new ClassNode();
        classNode.name = name.replace('.', '/');
        classNode.access = 1;
        classNode.version = 52;
        classNode.superName = "java/lang/Object";
        ArrayList<MethodNode> methods = new ArrayList<MethodNode>();
        MethodNode methodNode = new MethodNode(9, "<clinit>", "()V", null, null);
        InsnList insn = new InsnList();
        insn.add((AbstractInsnNode)new FieldInsnNode(178, "java/lang/System", "out", "Ljava/io/PrintStream;"));
        insn.add((AbstractInsnNode)new LdcInsnNode((Object)"Nice try"));
        insn.add((AbstractInsnNode)new MethodInsnNode(182, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false));
        insn.add((AbstractInsnNode)new TypeInsnNode(187, "java/lang/Throwable"));
        insn.add((AbstractInsnNode)new InsnNode(89));
        insn.add((AbstractInsnNode)new LdcInsnNode((Object)"owned"));
        insn.add((AbstractInsnNode)new MethodInsnNode(183, "java/lang/Throwable", "<init>", "(Ljava/lang/String;)V", false));
        insn.add((AbstractInsnNode)new InsnNode(191));
        methodNode.instructions = insn;
        methods.add(methodNode);
        classNode.methods = methods;
        ClassWriter classWriter = new ClassWriter(2);
        classNode.accept((ClassVisitor)classWriter);
        return classWriter.toByteArray();
    }

    private static void dumpDetected() {
        NotifyUtils.notice("L", "\u4f60\u5988\u6b7b\u4e86\u8001\u5f1f");
    }

    private static void resolveClassLoader() throws NoSuchMethodException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("windows")) {
            String vmName = System.getProperty("java.vm.name");
            String dll = vmName.contains("Client VM") ? "/bin/client/jvm.dll" : "/bin/server/jvm.dll";
            try {
                System.load(System.getProperty("java.home") + dll);
            }
            catch (UnsatisfiedLinkError e) {
                throw new RuntimeException(e);
            }
            classLoader = AntiDump.class.getClassLoader();
        } else {
            classLoader = null;
        }
        findNative = ClassLoader.class.getDeclaredMethod("findNative", ClassLoader.class, String.class);
        try {
            Class<?> cls = ClassLoader.getSystemClassLoader().loadClass("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            unsafe.putObjectVolatile(cls, unsafe.staticFieldOffset(logger), null);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        findNative.setAccessible(true);
    }

    private static void setupIntrospection() throws Throwable {
        AntiDump.resolveClassLoader();
    }

    public static void disassembleStruct() {
        try {
            AntiDump.setupIntrospection();
            long entry = AntiDump.getSymbol("gHotSpotVMStructs");
            unsafe.putLong(entry, 0L);
        }
        catch (Throwable t2) {
            t2.printStackTrace();
        }
    }

    private static long getSymbol(String symbol) throws InvocationTargetException, IllegalAccessException {
        long address = (Long)findNative.invoke(null, classLoader, symbol);
        if (address == 0L) {
            throw new NoSuchElementException(symbol);
        }
        return unsafe.getLong(address);
    }

    private static String getString(long addr) {
        byte b2;
        if (addr == 0L) {
            return null;
        }
        char[] chars = new char[40];
        int offset = 0;
        while ((b2 = unsafe.getByte(addr + (long)offset)) != 0) {
            if (offset >= chars.length) {
                chars = Arrays.copyOf(chars, offset * 2);
            }
            chars[offset++] = (char)b2;
        }
        return new String(chars, 0, offset);
    }

    private static void readStructs(Map<String, Set<Object[]>> structs) throws InvocationTargetException, IllegalAccessException {
        long entry = AntiDump.getSymbol("gHotSpotVMStructs");
        long typeNameOffset = AntiDump.getSymbol("gHotSpotVMStructEntryTypeNameOffset");
        long fieldNameOffset = AntiDump.getSymbol("gHotSpotVMStructEntryFieldNameOffset");
        long typeStringOffset = AntiDump.getSymbol("gHotSpotVMStructEntryTypeStringOffset");
        long isStaticOffset = AntiDump.getSymbol("gHotSpotVMStructEntryIsStaticOffset");
        long offsetOffset = AntiDump.getSymbol("gHotSpotVMStructEntryOffsetOffset");
        long addressOffset = AntiDump.getSymbol("gHotSpotVMStructEntryAddressOffset");
        long arrayStride = AntiDump.getSymbol("gHotSpotVMStructEntryArrayStride");
        while (true) {
            String typeName = AntiDump.getString(unsafe.getLong(entry + typeNameOffset));
            String fieldName = AntiDump.getString(unsafe.getLong(entry + fieldNameOffset));
            if (fieldName == null) break;
            String typeString = AntiDump.getString(unsafe.getLong(entry + typeStringOffset));
            boolean isStatic = unsafe.getInt(entry + isStaticOffset) != 0;
            long offset = unsafe.getLong(entry + (isStatic ? addressOffset : offsetOffset));
            Set<Object[]> fields = structs.get(typeName);
            if (fields == null) {
                fields = new HashSet<Object[]>();
                structs.put(typeName, fields);
            }
            fields.add(new Object[]{fieldName, typeString, offset, isStatic});
            entry += arrayStride;
        }
        long address = (Long)findNative.invoke(null, classLoader, 2);
        if (address == 0L) {
            throw new NoSuchElementException("");
        }
        unsafe.getLong(address);
    }

    private static void readTypes(Map<String, Object[]> types, Map<String, Set<Object[]>> structs) throws InvocationTargetException, IllegalAccessException {
        String typeName;
        long entry = AntiDump.getSymbol("gHotSpotVMTypes");
        long typeNameOffset = AntiDump.getSymbol("gHotSpotVMTypeEntryTypeNameOffset");
        long superclassNameOffset = AntiDump.getSymbol("gHotSpotVMTypeEntrySuperclassNameOffset");
        long isOopTypeOffset = AntiDump.getSymbol("gHotSpotVMTypeEntryIsOopTypeOffset");
        long isIntegerTypeOffset = AntiDump.getSymbol("gHotSpotVMTypeEntryIsIntegerTypeOffset");
        long isUnsignedOffset = AntiDump.getSymbol("gHotSpotVMTypeEntryIsUnsignedOffset");
        long sizeOffset = AntiDump.getSymbol("gHotSpotVMTypeEntrySizeOffset");
        long arrayStride = AntiDump.getSymbol("gHotSpotVMTypeEntryArrayStride");
        while ((typeName = AntiDump.getString(unsafe.getLong(entry + typeNameOffset))) != null) {
            String superclassName = AntiDump.getString(unsafe.getLong(entry + superclassNameOffset));
            boolean isOop = unsafe.getInt(entry + isOopTypeOffset) != 0;
            boolean isInt = unsafe.getInt(entry + isIntegerTypeOffset) != 0;
            boolean isUnsigned = unsafe.getInt(entry + isUnsignedOffset) != 0;
            int size = unsafe.getInt(entry + sizeOffset);
            Set<Object[]> fields = structs.get(typeName);
            types.put(typeName, new Object[]{typeName, superclassName, size, isOop, isInt, isUnsigned, fields});
            entry += arrayStride;
        }
    }
}

