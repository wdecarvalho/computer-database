package com.excilys.extensions;

import java.lang.reflect.Parameter;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class MockitoExtension implements TestInstancePostProcessor, ParameterResolver {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        MockitoAnnotations.initMocks(testInstance);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.getParameter().isAnnotationPresent(Mock.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return getMock(parameterContext.getParameter(), extensionContext);
    }

    /**
     * Permet de recuperer un Mock.
     * @param parameter parameter
     * @param extensionContext ExtensionContext
     * @return Retourne un mock
     */
    private Object getMock(Parameter parameter, ExtensionContext extensionContext) {

        Class<?> mockType = parameter.getType();
        Store mocks = extensionContext.getStore(Namespace.create(MockitoExtension.class, mockType));
        String mockName = getMockName(parameter);

        if (mockName != null) {
            return mocks.getOrComputeIfAbsent(mockName, key -> Mockito.mock(mockType, mockName));
        } else {
            return mocks.getOrComputeIfAbsent(mockType.getCanonicalName(), key -> Mockito.mock(mockType));
        }
    }
    /**
     * Permet de recuperer le nom d'un mock.
     * @param parameter Parameter
     * @return Nom du mock
     */
    private String getMockName(Parameter parameter) {
        String explicitMockName = parameter.getAnnotation(Mock.class).name().trim();
        if (!explicitMockName.isEmpty()) {
            return explicitMockName;
        } else if (parameter.isNamePresent()) {
            return parameter.getName();
        }
        return null;
    }
}