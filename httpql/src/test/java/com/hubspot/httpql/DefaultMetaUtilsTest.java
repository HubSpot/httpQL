package com.hubspot.httpql;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.hubspot.rosetta.annotations.RosettaNaming;
import org.junit.Test;

public class DefaultMetaUtilsTest {

  @Test
  public void itCorrectlyDetectsAndConvertsToSnakeCase() {
    assertThat(DefaultMetaUtils.convertToSnakeCaseIfSupported("ACamelCase", test1.class))
      .isEqualTo("a_camel_case");
    /*
     * LowerCaseWithUnderscoresStrategy has been removed in later version of jackson, commenting out for compatibility
     * assertThat(DefaultMetaUtils.convertToSnakeCaseIfSupported("ACamelCase", test2.class))
     * .isEqualTo("a_camel_case");
     */
    assertThat(
      DefaultMetaUtils.convertToSnakeCaseIfSupported(
        "ACamelCase",
        DefaultMetaUtilsTest.class
      )
    )
      .isEqualTo("ACamelCase");
  }

  @RosettaNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public static class test1 {}
  /*
   * @RosettaNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
   * public static class test2 {}
   */
}
