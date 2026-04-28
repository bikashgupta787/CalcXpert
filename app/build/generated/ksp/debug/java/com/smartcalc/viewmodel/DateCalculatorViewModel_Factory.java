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
public final class DateCalculatorViewModel_Factory implements Factory<DateCalculatorViewModel> {
  @Override
  public DateCalculatorViewModel get() {
    return newInstance();
  }

  public static DateCalculatorViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static DateCalculatorViewModel newInstance() {
    return new DateCalculatorViewModel();
  }

  private static final class InstanceHolder {
    private static final DateCalculatorViewModel_Factory INSTANCE = new DateCalculatorViewModel_Factory();
  }
}
