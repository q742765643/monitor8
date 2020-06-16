/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.oap.server.starter;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;


/**
 * OAP starter specific for the ES7 storage. This includes the same code of OAPServerStartUp in the `server-starter`
 * module.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.piesat.*"})
/*@EnableAutoConfiguration(exclude = {RedisRepositoriesAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class})

@ComponentScan(basePackages = {"com.piesat.*"})
@ServletComponentScan(basePackages={"com.piesat.*"})
@EnableJpaRepositories(basePackages = { "com.piesat" },repositoryBaseClass = GenericDaoImpl.class)
@EnableJpaAuditing
@EntityScan(basePackages = { "com.piesat" })
@MapperScan({"com.piesat.*.mapper","com.piesat.sod.*.mapper"})*/
public class OAPServerStartUp extends SpringBootServletInitializer {
    public static void main(String[] args) {
        new SpringApplicationBuilder(OAPServerStartUp.class)
                .properties("spring.config.location=classpath:/app.yml").run(args);
    }
}
