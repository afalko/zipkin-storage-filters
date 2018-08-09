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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import zipkin.filter.SpanFilter;
import zipkin2.storage.StorageComponent;
import zipkin2.storage.filters.FiltersStorage;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@ConfigurationProperties("zipkin.storage.filters")
public class ZipkinFiltersStorageProperties implements Serializable {
    /** Class name of the storage type we ultimately want to write to */
    private String destinationStorage;

    public void setDestinationStorage(String destinationStorage) {
        this.destinationStorage = destinationStorage;
    }

    public String getDestinationStorage() {
        return destinationStorage;
    }

    @Bean
    @Autowired
    public StorageComponent getDestinationStorageDriver(ApplicationContext applicationContext) {
        Map<String, StorageComponent> allStorageComponents = applicationContext.getBeansOfType(StorageComponent.class);
        for (String componentName : allStorageComponents.keySet()) {
            if (componentName.equals(destinationStorage)) {
                return allStorageComponents.get(componentName);
            }
        }
        return null;
    }

    @Autowired
    private StorageComponent destinationStorageDriver;

    @Autowired
    private List<SpanFilter> filters;

    public FiltersStorage.Builder toBuilder() {
        final FiltersStorage.Builder result = FiltersStorage.newBuilder();
        result.storageDelegate(destinationStorageDriver);
        result.spanFilters(filters);
        return result;
    }
}
