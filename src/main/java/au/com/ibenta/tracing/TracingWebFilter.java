package au.com.ibenta.tracing;

import brave.Span;
import brave.propagation.TraceContext;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoOperator;
import reactor.util.context.Context;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.Long.toHexString;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.cloud.sleuth.instrument.web.TraceWebServletAutoConfiguration.TRACING_FILTER_ORDER;

@Component
@Order(TRACING_FILTER_ORDER + 1)
public class TracingWebFilter implements WebFilter {

    public static final String X_B3_SPAN_ID = "X-B3-SpanId";
    public static final String X_B3_TRACE_ID = "X-B3-TraceId";
    private static final Logger log = getLogger(TracingWebFilter.class);

    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
        final var source = log(exchange.getRequest()).then(chain.filter(exchange));
        return new TracingWebFilterMono(source, exchange).subscriberContext(setTraceHeaders(exchange));
    }

    private Mono<String> log(final ServerHttpRequest request) {
        final var bos = new ByteArrayOutputStream();
        final var method = request.getMethod();
        final var url = request.getURI().toString();
        return DataBufferUtils.write(request.getBody(), bos).collectList().
                map(r -> bos)
                .map(ByteArrayOutputStream::toByteArray)
                .map(String::new)
                .map(String::trim)
                .doOnNext(content -> log.info("{} {} {}", method, url, content));
    }

    private Function<Context, Context> setTraceHeaders(final ServerWebExchange exchange) {
        return context -> {
            ofNullable(context.get(Span.class)).ifPresent(span -> {
                final TraceContext traceContext = span.context();
                final HttpHeaders headers = exchange.getResponse().getHeaders();
                headers.add(X_B3_SPAN_ID, toHexString(traceContext.spanId()));
                headers.add(X_B3_TRACE_ID, traceContext.traceIdString());
            });
            return context;
        };
    }

    private static final class TracingWebFilterMono extends MonoOperator<Void, Void> {

        private final ServerWebExchange exchange;

        protected TracingWebFilterMono(Mono<? extends Void> source, final ServerWebExchange exchange) {
            super(source);
            this.exchange = exchange;
        }

        @Override
        public void subscribe(CoreSubscriber<? super Void> actual) {
            Context context = actual.currentContext();
            source.subscribe(new TracingWebFilterSubscriber(actual, context, exchange));
        }

        private final class TracingWebFilterSubscriber implements CoreSubscriber<Void> {

            private static final String SERVICE_URL = "serviceUrl";

            private final CoreSubscriber<? super Void> actual;

            private Context context;
            private MDC.MDCCloseable mdc;

            private TracingWebFilterSubscriber(final CoreSubscriber<? super Void> actual,
                                               final Context context,
                                               final ServerWebExchange exchange) {
                this.actual = actual;
                this.context = context;

                getServiceUrlFrom(exchange).ifPresent(this::putToContext);
            }

            private void putToContext(final String serviceUrl) {
                mdc = MDC.putCloseable(SERVICE_URL, serviceUrl);
                context = context.put(SERVICE_URL, serviceUrl);
            }

            @Override
            public void onSubscribe(Subscription subscription) {
                actual.onSubscribe(subscription);
            }

            @Override
            public void onNext(Void next) {
                // IGNORE
                actual.onNext(next);
            }

            @Override
            public void onError(Throwable throwable) {
                cleanupContext();
                actual.onError(throwable);
            }

            @Override
            public void onComplete() {
                cleanupContext();
                actual.onComplete();
            }

            @Override
            public Context currentContext() {
                return context;
            }

            private void cleanupContext() {
                log.trace("Subscription completed cleaning up context...");
                context.delete(SERVICE_URL);
                getOptionalMdc().ifPresent(MDC.MDCCloseable::close);
            }

            private Optional<MDC.MDCCloseable> getOptionalMdc() {
                return Optional.ofNullable(mdc);
            }

            private Optional<String> getServiceUrlFrom(final ServerWebExchange exchange) {
                return Optional.ofNullable(exchange)
                        .map(ServerWebExchange::getRequest)
                        .map(ServerHttpRequest::getURI)
                        .map(URI::toString);
            }
        }
    }
}

