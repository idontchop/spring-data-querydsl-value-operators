package com.idontchop.spring.data.querydsl.value.operators.experimental.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.webmvc.config.ResourceMetadataHandlerMethodArgumentResolver;
import org.springframework.format.support.DefaultFormattingConversionService;

import com.idontchop.spring.data.querydsl.value.operators.experimental.QuerydslAwareRootResourceInformationHandlerMethodArgumentResolverBeanPostProcessor;

@Configuration
public class ValueOperatorConfiguration {
    
    @Bean
    QuerydslAwareRootResourceInformationHandlerMethodArgumentResolverBeanPostProcessor querydslAwareRootResourceInformationHandlerMethodArgumentResolverBeanPostProcessor(
            Repositories repositories,
            ResourceMetadataHandlerMethodArgumentResolver resourceMetadataHandlerMethodArgumentResolver,
            @Qualifier("repositoryInvokerFactory") RepositoryInvokerFactory repositoryInvokerFactory,
            QuerydslBindingsFactory querydslBindingsFactory) {
		return new QuerydslAwareRootResourceInformationHandlerMethodArgumentResolverBeanPostProcessor(
			repositories,
			resourceMetadataHandlerMethodArgumentResolver,
			repositoryInvokerFactory,
			querydslBindingsFactory,
			new DefaultFormattingConversionService()
		);
	}
}
