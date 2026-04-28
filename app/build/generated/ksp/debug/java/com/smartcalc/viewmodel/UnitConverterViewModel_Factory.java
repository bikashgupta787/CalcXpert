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
public final class UnitConverterViewModel_Factory implements Factory<UnitConverterViewModel> {
  @Override
  public UnitConverterViewModel get() {
    return newInstance();
  }

  public static UnitConverterViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static UnitConverterViewModel newInstance() {
    return new UnitConverterViewModel();
  }

  private static final class InstanceHolder {
    private static final UnitConverterViewModel_Factory INSTANCE = new UnitConverterViewModel_Factory();
  }
}
