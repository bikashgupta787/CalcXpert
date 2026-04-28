package com.smartcalc.viewmodel;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class BasicCalculatorViewModel_Factory implements Factory<BasicCalculatorViewModel> {
  @Override
  public BasicCalculatorViewModel get() {
    return newInstance();
  }

  public static BasicCalculatorViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static BasicCalculatorViewModel newInstance() {
    return new BasicCalculatorViewModel();
  }

  private static final class InstanceHolder {
    private static final BasicCalculatorViewModel_Factory INSTANCE = new BasicCalculatorViewModel_Factory();
  }
}
