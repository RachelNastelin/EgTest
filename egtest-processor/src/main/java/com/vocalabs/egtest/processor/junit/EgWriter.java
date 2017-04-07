package com.vocalabs.egtest.processor.junit;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.vocalabs.egtest.annotation.EgLanguage;
import com.vocalabs.egtest.processor.data.ReturnsExample;
import com.vocalabs.egtest.processor.data.ReturnsWithDeltaExample;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/** Writer for {@code @Eg(...)} annotations. */
class EgWriter extends JUnitExampleWriter<ExecutableElement, ReturnsExample> {
    public EgWriter(ExecutableElement element, List<ReturnsExample> examples, JUnitClassWriter classWriter, TypeSpec.Builder toAddTo) {
        super(element, examples, classWriter, toAddTo);
    }

    @Override
    protected String baseName() {
        return "Returns";
    }

    @Override
    public void addTests() {
        if ( ! checkSupport())
            return;

        String newMethodName = testMethodName();
        MethodSpec.Builder specBuilder = MethodSpec.methodBuilder(newMethodName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(testAnnotation)
                .addException(Exception.class)
                .returns(void.class);

        ClassName assertion = ClassName.get("org.junit.Assert", "assertEquals");
        ClassName className = ClassName.get((TypeElement) element.getEnclosingElement());
        CodeInjector codeInjector = classWriter.getCodeInjector();
        for (ReturnsExample example: examples) {
            EgLanguage language = example.getAnnotation().language();
            LanguageInjector languageInjector = codeInjector.languageInjector(language);
            String[] arguments = example.getAnnotation().given();
            String argumentString = String.join(", ", example.getAnnotation().given());
            String expected  = example.getAnnotation().returns();
            String description = element.getSimpleName()+"("+argumentString+")";
            String methodName = element.getSimpleName().toString();

            specBuilder.addCode("$L($S,\n    ", assertion, description);
            languageInjector.add(specBuilder, element.getReturnType(), expected);
            specBuilder.addCode(",\n");

            if (element.getModifiers().contains(Modifier.STATIC)) {
                specBuilder.addCode("    $T.$L(", className, methodName);
                addArguments(specBuilder, languageInjector, arguments);
                specBuilder.addCode(")");
            }
            else {
                String constructorArgs = String.join(", ", example.getAnnotation().construct());
                specBuilder.addCode("    new $T($L).$L(", className, constructorArgs, methodName);
                addArguments(specBuilder, languageInjector, arguments);
                specBuilder.addCode(")");
            }
            if (example instanceof ReturnsWithDeltaExample) {
                specBuilder.addCode(", $L", ((ReturnsWithDeltaExample) example).deltaString());
            }

            specBuilder.addCode(");\n");
        }
        toAddTo.addMethod(specBuilder.build());
    }

    private void addArguments(MethodSpec.Builder specBuilder, LanguageInjector languageInjector, String[] arguments) {
        List<? extends VariableElement> parameters = element.getParameters();
        int argPos=0;
        for (VariableElement param: parameters) {
            if (element.isVarArgs()  &&  argPos == parameters.size() - 1) {
                addVarArgs(specBuilder, languageInjector, arguments, parameters, param);
            }
            else {
                if (argPos > 0)
                    specBuilder.addCode(", ");
                String argument = arguments[argPos];
                languageInjector.add(specBuilder, param.asType(), argument);
            }
            argPos++;
        }
    }

    private void addVarArgs(MethodSpec.Builder specBuilder,
                            LanguageInjector languageInjector,
                            String[] arguments,
                            List<? extends VariableElement> parameters,
                            VariableElement param)
    {
        if (parameters.size() <= arguments.length) {
            ArrayType varargType = (ArrayType) param.asType();
            TypeMirror type = varargType.getComponentType();
            for (int argPos = parameters.size()-1; argPos < arguments.length; argPos++) {
                if (argPos > 0)
                    specBuilder.addCode(", ");
                String argument = arguments[argPos];
                languageInjector.add(specBuilder, type, argument);
            }
        }
    }
}
