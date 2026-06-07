package com.algoquest.backend.judge;

public final class JavaMainGenerator {

    private JavaMainGenerator() {
    }

    public static String generate(ProblemJudgeConfig config) {
        StringBuilder builder = new StringBuilder();
        builder.append("import java.util.Arrays;\n\n");
        builder.append("public class Main {\n");
        builder.append("    public static void main(String[] args) {\n");
        builder.append("        boolean hasRuntimeError = false;\n");

        int index = 0;
        for (ProblemTestCase testCase : config.testCases()) {
            index++;
            builder.append("        try {\n");
            builder.append("            Solution solution = new Solution();\n");
            appendIndented(builder, testCase.setupCode(), 12);
            appendIndented(builder, testCase.executionCode(), 12);
            String comparisonCode = testCase.comparisonCode() == null || testCase.comparisonCode().isBlank()
                    ? "\"" + escapeJava(testCase.expected()) + "\".equals(actual)"
                    : testCase.comparisonCode();
            builder.append("            boolean passed = ").append(comparisonCode).append(";\n");
            builder.append("            System.out.println(formatCase(\"")
                    .append(escapeJava(testCase.input()))
                    .append("\", \"")
                    .append(escapeJava(testCase.expected()))
                    .append("\", actual, passed));\n");
            builder.append("        } catch (Throwable throwable) {\n");
            builder.append("            hasRuntimeError = true;\n");
            builder.append("            System.out.println(formatCase(\"")
                    .append(escapeJava(testCase.input()))
                    .append("\", \"")
                    .append(escapeJava(testCase.expected()))
                    .append("\", throwable.getClass().getSimpleName() + \": \" + String.valueOf(throwable.getMessage()), false));\n");
            builder.append("        }\n");
        }

        builder.append("        if (hasRuntimeError) {\n");
        builder.append("            System.err.println(\"Runtime error occurred while executing test cases.\");\n");
        builder.append("            System.exit(2);\n");
        builder.append("        }\n");
        builder.append("    }\n\n");
        builder.append("    private static String formatCase(String input, String expected, String actual, boolean passed) {\n");
        builder.append("        return \"CASE_RESULT|\" + safe(input) + \"|\" + safe(expected) + \"|\" + safe(actual) + \"|\" + passed;\n");
        builder.append("    }\n\n");
        builder.append("    private static String normalizeText(String value) {\n");
        builder.append("        if (value == null) {\n");
        builder.append("            return \"\";\n");
        builder.append("        }\n");
        builder.append("        return value.replace(\"\\r\\n\", \"\\n\").trim();\n");
        builder.append("    }\n\n");
        builder.append("    private static String safe(String value) {\n");
        builder.append("        if (value == null) {\n");
        builder.append("            return \"null\";\n");
        builder.append("        }\n");
        builder.append("        return value.replace(\"\\\\\", \"\\\\\\\\\").replace(\"|\", \"\\\\|\").replace(\"\\n\", \"\\\\n\").replace(\"\\r\", \"\\\\r\");\n");
        builder.append("    }\n");
        builder.append("}\n");
        return builder.toString();
    }

    private static void appendIndented(StringBuilder builder, String block, int spaces) {
        String indent = " ".repeat(spaces);
        for (String line : block.split("\\n")) {
            builder.append(indent).append(line).append('\n');
        }
    }

    private static String escapeJava(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}
