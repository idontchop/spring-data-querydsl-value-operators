package com.idontchop.spring.data.querydsl.value.operators.experimental;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.querydsl.binding.QuerydslPredicateBuilder;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;
import org.springframework.data.rest.webmvc.config.RootResourceInformationHandlerMethodArgumentResolver;
import org.springframework.data.web.querydsl.QuerydslPredicateArgumentResolver;

import com.idontchop.spring.data.querydsl.value.operators.ExpressionProviderFactory;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class QuerydslAwareRootResourceInformationHandlerMethodArgumentResolverBeanPostProcessor implements BeanPostProcessor {

    private final Repositories repositories;
    private final ResourceMetadataHandlerMethodArgumentResolver resourceMetadataHandlerMethodArgumentResolver;
    private final RepositoryInvokerFactory repositoryInvokerFactory;

    private final QuerydslBindingsFactory querydslBindingsFactory;

    private final ConversionService conversionServiceDelegate;

    private final Class[] delegatedConversions;

    public QuerydslAwareRootResourceInformationHandlerMethodArgumentResolverBeanPostProcessor(
        Repositories repositories,
		ResourceMetadataHandlerMethodArgumentResolver resourceMetadataHandlerMethodArgumentResolver,
		RepositoryInvokerFactory repositoryInvokerFactory,
        QuerydslBindingsFactory querydslBindingsFactory,
        ConversionService conversionServiceDelegate
    ) {
        this.repositories = repositories;
        this.resourceMetadataHandlerMethodArgumentResolver = resourceMetadataHandlerMethodArgumentResolver;
        this.repositoryInvokerFactory = repositoryInvokerFactory;
        this.querydslBindingsFactory = querydslBindingsFactory;
        this.conversionServiceDelegate = conversionServiceDelegate;

        // @TODO
        this.delegatedConversions = null;
    }

    private final ConversionService delegationAwareConversionService = new ConversionService() {

        @Override
        public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
            return (isDelegatedConversion(sourceType) || isDelegatedConversion(targetType)) && conversionServiceDelegate.canConvert(sourceType, targetType);
        }

        @Override
        public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return (isDelegatedConversion(sourceType.getType()) || isDelegatedConversion(targetType.getType())) && conversionServiceDelegate.canConvert(sourceType, targetType);
        }

        @Override
        public <T> T convert(Object source, Class<T> targetType) {
            if (isDelegatedConversion(source.getClass()) || isDelegatedConversion(targetType))
                return conversionServiceDelegate.convert(source, targetType);

            throw new UnsupportedOperationException("Overridden ConversionService in "
                + "QuerydslPredicateArgumentResolver does not " + "support conversion");
        }

        @Override
        public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (isDelegatedConversion(sourceType.getType()) || isDelegatedConversion(targetType.getType()))
                return conversionServiceDelegate.convert(source, sourceType, targetType);

            throw new UnsupportedOperationException("Overridden ConversionService in "
                + "QuerydslPredicateArgumentResolver does not " + "support conversion");
        }

        private boolean isDelegatedConversion(Class<?> type) {
            return Optional.ofNullable(conversionServiceDelegate)
                .filter(delegate -> type != null && delegatedConversions != null)
                .flatMap(delegate -> Arrays.stream(delegatedConversions)
                    .filter(c -> c.equals(type))
                    .findFirst())
                .isPresent();
        }
    };

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        log.debug("71 {} {}", bean.getClass().toString(), QuerydslUtils.QUERY_DSL_PRESENT);
        if (!QuerydslUtils.QUERY_DSL_PRESENT) return bean;

        if (bean != null && RootResourceInformationHandlerMethodArgumentResolver.class.isAssignableFrom(bean.getClass())) {
            ExpressionProviderFactory.setSupportsUnTypedValues(true);

            QuerydslBindingsFactory factory = this.querydslBindingsFactory;
			QuerydslPredicateBuilder predicateBuilder = new QuerydslPredicateBuilder(delegationAwareConversionService,
					factory.getEntityPathResolver());
            
            try {
                return ConstructorUtils.invokeConstructor(QuerydslAwareRootResourceInformationHandlerMethodArgumentResolver.class,
                    repositories,
                    repositoryInvokerFactory,
                    resourceMetadataHandlerMethodArgumentResolver,
                    predicateBuilder,
                    factory);
            } catch (Throwable t) {
                throw new RuntimeException("Failed to post-process QuerydslAwareRootResourceInformationHandlerMethodArgumentResolver", t);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
