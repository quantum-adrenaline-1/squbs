/*
 * Licensed to Typesafe under one or more contributor license agreements.
 * See the AUTHORS file distributed with this work for
 * additional information regarding copyright ownership.
 * This file is licensed to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.squbs.unicomplex

import java.io._
import akka.actor.ActorSystem
import org.squbs.lifecycle.GracefulStop

object Bootstrap extends App {

  println("Booting unicomplex")

  // Note, the config directories may change during extension init. It is important to re-read the full config
  // for the actor system start.
  // TODO need to solve the classpath issue
  UnicomplexBoot { (name, config) => ActorSystem(name, config) }
    .scanComponents(
      System.getProperty("java.class.path").split(File.pathSeparator) ++ 
        System.getProperty("java.ext.dirs").split(File.pathSeparator)
    )
    .initExtensions
    .stopJVMOnExit
    .start()
}


object Shutdown extends App {
  val preConfig = UnicomplexBoot.getFullConfig(None)
  val actorSystemName = preConfig.getString("squbs.actorsystem-name")
  Unicomplex(actorSystemName) ! GracefulStop
}