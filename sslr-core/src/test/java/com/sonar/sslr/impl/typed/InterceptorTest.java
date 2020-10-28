/*
 * SonarSource Language Recognizer
 * Copyright (C) 2010-2020 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.sonar.sslr.impl.typed;

import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

public class InterceptorTest {

  public static class Target extends BaseTarget {
    private final Object p;

    public Target(Object p) {
      this.p = p;
    }

    public Object m() {
      return "m()";
    }

    public Object overloaded() {
      return "overloaded()";
    }

    public Object overloaded(Object p) {
      return "overloaded(" + p + ")";
    }

    @Override
    public Object overridden() {
      return "Target.overridden()";
    }

    private Object privateMethod() {
      return "privateMethod()";
    }

    Object packageLocalMethod() {
      return "packageLocalMethod()";
    }
  }

  public static class BaseTarget {
    @SuppressWarnings("unused")
    public Object overridden() {
      return "BaseTarget.overridden()";
    }

    public Object base() {
      return "base()";
    }
  }

  private boolean intercept = false;
  private final ArrayList<Method> interceptedMethods = new ArrayList<>();
  private final MethodInterceptor methodInterceptor = method -> {
    interceptedMethods.add(method);
    return intercept;
  };
  private final Target interceptedTarget = (Target) Interceptor.create(
    Target.class,
    new Class[]{Object.class},
    new Object[]{"arg"},
    methodInterceptor
  );

  @Test
  public void should_invoke_constructor() {
    assertEquals("arg", interceptedTarget.p);
  }

  @Test
  public void should_intercept() {
    assertEquals("m()", interceptedTarget.m());
    assertEquals(1, interceptedMethods.size());

    intercept = true;
    assertNull(interceptedTarget.m());
    assertEquals(2, interceptedMethods.size());
  }

  @Test
  public void should_intercept_overloaded_methods() {
    assertEquals("overloaded()", interceptedTarget.overloaded());
    assertEquals(1, interceptedMethods.size());

    assertEquals("overloaded(arg)", interceptedTarget.overloaded("arg"));
    assertEquals(2, interceptedMethods.size());
  }

  @Test
  public void should_intercept_overridden_methods() {
    assertEquals("Target.overridden()", interceptedTarget.overridden());
    assertEquals(1, interceptedMethods.size());
  }

  @Test
  public void should_intercept_base_methods() {
    assertEquals("base()", interceptedTarget.base());
    assertEquals(1, interceptedMethods.size());
  }

  /**
   * Can not intercept non-public methods,
   * but should not fail in their presence,
   * because SonarTSQL uses private helper methods.
   */
  @Test
  public void can_not_intercept_non_public_methods() {
    assertEquals("privateMethod()", interceptedTarget.privateMethod());
    assertEquals("packageLocalMethod()", interceptedTarget.packageLocalMethod());
    assertEquals(0, interceptedMethods.size());

    assertEquals(Arrays.asList("base", "m", "overloaded", "overloaded", "overridden"),
      Arrays.stream(interceptedTarget.getClass().getDeclaredMethods())
        .map(Method::getName)
        .sorted()
        .collect(Collectors.toList()));
  }

  @Test
  public void requires_class_to_be_public() {
    IllegalAccessError thrown = assertThrows(IllegalAccessError.class,
      () -> Interceptor.create(NonPublicClass.class, new Class<?>[]{}, new Object[]{}, methodInterceptor));
    assertThat(thrown.getMessage())
      // Note that details of the message are different between JDK versions
      .startsWith("class GeneratedBySSLR cannot access its superclass com.sonar.sslr.impl.typed.InterceptorTest$NonPublicClass");
  }

  private static class NonPublicClass {
  }

  /**
   * @see #can_not_intercept_non_public_methods()
   */
  @Test
  public void requires_final_methods_to_be_non_public() {
    VerifyError thrown = assertThrows(VerifyError.class,
      () -> Interceptor.create(PublicFinalMethod.class, new Class[]{}, new Object[]{}, methodInterceptor));
    assertThat(thrown.getMessage())
      // Note that details of the message are different between JDK versions
      .startsWith("class GeneratedBySSLR overrides final method");
  }

  public static class PublicFinalMethod {
    @SuppressWarnings("unused")
    public final Object m() {
      return null;
    }
  }

  @Test
  public void requires_non_primitive_return_types() {
    assertThrows(UnsupportedOperationException.class,
      () -> Interceptor.create(PrimitiveReturnType.class, new Class[]{}, new Object[]{}, methodInterceptor));
  }

  public static class PrimitiveReturnType {
    @SuppressWarnings("unused")
    public void m() {
    }
  }

  @Test
  public void should_use_ClassLoader_of_intercepted_class() throws Exception {
    ClassWriter cv = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
    cv.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC, "Target", null, "java/lang/Object", null);
    MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
    mv.visitVarInsn(Opcodes.ALOAD, 0);
    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
    mv.visitInsn(Opcodes.RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
    mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "m", "()Ljava/lang/String;", null, null);
    mv.visitLdcInsn("m()");
    mv.visitInsn(Opcodes.ARETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();

    byte[] classBytes = cv.toByteArray();

    Class<?> cls = new ClassLoader() {
      public Class<?> defineClass() {
        return defineClass("Target", classBytes, 0, classBytes.length);
      }
    }.defineClass();

    Object interceptedTarget = Interceptor.create(cls, new Class[]{}, new Object[]{}, methodInterceptor);
    assertEquals("m()", interceptedTarget.getClass().getMethod("m").invoke(interceptedTarget));
    assertEquals(1, interceptedMethods.size());
  }

}
