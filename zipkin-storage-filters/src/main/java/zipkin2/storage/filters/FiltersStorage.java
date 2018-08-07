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
package zipkin2.storage.filters;

import com.google.auto.value.AutoValue;
import zipkin.filter.SpanFilter;
import zipkin2.CheckResult;
import zipkin2.storage.SpanConsumer;
import zipkin2.storage.SpanStore;
import zipkin2.storage.StorageComponent;

import java.util.Collections;
import java.util.List;

@AutoValue
public abstract class FiltersStorage extends StorageComponent {
  public static Builder newBuilder() {
    return new AutoValue_FiltersStorage.Builder()
      .spanFilters(Collections.emptyList())
      .storageDelegate(null)
      .searchEnabled(false) // This storage driver doesn't support reading
      .strictTraceId(true);
  }

  @AutoValue.Builder
  public static abstract class Builder extends StorageComponent.Builder {
    public abstract Builder spanFilters(List<SpanFilter> spanFilters);

    public abstract Builder storageDelegate(StorageComponent storageComponent);

    @Override
    public abstract Builder strictTraceId(boolean strictTraceId);

    @Override
    public abstract Builder searchEnabled(boolean searchEnabled);

    @Override
    abstract public FiltersStorage build();

    Builder() {
    }
  }

  abstract List<SpanFilter> spanFilters();

  abstract StorageComponent storageDelegate();

  public abstract boolean strictTraceId();

  abstract boolean searchEnabled();

  @Override
  public SpanStore spanStore() {
    return new FiltersSpanStore(this);
  }

  @Override
  public SpanConsumer spanConsumer() {
    return new FiltersSpanConsumer(this);
  }

  /** This is blocking so that we can determine if the cluster is healthy or not */
  @Override
  public CheckResult check() {
    return ensureFiltersReady();
  }

  CheckResult ensureFiltersReady() {
    return CheckResult.OK;
  }

  @Override
  public void close() {
  }

  FiltersStorage() {
  }
}
