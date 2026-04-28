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
public final class TemperatureViewModel_Factory implements Factory<TemperatureViewModel> {
  @Override
  public TemperatureViewModel get() {
    return newInstance();
  }

  public static TemperatureViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static TemperatureViewModel newInstance() {
    return new TemperatureViewModel();
  }

  private static final class InstanceHolder {
    private static final TemperatureViewModel_Factory INSTANCE = new TemperatureViewModel_Factory();
  }
}
