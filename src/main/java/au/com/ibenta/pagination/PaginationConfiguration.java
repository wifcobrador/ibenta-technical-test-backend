package au.com.ibenta.pagination;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;

@Configuration
@EnableConfigurationProperties(SpringDataWebProperties.class)
public class PaginationConfiguration {

    @Bean
    @ConditionalOnMissingBean
    PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer(final SpringDataWebProperties properties) {
        return resolver -> {
            SpringDataWebProperties.Pageable pageable = properties.getPageable();
            resolver.setPageParameterName(pageable.getPageParameter());
            resolver.setSizeParameterName(pageable.getSizeParameter());
            resolver.setOneIndexedParameters(pageable.isOneIndexedParameters());
            resolver.setPrefix(pageable.getPrefix());
            resolver.setQualifierDelimiter(pageable.getQualifierDelimiter());
            resolver.setFallbackPageable(PageRequest.of(0, pageable.getDefaultPageSize()));
            resolver.setMaxPageSize(pageable.getMaxPageSize());
        };
    }

    @Bean
    @ConditionalOnMissingBean
    SortHandlerMethodArgumentResolverCustomizer sortCustomizer(final SpringDataWebProperties properties) {
        return resolver -> resolver.setSortParameter(properties.getSort().getSortParameter());
    }

    @Bean
    PageableHandlerMethodArgumentResolver pageableResolver(
            final PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer,
            final SortHandlerMethodArgumentResolver sortResolver) {

        PageableHandlerMethodArgumentResolver handler = new PageableHandlerMethodArgumentResolver(sortResolver);

        if (pageableCustomizer != null) {
            pageableCustomizer.customize(handler);
        }

        return handler;
    }

    @Bean
    SortHandlerMethodArgumentResolver sortResolver(final SortHandlerMethodArgumentResolverCustomizer sortCustomizer) {

        SortHandlerMethodArgumentResolver resolver = new SortHandlerMethodArgumentResolver();

        if (sortCustomizer != null) {
            sortCustomizer.customize(resolver);
        }

        return resolver;
    }
}
