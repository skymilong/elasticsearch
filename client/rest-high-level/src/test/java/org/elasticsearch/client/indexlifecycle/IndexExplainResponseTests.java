/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.client.indexlifecycle;

import org.elasticsearch.cluster.ClusterModule;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.ToXContentObject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.test.AbstractXContentTestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class IndexExplainResponseTests extends AbstractXContentTestCase<IndexLifecycleExplainResponse> {

    static IndexLifecycleExplainResponse randomIndexExplainResponse() {
        if (frequently()) {
            return randomManagedIndexExplainResponse();
        } else {
            return randomUnmanagedIndexExplainResponse();
        }
    }

    private static IndexLifecycleExplainResponse randomUnmanagedIndexExplainResponse() {
        return IndexLifecycleExplainResponse.newUnmanagedIndexResponse(randomAlphaOfLength(10));
    }

    private static IndexLifecycleExplainResponse randomManagedIndexExplainResponse() {
        return IndexLifecycleExplainResponse.newManagedIndexResponse(randomAlphaOfLength(10), randomAlphaOfLength(10),
                randomNonNegativeLong(), randomAlphaOfLength(10), randomAlphaOfLength(10), randomAlphaOfLength(10),
                randomBoolean() ? null : randomAlphaOfLength(10), randomNonNegativeLong(), randomNonNegativeLong(), randomNonNegativeLong(),
                randomBoolean() ? null : new BytesArray(new RandomStepInfo(() -> randomAlphaOfLength(10)).toString()),
                randomBoolean() ? null : PhaseExecutionInfoTests.randomPhaseExecutionInfo(""));
    }

    @Override
    protected IndexLifecycleExplainResponse createTestInstance() {
        return randomIndexExplainResponse();
    }

    @Override
    protected IndexLifecycleExplainResponse doParseInstance(XContentParser parser) throws IOException {
        return IndexLifecycleExplainResponse.PARSER.apply(parser, null);
    }

    @Override
    protected boolean supportsUnknownFields() {
        return false;
    }

    private static class RandomStepInfo implements ToXContentObject {

        private final String key;
        private final String value;

        RandomStepInfo(Supplier<String> randomStringSupplier) {
            this.key = randomStringSupplier.get();
            this.value = randomStringSupplier.get();
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            builder.field(key, value);
            builder.endObject();
            return builder;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            RandomStepInfo other = (RandomStepInfo) obj;
            return Objects.equals(key, other.key) && Objects.equals(value, other.value);
        }

        @Override
        public String toString() {
            return Strings.toString(this);
        }
    }

    @Override
    protected NamedXContentRegistry xContentRegistry() {
        List<NamedXContentRegistry.Entry> entries = new ArrayList<>(ClusterModule.getNamedXWriteables());
        entries.add(new NamedXContentRegistry.Entry(LifecycleAction.class, new ParseField(DeleteAction.NAME), DeleteAction::parse));
        return new NamedXContentRegistry(entries);
    }
}