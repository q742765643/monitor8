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

package org.apache.skywalking.oap.server.receiver.trace.provider.handler.v8.grpc;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.network.common.v3.Commands;
import org.apache.skywalking.apm.network.language.agent.v3.SegmentObject;
import org.apache.skywalking.apm.network.language.agent.v3.TraceSegmentReportServiceGrpc;
import org.apache.skywalking.oap.server.library.module.ModuleManager;
import org.apache.skywalking.oap.server.library.server.grpc.GRPCHandler;
import org.apache.skywalking.oap.server.receiver.trace.provider.TraceServiceModuleConfig;
import org.apache.skywalking.oap.server.receiver.trace.provider.parser.SegmentParserListenerManager;
import org.apache.skywalking.oap.server.receiver.trace.provider.parser.TraceAnalyzer;
import org.apache.skywalking.oap.server.telemetry.TelemetryModule;
import org.apache.skywalking.oap.server.telemetry.api.CounterMetrics;
import org.apache.skywalking.oap.server.telemetry.api.HistogramMetrics;
import org.apache.skywalking.oap.server.telemetry.api.MetricsCreator;
import org.apache.skywalking.oap.server.telemetry.api.MetricsTag;

@Slf4j
public class TraceSegmentReportServiceHandler extends TraceSegmentReportServiceGrpc.TraceSegmentReportServiceImplBase implements GRPCHandler {
    private final ModuleManager moduleManager;
    private final SegmentParserListenerManager listenerManager;
    private final TraceServiceModuleConfig config;
    private HistogramMetrics histogram;
    private CounterMetrics errorCounter;

    public TraceSegmentReportServiceHandler(ModuleManager moduleManager,
                                            SegmentParserListenerManager listenerManager,
                                            TraceServiceModuleConfig config) {
        this.moduleManager = moduleManager;
        this.listenerManager = listenerManager;
        this.config = config;
        MetricsCreator metricsCreator = moduleManager.find(TelemetryModule.NAME)
                                                     .provider()
                                                     .getService(MetricsCreator.class);
        histogram = metricsCreator.createHistogramMetric(
            "trace_in_latency", "The process latency of trace data",
            new MetricsTag.Keys("protocol"), new MetricsTag.Values("grpc")
        );
        errorCounter = metricsCreator.createCounter("trace_analysis_error_count", "The error number of trace analysis",
                                                    new MetricsTag.Keys("protocol"), new MetricsTag.Values("grpc")
        );
    }

    @Override
    public StreamObserver<SegmentObject> collect(StreamObserver<Commands> responseObserver) {
        return new StreamObserver<SegmentObject>() {
            @Override
            public void onNext(SegmentObject segment) {
                if (log.isDebugEnabled()) {
                    log.debug("receive segment");
                }

                HistogramMetrics.Timer timer = histogram.createTimer();
                try {
                    final TraceAnalyzer traceAnalyzer = new TraceAnalyzer(
                        moduleManager, listenerManager, config);
                    traceAnalyzer.doAnalysis(segment);
                } catch (Exception e) {
                    errorCounter.inc();
                } finally {
                    timer.finish();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                log.error(throwable.getMessage(), throwable);
                responseObserver.onCompleted();
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(Commands.newBuilder().build());
                responseObserver.onCompleted();
            }
        };
    }
}
