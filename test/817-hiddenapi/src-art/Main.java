/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import dalvik.system.PathClassLoader;
import java.io.File;
import java.lang.reflect.Method;

public class Main {

  public static void main(String[] args) throws Exception {
    System.loadLibrary(args[0]);

    // Enable hidden API checks in case they are disabled by default.
    init();

    // Put the classes with hiddenapi bits in the boot classpath.
    appendToBootClassLoader(DEX_PARENT_BOOT, /* isCorePlatform */ false);

    // Create a new class loader so the TestCase class sees the InheritAbstract classes in the boot
    // classpath.
    ClassLoader childLoader = new PathClassLoader(DEX_CHILD, Object.class.getClassLoader());
    Class<?> cls = Class.forName("TestCase", true, childLoader);
    Method m = cls.getDeclaredMethod("test");
    m.invoke(null);
  }

  private static final String DEX_PARENT_BOOT =
      new File(new File(System.getenv("DEX_LOCATION"), "res"), "boot.jar").getAbsolutePath();
  private static final String DEX_CHILD =
      new File(System.getenv("DEX_LOCATION"), "817-hiddenapi-ex.jar").getAbsolutePath();

  private static native int appendToBootClassLoader(String dexPath, boolean isCorePlatform);
  private static native void init();
}
