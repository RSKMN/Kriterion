# Backend Code Quality Tooling (Recommended)

This backend currently focuses on foundation setup. For Java formatting and linting, use one of the options below in future phases:

## Option 1: Spotless + Google Java Format

Recommended for consistent automated formatting.

Maven plugin to add in `pom.xml` (example):

```xml
<plugin>
  <groupId>com.diffplug.spotless</groupId>
  <artifactId>spotless-maven-plugin</artifactId>
  <version>2.44.2</version>
  <configuration>
    <java>
      <googleJavaFormat>
        <version>1.24.0</version>
      </googleJavaFormat>
    </java>
  </configuration>
</plugin>
```

## Option 2: Checkstyle

Recommended for structural and style rules in CI.

Typical setup:

- `maven-checkstyle-plugin` in `pom.xml`
- a project-level `checkstyle.xml`

## Suggested CI order

1. `mvn -q -DskipTests compile`
2. `mvn spotless:check` (if enabled)
3. `mvn checkstyle:check` (if enabled)
