package com.algoquest.backend.judge;

import com.algoquest.backend.judge.spec.ComparisonStrategy;
import com.algoquest.backend.judge.spec.InputValue;
import com.algoquest.backend.judge.spec.JudgeSpecData;
import com.algoquest.backend.judge.spec.ParamSpec;
import com.algoquest.backend.judge.spec.TestCaseData;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JavaMainGenerator {

    private static final Logger log = LoggerFactory.getLogger(JavaMainGenerator.class);

    static final Set<String> ALLOWED_TYPES = Set.of(
            "int", "long", "double", "boolean", "String",
            "int[]", "long[]", "double[]", "String[]",
            "List<Integer>", "List<String>", "List<List<Integer>>"
    );

    private static final Set<String> UNORDERED_ALLOWED_RETURN_TYPES = Set.of(
            "int[]", "long[]", "List<Integer>"
    );

    private static final Set<String> FLOAT_TOLERANCE_ALLOWED_RETURN_TYPES = Set.of(
            "double", "float"
    );

    private JavaMainGenerator() {
    }

    // ── Spec-driven path ──────────────────────────────────────────────────────

    public static String generateFromSpec(JudgeSpecData spec, List<TestCaseData> testCases) {
        validateSpec(spec);

        boolean needsArrays = needsArraysImport(spec);
        boolean needsList = needsListImport(spec);

        StringBuilder builder = new StringBuilder();
        if (needsArrays) builder.append("import java.util.Arrays;\n");
        if (needsList) {
            builder.append("import java.util.List;\n");
            builder.append("import java.util.ArrayList;\n");
            builder.append("import java.util.Collections;\n");
        }
        builder.append("import java.nio.charset.StandardCharsets;\n");
        builder.append("import java.nio.file.Files;\n");
        builder.append("import java.nio.file.Path;\n");
        builder.append("\n");
        builder.append("public class Main {\n");
        builder.append("    private static final java.util.List<String> JUDGE_RESULTS = new java.util.ArrayList<>();\n");
        builder.append("    public static void main(String[] args) throws Exception {\n");
        builder.append("        boolean hasRuntimeError = false;\n");

        for (TestCaseData tc : testCases) {
            String setupCode    = buildSetupCode(tc.inputs());
            String executionCode = buildExecutionCode(spec, tc.inputs());
            String expectedStr  = buildExpectedStr(spec, tc.expected().value());
            String comparisonCode = buildComparisonCode(spec, expectedStr);

            builder.append("        try {\n");
            builder.append("            Solution solution = new Solution();\n");
            appendIndented(builder, setupCode, 12);
            appendIndented(builder, executionCode, 12);
            builder.append("            boolean passed = ").append(comparisonCode).append(";\n");
            builder.append("            JUDGE_RESULTS.add(formatCase(\"")
                    .append(escapeJava(tc.displayInput()))
                    .append("\", \"")
                    .append(escapeJava(expectedStr))
                    .append("\", actual, passed));\n");
            builder.append("        } catch (Throwable throwable) {\n");
            builder.append("            hasRuntimeError = true;\n");
            builder.append("            JUDGE_RESULTS.add(formatCase(\"")
                    .append(escapeJava(tc.displayInput()))
                    .append("\", \"")
                    .append(escapeJava(expectedStr))
                    .append("\", throwable.getClass().getSimpleName() + \": \" + String.valueOf(throwable.getMessage()), false));\n");
            builder.append("        }\n");
        }

        builder.append("        writeResults();\n");
        builder.append("        if (hasRuntimeError) {\n");
        builder.append("            System.err.println(\"Runtime error occurred while executing test cases.\");\n");
        builder.append("            System.exit(2);\n");
        builder.append("        }\n");
        builder.append("    }\n\n");
        appendHelpers(builder);
        builder.append("}\n");
        return builder.toString();
    }

    // ── Validation ────────────────────────────────────────────────────────────

    static void validateSpec(JudgeSpecData spec) {
        // 1. Param type whitelist
        for (ParamSpec p : spec.params()) {
            if (!ALLOWED_TYPES.contains(p.type())) {
                throw new IllegalArgumentException(
                        "Unsupported parameter type '" + p.type() + "' for param '" + p.name() +
                        "'. Allowed: " + ALLOWED_TYPES);
            }
        }

        // 2. Return type whitelist (void is allowed separately)
        if (!"void".equals(spec.returnType()) && !ALLOWED_TYPES.contains(spec.returnType())) {
            throw new IllegalArgumentException(
                    "Unsupported return type '" + spec.returnType() + "'. Allowed: " + ALLOWED_TYPES);
        }

        // 3. Fix-2: void + output_target strictness
        if ("void".equals(spec.returnType())) {
            boolean hasExplicitTarget = spec.outputTarget() != null && !spec.outputTarget().isBlank();
            if (!hasExplicitTarget) {
                boolean isBuiltin = "builtin".equals(spec.problemSource()) || spec.problemSource() == null;
                if (!isBuiltin) {
                    throw new IllegalArgumentException(
                            "void method '" + spec.methodName() + "' in non-builtin problem must set output_target explicitly");
                }
                // builtin: fall back to first int[] param, but warn
                String fallback = firstIntArrayParam(spec);
                if (fallback == null) {
                    throw new IllegalArgumentException(
                            "void method '" + spec.methodName() + "' requires output_target or at least one int[] parameter");
                }
                log.warn("void method '{}': output_target not set, falling back to first int[] param '{}'. " +
                         "Set output_target explicitly to avoid this warning.", spec.methodName(), fallback);
            }
        }

        // 4. Fix-3: FLOAT_TOLERANCE only for double/float return types
        if (spec.comparisonStrategy() == ComparisonStrategy.FLOAT_TOLERANCE) {
            if (!FLOAT_TOLERANCE_ALLOWED_RETURN_TYPES.contains(spec.returnType())) {
                throw new IllegalArgumentException(
                        "FLOAT_TOLERANCE requires returnType 'double' or 'float', got: '" +
                        spec.returnType() + "'");
            }
        }

        // 5. Fix-1: UNORDERED only for numeric array return types
        if (spec.comparisonStrategy() == ComparisonStrategy.UNORDERED) {
            if (!UNORDERED_ALLOWED_RETURN_TYPES.contains(spec.returnType())) {
                throw new IllegalArgumentException(
                        "UNORDERED comparison is only supported for numeric array return types " +
                        UNORDERED_ALLOWED_RETURN_TYPES + ", got: '" + spec.returnType() + "'");
            }
        }
    }

    // ── Code building helpers ─────────────────────────────────────────────────

    private static String buildSetupCode(List<InputValue> inputs) {
        StringBuilder sb = new StringBuilder();
        for (InputValue input : inputs) {
            sb.append(renderDeclaration(input.name(), input.type(), input.value())).append('\n');
        }
        return sb.toString().stripTrailing();
    }

    private static String renderDeclaration(String name, String type, JsonNode value) {
        return switch (type) {
            case "int"     -> "int " + name + " = " + value.asInt() + ";";
            case "long"    -> "long " + name + " = " + value.asLong() + "L;";
            case "double"  -> "double " + name + " = " + value.asDouble() + ";";
            case "boolean" -> "boolean " + name + " = " + value.asBoolean() + ";";
            case "String"  -> "String " + name + " = \"" + escapeJava(value.asText()) + "\";";
            case "int[]"   -> "int[] " + name + " = new int[]{" + joinIntArray(value) + "};";
            case "long[]"  -> "long[] " + name + " = new long[]{" + joinLongArray(value) + "};";
            case "double[]"-> "double[] " + name + " = new double[]{" + joinDoubleArray(value) + "};";
            case "String[]"-> "String[] " + name + " = new String[]{" + joinStringArray(value) + "};";
            default -> throw new IllegalArgumentException("Cannot render declaration for type: " + type);
        };
    }

    private static String buildExecutionCode(JudgeSpecData spec, List<InputValue> inputs) {
        String paramList = inputs.stream().map(InputValue::name).collect(Collectors.joining(", "));
        return switch (spec.returnType()) {
            case "void" -> {
                String outputParam = resolveOutputTarget(spec);
                yield "solution." + spec.methodName() + "(" + paramList + ");\n" +
                      "String actual = Arrays.toString(" + outputParam + ");";
            }
            case "int[]" -> "int[] _result = solution." + spec.methodName() + "(" + paramList + ");\n" +
                            "String actual = Arrays.toString(_result);";
            case "long[]" -> "long[] _result = solution." + spec.methodName() + "(" + paramList + ");\n" +
                             "String actual = Arrays.toString(_result);";
            case "double[]" -> "double[] _result = solution." + spec.methodName() + "(" + paramList + ");\n" +
                               "String actual = Arrays.toString(_result);";
            case "String[]" -> "String[] _result = solution." + spec.methodName() + "(" + paramList + ");\n" +
                               "String actual = Arrays.toString(_result);";
            case "int"     -> "String actual = String.valueOf(solution." + spec.methodName() + "(" + paramList + "));";
            case "long"    -> "String actual = String.valueOf(solution." + spec.methodName() + "(" + paramList + "));";
            case "double"  -> "String actual = String.valueOf(solution." + spec.methodName() + "(" + paramList + "));";
            case "boolean" -> "String actual = String.valueOf(solution." + spec.methodName() + "(" + paramList + "));";
            case "String"  -> "String actual = solution." + spec.methodName() + "(" + paramList + ");";
            case "List<Integer>", "List<String>", "List<List<Integer>>" ->
                    "String actual = String.valueOf(solution." + spec.methodName() + "(" + paramList + "));";
            default -> throw new IllegalArgumentException("Unsupported return type: " + spec.returnType());
        };
    }

    private static String buildExpectedStr(JudgeSpecData spec, JsonNode value) {
        return switch (spec.returnType()) {
            case "int", "long", "boolean", "double", "String" -> value.asText();
            case "int[]", "void" -> "[" + joinIntArray(value) + "]";
            case "long[]"  -> "[" + joinLongArray(value) + "]";
            case "double[]" -> "[" + joinDoubleArray(value) + "]";
            case "String[]" -> "[" + joinStringArrayValues(value) + "]";
            case "List<List<Integer>>" -> nestedIntListToString(value);
            case "List<Integer>"       -> intListToString(value);
            case "List<String>"        -> stringListToString(value);
            default -> throw new IllegalArgumentException("Unsupported return type: " + spec.returnType());
        };
    }

    private static String buildComparisonCode(JudgeSpecData spec, String expectedStr) {
        return switch (spec.comparisonStrategy()) {
            case EXACT ->
                    "\"" + escapeJava(expectedStr) + "\".equals(actual)";
            case TEXT_NORMALIZE ->
                    "normalizeText(actual).equals(normalizeText(\"" + escapeJava(expectedStr) + "\"))";
            case UNORDERED ->
                    "sortedTokens(actual).equals(sortedTokens(\"" + escapeJava(expectedStr) + "\"))";
            case FLOAT_TOLERANCE -> {
                double epsilon = spec.comparisonOptions() != null && spec.comparisonOptions().epsilon() != null
                        ? spec.comparisonOptions().epsilon() : 1e-6;
                yield "Math.abs(Double.parseDouble(actual) - Double.parseDouble(\"" +
                      escapeJava(expectedStr) + "\")) <= " + epsilon;
            }
            case CUSTOM ->
                    throw new UnsupportedOperationException("CUSTOM comparison strategy is not yet implemented");
        };
    }

    // ── Import inference ──────────────────────────────────────────────────────

    private static boolean needsArraysImport(JudgeSpecData spec) {
        String rt = spec.returnType();
        if ("void".equals(rt) || rt.endsWith("[]")) return true;
        return spec.params().stream().anyMatch(p -> p.type().endsWith("[]"));
    }

    private static boolean needsListImport(JudgeSpecData spec) {
        return spec.returnType().startsWith("List");
    }

    // ── void output_target resolution ─────────────────────────────────────────

    private static String resolveOutputTarget(JudgeSpecData spec) {
        if (spec.outputTarget() != null && !spec.outputTarget().isBlank()) {
            return spec.outputTarget();
        }
        return firstIntArrayParam(spec);
    }

    private static String firstIntArrayParam(JudgeSpecData spec) {
        return spec.params().stream()
                .filter(p -> p.type().equals("int[]"))
                .map(ParamSpec::name)
                .findFirst()
                .orElse(null);
    }

    // ── JSON → Java literal helpers ───────────────────────────────────────────

    private static String joinIntArray(JsonNode arrayNode) {
        List<String> parts = new ArrayList<>();
        for (JsonNode n : arrayNode) parts.add(String.valueOf(n.asInt()));
        return String.join(", ", parts);
    }

    private static String joinLongArray(JsonNode arrayNode) {
        List<String> parts = new ArrayList<>();
        for (JsonNode n : arrayNode) parts.add(n.asLong() + "L");
        return String.join(", ", parts);
    }

    private static String joinDoubleArray(JsonNode arrayNode) {
        List<String> parts = new ArrayList<>();
        for (JsonNode n : arrayNode) parts.add(String.valueOf(n.asDouble()));
        return String.join(", ", parts);
    }

    private static String joinStringArray(JsonNode arrayNode) {
        List<String> parts = new ArrayList<>();
        for (JsonNode n : arrayNode) parts.add("\"" + escapeJava(n.asText()) + "\"");
        return String.join(", ", parts);
    }

    private static String joinStringArrayValues(JsonNode arrayNode) {
        // For expected string: matches Arrays.toString(String[]) format (no quotes)
        List<String> parts = new ArrayList<>();
        for (JsonNode n : arrayNode) parts.add(n.asText());
        return String.join(", ", parts);
    }

    private static String intListToString(JsonNode arrayNode) {
        List<String> parts = new ArrayList<>();
        for (JsonNode n : arrayNode) parts.add(String.valueOf(n.asInt()));
        return "[" + String.join(", ", parts) + "]";
    }

    private static String stringListToString(JsonNode arrayNode) {
        List<String> parts = new ArrayList<>();
        for (JsonNode n : arrayNode) parts.add(n.asText());
        return "[" + String.join(", ", parts) + "]";
    }

    private static String nestedIntListToString(JsonNode outerNode) {
        List<String> rows = new ArrayList<>();
        for (JsonNode inner : outerNode) rows.add(intListToString(inner));
        return "[" + String.join(", ", rows) + "]";
    }

    // ── Shared helpers appended to every generated Main.java ─────────────────

    private static void appendHelpers(StringBuilder builder) {
        builder.append("    private static String formatCase(String input, String expected, String actual, boolean passed) {\n");
        builder.append("        return \"CASE_RESULT|\" + safe(input) + \"|\" + safe(expected) + \"|\" + safe(actual) + \"|\" + passed;\n");
        builder.append("    }\n\n");
        builder.append("    private static void writeResults() throws Exception {\n");
        builder.append("        String resultPath = System.getProperty(\"judge.result.file\", \"/tmp/judge-result/result.txt\");\n");
        builder.append("        Path path = Path.of(resultPath);\n");
        builder.append("        if (path.getParent() != null) Files.createDirectories(path.getParent());\n");
        builder.append("        StringBuilder sb = new StringBuilder();\n");
        builder.append("        for (String line : JUDGE_RESULTS) { sb.append(line).append('\\n'); }\n");
        builder.append("        Files.writeString(path, sb.toString(), StandardCharsets.UTF_8);\n");
        builder.append("    }\n\n");

        builder.append("    private static String normalizeText(String value) {\n");
        builder.append("        if (value == null) return \"\";\n");
        builder.append("        return value.replace(\"\\r\\n\", \"\\n\").trim();\n");
        builder.append("    }\n\n");

        // Fix-1: numeric sort instead of lexicographic sort
        builder.append("    private static String sortedTokens(String value) {\n");
        builder.append("        if (value == null) return \"\";\n");
        builder.append("        String stripped = value.replaceAll(\"[\\\\[\\\\]]\", \"\").trim();\n");
        builder.append("        if (stripped.isEmpty()) return \"[]\";\n");
        builder.append("        String[] parts = stripped.split(\",\\\\s*\");\n");
        builder.append("        java.util.Arrays.sort(parts,\n");
        builder.append("            (a, b) -> Long.compare(Long.parseLong(a.trim()), Long.parseLong(b.trim())));\n");
        builder.append("        return java.util.Arrays.toString(parts);\n");
        builder.append("    }\n\n");

        builder.append("    private static String safe(String value) {\n");
        builder.append("        if (value == null) return \"null\";\n");
        builder.append("        return value.replace(\"\\\\\", \"\\\\\\\\\").replace(\"|\", \"\\\\|\").replace(\"\\n\", \"\\\\n\").replace(\"\\r\", \"\\\\r\");\n");
        builder.append("    }\n");
    }

    private static void appendIndented(StringBuilder builder, String block, int spaces) {
        String indent = " ".repeat(spaces);
        for (String line : block.split("\\n")) {
            builder.append(indent).append(line).append('\n');
        }
    }

    static String escapeJava(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }
}
