/*
 * Copyright 2015-2018 The OpenZipkin Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package zipkin.autoconfigure.storage.filters;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.storage.StorageComponent;

/**
 * Autoconfiguration for ZipkinFilter storage
 */
@Configuration
@EnableConfigurationProperties(ZipkinFiltersStorageProperties.class)
//@ConditionalOnProperty(name = "zipkin.storage.type", havingValue = "filters")
@ConditionalOnMissingBean(StorageComponent.class)
class ZipkinFiltersStorageAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    StorageComponent storage(ZipkinFiltersStorageProperties properties) {
        return properties.toBuilder().build();
    }
}
