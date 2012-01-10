package com.github.searls.jasmine.runner;

import org.antlr.stringtemplate.StringTemplate;

import java.io.IOException;
import java.util.Set;

import static java.util.Arrays.asList;

public class RequireJsSpecRunnerHtmlGenerator extends AbstractSpecRunnerHtmlGenerator implements SpecRunnerHtmlGenerator {

    public static final String REQUIRE_JS_HTML_TEMPLATE_FILE = "/jasmine-templates/RequireJsSpecRunner.htmltemplate";

    protected RequireJsSpecRunnerHtmlGenerator(HtmlGeneratorConfiguration configuration) {
        super(configuration);
    }

    public String generate() {
        try {
            return generateHtml(getConfiguration().getSpecs(), getConfiguration().getSourceDirectory());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load files for dependencies, sources, or a custom runner", e);
        }
    }

    public String generateWitRelativePaths() {
        try {
            return generateHtml(getConfiguration().getSpecsRelativePath(), getConfiguration().getSourceDirectoryRelativePath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load files for dependencies, sources, or a custom runner", e);
        }
    }

    private String generateHtml(Set<String> specsRelativePath, String sourceDirectory) throws IOException {
        StringTemplate template = resolveHtmlTemplate();

        includeJavaScriptDependencies(asList(JASMINE_JS, JASMINE_HTML_JS), template);
        applyCssToTemplate(asList(JASMINE_CSS), template);
        Set<String> preloads = getConfiguration().getPreloadsRelativePath();
        template.setAttribute("priority", createArrayOfScripts(preloads));
        template.setAttribute("requirejsPath", resolveRequirejsPath(sourceDirectory));
        setCustomRunnerConfig(template);
        template.setAttribute(REPORTER_ATTR_NAME, getConfiguration().getReporterType().name());
        template.setAttribute("sourceDir", sourceDirectory);
        template.setAttribute("specs", createArrayOfScripts(specsRelativePath));
        setEncoding(getConfiguration(), template);

        return template.toString();
    }

    private String resolveRequirejsPath(String sourceDirectory) {
        String scriptLoaderPath = getConfiguration().getScriptLoaderPath();
        if(null == scriptLoaderPath) {
            return String.format("%s/require.js", sourceDirectory);
        } else {
            return String.format("%s/%s", sourceDirectory, scriptLoaderPath);
        }
    }

    private void setCustomRunnerConfig(StringTemplate template) throws IOException {
        String customRunnerConfiguration = getConfiguration().getCustomRunnerConfiguration();
        if (null != customRunnerConfiguration) {
            template.setAttribute("customRunnerConfiguration", customRunnerConfiguration);
        }
    }

    private String createArrayOfScripts(Set<String> scripts) throws IOException {
        if (null == scripts || scripts.isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder("[");
        for (String script : scripts) {
            builder.append("'" + script + "'");
            builder.append(", ");
        }
        if (!scripts.isEmpty()) {
            builder.delete(builder.lastIndexOf(", "), builder.length());
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    protected String getDefaultHtmlTemplatePath() {
        return REQUIRE_JS_HTML_TEMPLATE_FILE;
    }
}
