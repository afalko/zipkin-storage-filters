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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zipkin.filter.SpanFilter;
import zipkin2.Call;
import zipkin2.Span;
import zipkin2.storage.SpanConsumer;
import zipkin2.storage.StorageComponent;

import java.util.List;

public class FiltersSpanConsumer implements SpanConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(FiltersSpanStore.class);

    private final List<SpanFilter> spanFilters;
    private final StorageComponent storageDelegate;

    public FiltersSpanConsumer(FiltersStorage filtersStorage) {
        this.spanFilters = filtersStorage.spanFilters();
        this.storageDelegate = filtersStorage.storageDelegate();
    }

    /**
     * Take the list of spans and pump them through pre-configured filters
     *
     * @param spans
     */
    List<Span> filterSpans(List<Span> spans) {
        List<Span> processed = spans;
        if (spanFilters == null) {
            return spans;
        }
        for (SpanFilter filter : spanFilters) {
            processed = filter.process(processed);
        }
        return processed;
    }

    @Override
    public Call<Void> accept(List<Span> spans) {
        LOG.debug("Running {} spans through {} filters", spans.size(), spanFilters.size());
        if (spans.size() > 0) {
            LOG.debug("Id of first span: {}", spans.get(0).id());
        }

        spans = filterSpans(spans);
        LOG.debug("Finished filtering, storing {} spans", spans.size());

        return storageDelegate.spanConsumer().accept(spans);
    }

}
